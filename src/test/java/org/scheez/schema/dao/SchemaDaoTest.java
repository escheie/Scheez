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
    public void test()
    {
        TableName tableName = new TableName("table1");
        Table table = new Table(tableName);
        
        schemaDao.dropTable(tableName);
        assertNull(schemaDao.getTable(tableName));
        
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
    }
}
