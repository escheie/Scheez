package org.scheez.schema.manger;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.dao.impl.SchemaDaoFactoryUrl;
import org.scheez.schema.def.ColumnType;
import org.scheez.schema.diff.MissingColumn;
import org.scheez.schema.diff.MissingTable;
import org.scheez.schema.diff.SchemaDifference;
import org.scheez.schema.diff.SchemaDifference.Type;
import org.scheez.schema.diff.UnknownColumn;
import org.scheez.schema.diff.UnknownTable;
import org.scheez.schema.objects.Column;
import org.scheez.schema.objects.TableName;
import org.scheez.test.db.TestDatabase;
import org.scheez.test.db.TestDatabaseManager;
import org.scheez.test.schema.Person;

@RunWith(Parameterized.class)
public class BasicSchemaManagerTest
{
    private static final Log log = LogFactory.getLog(BasicSchemaManagerTest.class);
    
    private static final String TEST_SCHEMA = "scheez_test";

    private SchemaDao schemaDao;

    public BasicSchemaManagerTest(TestDatabase testDatabase)
    {
        schemaDao = new SchemaDaoFactoryUrl(testDatabase.getUrl(), testDatabase.getDataSource()).getSchemaDao();
    }
   
    @Before
    public void setup ()
    { 
        if(schemaDao.schemaExists(TEST_SCHEMA))
        {
            schemaDao.dropSchema(TEST_SCHEMA);
        }
        schemaDao.createSchema(TEST_SCHEMA);
    }

    /**
     * Test method for
     * {@link org.scheez.schema.manger.BasicSchemaManager#findDifferences()}.
     */
    @Test
    public void testMissingAndUnknownTable()
    {
        SchemaClasses classes = new SchemaClasses();
        classes.include(Person.class);
        
        BasicSchemaManager schemaManager = new BasicSchemaManager(TEST_SCHEMA, schemaDao, classes);
        List<SchemaDifference> differences = schemaManager.findDifferences();
        
        assertNotNull(differences);
        assertEquals(1, differences.size());
        
        MissingTable missingTable = (MissingTable)differences.get(0);
        
        log.info(missingTable);
        assertEquals(Type.MISSING_TABLE, missingTable.getType());
        assertEquals(Person.class, missingTable.getTableClass());
        assertNotNull(missingTable.getTable());
        assertEquals("persons", missingTable.getTable().getName());
        assertNotNull(missingTable.getDescription());
        
        schemaManager.resolveDifferences(differences);
        
        differences = schemaManager.findDifferences();
        
        assertNotNull(differences);
        assertEquals(0, differences.size());
        
        classes.exclude(Person.class);
        schemaManager = new BasicSchemaManager(TEST_SCHEMA, schemaDao, classes);
        differences = schemaManager.findDifferences();
        
        assertNotNull(differences);
        assertEquals(1, differences.size());
        
        UnknownTable unknownTable = (UnknownTable)differences.get(0);
        
        log.info(unknownTable);
        assertEquals(Type.UNKNOWN_TABLE, unknownTable.getType());
        assertNull(unknownTable.getTableClass());
        assertNotNull(unknownTable.getTable());
        assertNotNull(unknownTable.getDescription());
        
        schemaManager.resolveDifferences(differences);
        
        differences = schemaManager.findDifferences();
        
        assertNotNull(differences);
        assertEquals(0, differences.size());
    }
    
    /**
     * Test method for
     * {@link org.scheez.schema.manger.BasicSchemaManager#findDifferences()}.
     */
    @Test
    public void testMissingAndUnknownColumns()
    {
        SchemaClasses classes = new SchemaClasses();
        classes.include(Person.class);
        
        BasicSchemaManager schemaManager = new BasicSchemaManager(TEST_SCHEMA, schemaDao, classes);
        installSchema(schemaManager);
        
        TableName tableName = new TableName (TEST_SCHEMA, "persons");
        
        String droppedColumn = "first_name";
        schemaDao.dropColumn(tableName, droppedColumn);
        
        List<SchemaDifference> differences = schemaManager.findDifferences();
        
        assertNotNull(differences);
        assertEquals(1, differences.size());
        
        MissingColumn missingColumn = (MissingColumn)differences.get(0);
        
        log.info(missingColumn);
        assertEquals(Type.MISSING_COLUMN, missingColumn.getType());
        assertEquals(Person.class, missingColumn.getTableClass());
        assertNotNull(missingColumn.getTable());
        assertNotNull(missingColumn.getColumn());
        assertEquals(droppedColumn, missingColumn.getColumn().getName());
        assertNotNull(missingColumn.getDescription());
        assertNotNull(missingColumn.getField());
        assertEquals("firstName", missingColumn.getField().getName());
        
        schemaManager.resolveDifferences(differences);
        
        differences = schemaManager.findDifferences();
        
        assertNotNull(differences);
        assertEquals(0, differences.size());
        
        Column newColumn = new Column ("unknown", ColumnType.VARCHAR, 256);
        schemaDao.addColumn(tableName, newColumn);
        
        differences = schemaManager.findDifferences();
        
        assertNotNull(differences);
        assertEquals(1, differences.size());
        
        UnknownColumn unknownColumn = (UnknownColumn)differences.get(0);
        
        log.info(unknownColumn);
        assertEquals(Type.UNKNOWN_COLUMN, unknownColumn.getType());
        assertEquals(Person.class, unknownColumn.getTableClass());
        assertNotNull(unknownColumn.getTable());
        assertNotNull(unknownColumn.getColumn());
        assertNull(unknownColumn.getField());
        assertNotNull(unknownColumn.getDescription());
        assertEquals(newColumn.getName(), newColumn.getName());
        assertEquals(newColumn.getType(), newColumn.getType());
        
        schemaManager.resolveDifferences(differences);
        
        differences = schemaManager.findDifferences();
        
        assertNotNull(differences);
        assertEquals(0, differences.size());
        
    }
    
    private void installSchema (SchemaManager schemaManager)
    {
        List<SchemaDifference> differences = schemaManager.findDifferences();
        assertNotNull(differences);
        schemaManager.resolveDifferences(differences);
        differences = schemaManager.findDifferences();
        assertNotNull(differences);
        assertEquals(0, differences.size());
    }
    
    @Parameters (name="{0}")
    public static Collection<Object[]> testDatabases ()
    {
        return TestDatabaseManager.getInstance().getDatabaseParameters();
    } 
}
