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
import org.scheez.schema.objects.Column;
import org.scheez.schema.objects.Table;
import org.scheez.schema.objects.TableName;
import org.scheez.test.db.TestDatabase;
import org.scheez.test.db.TestDatabaseManager;

@RunWith(Parameterized.class)
public class SchemaDaoTest
{
    private TestDatabase testDatabase;
    
    private SchemaDaoFactory schemaDaoFactory;
    
    public SchemaDaoTest (TestDatabase testDatabase)
    {
        this.testDatabase = testDatabase;
    }
    
    @Before
    public void setUp ()
    {
        schemaDaoFactory = new SchemaDaoFactoryUrl(testDatabase.getUrl(), testDatabase.getDataSource());
    }
    
    @Test
    public void testSchemaDao ()
    {
        SchemaDao schemaDao = schemaDaoFactory.getSchemaDao();
        
        TableName tableName = new TableName("schema1", "table1");
        Table table = new Table(tableName);
    
        if(schemaDao.schemaExists(tableName.getSchemaName()))
        {
            schemaDao.dropSchema("schema1");
        }
        
        List<String> schemas = schemaDao.getSchemas();
        for (String schemaName : schemas)
        {
            assertNotNull(schemaName);
        }
        
        assertFalse(schemaDao.schemaExists(tableName.getSchemaName()));
        assertNull(schemaDao.getTable(tableName));
        
        
        schemaDao.createSchema(tableName.getSchemaName());
        assertTrue(schemaDao.schemaExists(tableName.getSchemaName()));
        
        assertEquals (0, schemaDao.getTables(tableName.getSchemaName()).size());
        
        int schemaCount = schemas.size();
        schemas = schemaDao.getSchemas();
        assertEquals(schemaCount + 1, schemas.size());
        for (String schemaName : schemas)
        {
            assertNotNull(schemaName);
        }
        
        ColumnType[] types = ColumnType.values();
        for(int index = 0; index < types.length; index++)
        {
            table.addColumn(new Column("col" + (index + 1), types[index],  (types[index] == ColumnType.VARCHAR) ? 256 : null));
        } 
        schemaDao.createTable(table);
        
        Table table2 = schemaDao.getTable(tableName);
        assertNotNull(table2);
        assertEquals(table.getColumns().size(), types.length);
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
        
        schemaDao.dropSchema(tableName.getSchemaName());
        
        assertNull(schemaDao.getTable(tableName));
        assertFalse(schemaDao.schemaExists(tableName.getSchemaName()));
    }
    
    @Parameters (name="{0}")
    public static Collection<Object[]> testDatabases ()
    {
        return TestDatabaseManager.getInstance().getDatabaseParameters();
    }
}
