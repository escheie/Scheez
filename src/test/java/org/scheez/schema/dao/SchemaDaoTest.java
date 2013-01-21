package org.scheez.schema.dao;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.scheez.schema.def.ColumnType;
import org.scheez.schema.objects.Column;
import org.scheez.schema.objects.Table;
import org.scheez.schema.objects.TableName;

public abstract class SchemaDaoTest
{
    private SchemaDao schemaDao;
    
    protected abstract SchemaDao initSchemaDao();
    
    protected abstract ColumnType getExpectedColumnType (ColumnType columnType);
    
    @Before
    public void setUp ()
    {
        schemaDao = initSchemaDao();
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
            assertEquals(getExpectedColumnType(types[index++]), column.getType());
        }
        
        schemaDao.dropTable(tableName);
        assertNull(schemaDao.getTable(tableName));
        
        schemaDao.createTable(table);
        assertNotNull(schemaDao.getTable(tableName));
        
        schemaDao.dropSchema(tableName.getSchemaName());
        
        assertNull(schemaDao.getTable(tableName));
        assertFalse(schemaDao.schemaExists(tableName.getSchemaName()));
    }
}
