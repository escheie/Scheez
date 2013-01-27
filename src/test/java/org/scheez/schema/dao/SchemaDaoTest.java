package org.scheez.schema.dao;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.scheez.dao.SchemaDao;
import org.scheez.schema.def.ColumnType;
import org.scheez.schema.objects.Column;
import org.scheez.schema.objects.Table;
import org.scheez.schema.objects.TableName;
import org.scheez.test.db.TestDatabase;
import org.scheez.test.db.TestDatabaseManager;

@RunWith(Parameterized.class)
public abstract class SchemaDaoTest
{
    private TestDatabase testDatabase;
    
    private SchemaDao schemaDao;
    
    public SchemaDaoTest (TestDatabase testDatabase)
    {
        this.testDatabase = testDatabase;
    }
    
    @Test
    public void testSchemaDao ()
    {
        TableName tableName = new TableName("schema1", "table1");
        Table table = new Table(tableName);
    
        if(schemaDao.schemaExists(tableName.getSchemaName()))
        {
            schemaDao.dropSchema("schema1");
        }
        
        assertFalse(schemaDao.schemaExists(tableName.getSchemaName()));
        assertNull(schemaDao.getTable(tableName));
        
        
        schemaDao.createSchema(tableName.getSchemaName());
        assertTrue(schemaDao.schemaExists(tableName.getSchemaName()));
        
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
        
        schemaDao.dropTable(tableName);
        assertNull(schemaDao.getTable(tableName));
        
        schemaDao.createTable(table);
        assertNotNull(schemaDao.getTable(tableName));
        
        schemaDao.dropSchema(tableName.getSchemaName());
        
        assertNull(schemaDao.getTable(tableName));
        assertFalse(schemaDao.schemaExists(tableName.getSchemaName()));
    }
    
    @Parameters (name="{0}")
    public static Collection<Object[]> profiles ()
    {
        return TestDatabaseManager.getInstance().getDatabaseParameters();
    }
}
