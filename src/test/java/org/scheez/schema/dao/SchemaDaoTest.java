package org.scheez.schema.dao;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.scheez.schema.dao.impl.SchemaDaoFactoryUrl;
import org.scheez.schema.def.ColumnType;
import org.scheez.schema.parts.Column;
import org.scheez.schema.parts.Table;
import org.scheez.schema.parts.TableName;
import org.scheez.test.db.TestDatabase;
import org.scheez.test.db.TestDatabaseManager;

@RunWith(Parameterized.class)
public class SchemaDaoTest
{
    private static final String TEST_SCHEMA = "scheez_test";
    
    private TestDatabase testDatabase;
    
    private SchemaDao schemaDao;
    
    public SchemaDaoTest (TestDatabase testDatabase)
    {
        this.testDatabase = testDatabase;
        schemaDao = new SchemaDaoFactoryUrl(testDatabase.getUrl(), testDatabase.getDataSource()).getSchemaDao();
    }
    
    @Before
    public void setUp ()
    {
        if(schemaDao.schemaExists(TEST_SCHEMA))
        {
            schemaDao.dropSchema(TEST_SCHEMA);
            assertFalse(schemaDao.schemaExists(TEST_SCHEMA));
        }
        schemaDao.createSchema(TEST_SCHEMA);
        assertTrue(schemaDao.schemaExists(TEST_SCHEMA));
    }
    
    @Test
    public void testSchemaMethods ()
    { 
        boolean foundTestSchema = false;
        List<String> schemas = schemaDao.getSchemas();
        for (String schemaName : schemas)
        {
            assertNotNull(schemaName);
            if(schemaName.equalsIgnoreCase(TEST_SCHEMA))
            {
                foundTestSchema = true;
            }
        }
        assertTrue(foundTestSchema);
        assertTrue(schemaDao.schemaExists(TEST_SCHEMA));
        
        schemaDao.dropSchema(TEST_SCHEMA);
        assertFalse(schemaDao.schemaExists(TEST_SCHEMA));
        
        int schemaCount = schemas.size();
        schemas = schemaDao.getSchemas();
        assertEquals(schemaCount - 1, schemas.size());
        foundTestSchema = false;
        for (String schemaName : schemas)
        {
            assertNotNull(schemaName);
            if(schemaName.equalsIgnoreCase(TEST_SCHEMA))
            {
                foundTestSchema = true;
            }
        }
        assertFalse(foundTestSchema);
    }
    
    @Test
    public void testTableMethods ()
    {   
        TableName tableName = new TableName (TEST_SCHEMA, "table1");
        Table table = new Table(tableName);
        
        assertEquals (0, schemaDao.getTables(tableName.getSchemaName()).size());
        assertNull (schemaDao.getTable(tableName));
        
        ColumnType[] types = ColumnType.values();
        for(int index = 0; index < types.length; index++)
        {
            table.addColumn(new Column("col" + (index + 1), types[index],  (types[index] == ColumnType.VARCHAR) ? 256 : null));
        } 
        schemaDao.createTable(table);
        
        Table table2 = schemaDao.getTable(tableName);
        assertNotNull(table2);
        assertEquals(table2.getColumns().size(), types.length);
        int index = 0;
        for (Column column : table2.getColumns())
        {
            assertNotNull(column.getName());
            assertEquals(testDatabase.getExpectedColumnType(types[index++]), column.getType());
        }
        
        assertEquals (1, schemaDao.getTables(tableName.getSchemaName()).size());
        
        schemaDao.dropTable(tableName);
        assertNull(schemaDao.getTable(tableName));
        
        schemaDao.createTable(table);
        assertNotNull(schemaDao.getTable(tableName));
    }
    
    @Test
    public void testColumnMethods ()
    {
        TableName tableName = new TableName (TEST_SCHEMA, "table1");
        Table table = new Table(tableName);
        table.addColumn(new Column("id", ColumnType.INTEGER));
        
        assertEquals (0, schemaDao.getTables(tableName.getSchemaName()).size());
        assertNull (schemaDao.getTable(tableName));
        
        schemaDao.createTable(table);
        
        ColumnType[] types = ColumnType.values();
        for(int index = 0; index < types.length; index++)
        {
            schemaDao.addColumn(tableName, new Column("col" + (index + 1), types[index]));
        } 
        
        Table table2 = schemaDao.getTable(tableName);
        assertNotNull(table2);
        assertEquals(table2.getColumns().size(), types.length + 1);
        int index = 0;
        for (Column column : table2.getColumns())
        {
            if(column.getName().equalsIgnoreCase("id"))
            {
                continue;
            }
            
            assertNotNull(column.getName());
            assertEquals(testDatabase.getExpectedColumnType(types[index++]), column.getType());
            
            assertNotNull(schemaDao.getColumn(tableName, column.getName()));
        }
    }
    
    @Parameters (name="{0}")
    public static Collection<Object[]> testDatabases ()
    {
        return TestDatabaseManager.getInstance().getDatabaseParameters();
    }
}
