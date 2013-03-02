package org.scheez.schema.manger;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.dao.SchemaDaoFactory;
import org.scheez.schema.def.ColumnType;
import org.scheez.schema.diff.MismatchedColumnLength;
import org.scheez.schema.diff.MismatchedColumnPrecision;
import org.scheez.schema.diff.MismatchedColumnType;
import org.scheez.schema.diff.MissingColumn;
import org.scheez.schema.diff.MissingTable;
import org.scheez.schema.diff.SchemaDifference;
import org.scheez.schema.diff.SchemaDifference.Type;
import org.scheez.schema.diff.UnknownColumn;
import org.scheez.schema.diff.UnknownTable;
import org.scheez.schema.parts.Column;
import org.scheez.schema.parts.TableName;
import org.scheez.test.TestDatabase;
import org.scheez.test.junit.Scheez;
import org.scheez.test.schema.Person;

@RunWith (Scheez.class)
public class BasicSchemaManagerTest 
{
    private static final Log log = LogFactory.getLog(BasicSchemaManagerTest.class);
    
    private static final String TEST_SCHEMA = "scheez_test";

    private SchemaDao schemaDao;

    public BasicSchemaManagerTest(TestDatabase testDatabase)
    {
        schemaDao = SchemaDaoFactory.getSchemaDao(testDatabase.getDataSource());
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
        assertNotNull(missingColumn.getExpectedColumn());
        assertEquals(droppedColumn, missingColumn.getExpectedColumn().getName());
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
        assertNotNull(unknownColumn.getExistingColumn());
        assertNull(unknownColumn.getField());
        assertNotNull(unknownColumn.getDescription());
        assertEquals(newColumn.getName(), newColumn.getName());
        assertEquals(newColumn.getType(), newColumn.getType());
        
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
    public void testMismatchedColumnTypes()
    {
        SchemaClasses classes = new SchemaClasses();
        classes.include(Person.class);
        
        BasicSchemaManager schemaManager = new BasicSchemaManager(TEST_SCHEMA, schemaDao, classes);
        installSchema(schemaManager);
        
        TableName tableName = new TableName (TEST_SCHEMA, "persons");
        
        Column column = new Column("first_name", ColumnType.CHAR);
        schemaDao.alterColumnType(tableName, column);
        
        List<SchemaDifference> differences = schemaManager.findDifferences();
        
        assertNotNull(differences);
        assertEquals(1, differences.size());
        
        MismatchedColumnType mismatchedColumnType = (MismatchedColumnType)differences.get(0);
        
        log.info(mismatchedColumnType);
        assertEquals(Type.MISMATCHED_COLUMN_TYPE, mismatchedColumnType.getType());
        assertEquals(Person.class, mismatchedColumnType.getTableClass());
        assertNotNull(mismatchedColumnType.getTable());
        assertNotNull(mismatchedColumnType.getExistingColumn());
        assertNotNull(mismatchedColumnType.getExpectedColumn());
        assertEquals(ColumnType.CHAR, mismatchedColumnType.getExistingColumn().getType());
        assertEquals(ColumnType.VARCHAR, mismatchedColumnType.getExpectedColumn().getType());
        assertNotNull(mismatchedColumnType.getDescription());
        assertNotNull(mismatchedColumnType.getField());
        
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
    public void testMismatchedColumnLength()
    {
        SchemaClasses classes = new SchemaClasses();
        classes.include(Person.class);
        
        BasicSchemaManager schemaManager = new BasicSchemaManager(TEST_SCHEMA, schemaDao, classes);
        installSchema(schemaManager);
        
        TableName tableName = new TableName (TEST_SCHEMA, "persons");
        
        Column column = new Column("first_name", ColumnType.VARCHAR, 128);
        schemaDao.alterColumnType(tableName, column);
        
        List<SchemaDifference> differences = schemaManager.findDifferences();
        
        assertNotNull(differences);
        assertEquals(1, differences.size());
        
        MismatchedColumnLength mismatchedColumnLength = (MismatchedColumnLength)differences.get(0);
        
        log.info(mismatchedColumnLength);
        assertEquals(Type.MISMATCHED_COLUMN_LENGTH, mismatchedColumnLength.getType());
        assertEquals(Person.class, mismatchedColumnLength.getTableClass());
        assertNotNull(mismatchedColumnLength.getTable());
        assertNotNull(mismatchedColumnLength.getExistingColumn());
        assertNotNull(mismatchedColumnLength.getExpectedColumn());
        assertEquals(128, mismatchedColumnLength.getExistingColumn().getLength().intValue());
        assertEquals(1024, mismatchedColumnLength.getExpectedColumn().getLength().intValue());
        assertNotNull(mismatchedColumnLength.getDescription());
        assertNotNull(mismatchedColumnLength.getField());
        
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
    public void testMismatchedColumnPrecision()
    {
        SchemaClasses classes = new SchemaClasses();
        classes.include(Person.class);
        
        BasicSchemaManager schemaManager = new BasicSchemaManager(TEST_SCHEMA, schemaDao, classes);
        installSchema(schemaManager);
        
        TableName tableName = new TableName (TEST_SCHEMA, "persons");
        
        Column column = new Column("iq", ColumnType.DECIMAL);
        column.setPrecision(6);
        column.setScale(4);
        schemaDao.alterColumnType(tableName, column);
        
        List<SchemaDifference> differences = schemaManager.findDifferences();
        
        assertNotNull(differences);
        assertEquals(1, differences.size());
        
        MismatchedColumnPrecision mismatchedColumnPrecision = (MismatchedColumnPrecision)differences.get(0);
        
        log.info(mismatchedColumnPrecision);
        assertEquals(Type.MISMATCHED_COLUMN_PRECISION, mismatchedColumnPrecision.getType());
        assertEquals(Person.class, mismatchedColumnPrecision.getTableClass());
        assertNotNull(mismatchedColumnPrecision.getTable());
        assertNotNull(mismatchedColumnPrecision.getExistingColumn());
        assertNotNull(mismatchedColumnPrecision.getExpectedColumn());
        assertEquals(6, mismatchedColumnPrecision.getExistingColumn().getPrecision().intValue());
        assertEquals(3, mismatchedColumnPrecision.getExpectedColumn().getPrecision().intValue());
        assertEquals(4, mismatchedColumnPrecision.getExistingColumn().getScale().intValue());
        assertEquals(2, mismatchedColumnPrecision.getExpectedColumn().getScale().intValue());
        assertNotNull(mismatchedColumnPrecision.getDescription());
        assertNotNull(mismatchedColumnPrecision.getField());
        
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
}
