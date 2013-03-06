package org.scheez.schema.dao;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.scheez.schema.def.ColumnType;
import org.scheez.schema.model.Column;
import org.scheez.schema.model.Index;
import org.scheez.schema.model.Table;
import org.scheez.schema.model.TableName;
import org.scheez.test.TestDatabase;
import org.scheez.test.junit.ScheezTestDatabase;

@RunWith(ScheezTestDatabase.class)
public class SchemaDaoTest 
{
    private static final String TEST_SCHEMA = "scheez_test";
    
    private SchemaDao schemaDao;
    
    public SchemaDaoTest (TestDatabase testDatabase)
    {
        schemaDao = SchemaDaoFactory.getSchemaDao(testDatabase.getDataSource());
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
            table.addColumn(new Column("col" + index, types[index]));
        } 
        schemaDao.createTable(table);
        
        Table table2 = schemaDao.getTable(tableName);
        assertNotNull(table2);
        assertEquals(types.length, table2.getColumns().size());
        for (Column column : table2.getColumns())
        {
            assertNotNull(column.getName());
            int index = Integer.parseInt(column.getName().substring(3));
            assertEquals(schemaDao.getExpectedColumnType(types[index]), column.getType());
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
            schemaDao.addColumn(tableName, new Column("col" + index, types[index]));
        } 
        
        Table table2 = schemaDao.getTable(tableName);
        assertNotNull(table2);
        assertEquals( types.length + 1, table2.getColumns().size());
        for (Column column : table2.getColumns())
        {
            if(column.getName().equalsIgnoreCase("id"))
            {
                continue;
            }
            
            assertNotNull(column.getName());
            int index = Integer.parseInt(column.getName().substring(3));
            assertEquals(schemaDao.getExpectedColumnType(types[index]), column.getType());
            
            assertNotNull(schemaDao.getColumn(tableName, column.getName()));
            
            schemaDao.dropColumn(tableName, column.getName());
            
            assertNull(schemaDao.getColumn(tableName, column.getName()));
        }
    }
    
    @Test
    public void testIndexMethods ()
    {
        TableName tableName = new TableName (TEST_SCHEMA, "table1");
        Table table = new Table(tableName);
        table.addColumn(new Column("id", ColumnType.INTEGER));
        
        assertEquals (0, schemaDao.getTables(tableName.getSchemaName()).size());
        assertNull (schemaDao.getTable(tableName));
        
        schemaDao.createTable(table);
        
        List<ColumnType> typeList = new LinkedList<ColumnType>(Arrays.asList(ColumnType.values()));
        typeList.remove(ColumnType.BINARY);
        ColumnType[] types = typeList.toArray(new ColumnType[0]);
      
        for(int index = 0; index < types.length; index++)
        {
            String colName = "col" + index;
            schemaDao.addColumn(tableName, new Column(colName, types[index]));
            schemaDao.addIndex(tableName, new Index("index" + index, colName));   
        } 
        
        Table table2 = schemaDao.getTable(tableName);
        assertNotNull(table2);
        assertEquals(types.length, table2.getIndexes().size());
        for (Index index : table2.getIndexes())
        { 
            assertNotNull(index.getName());
            int i = Integer.parseInt(index.getName().substring(5));
            assertEquals(1, index.getColumnNames().size());
            assertTrue(index.getColumnNames().get(0).equalsIgnoreCase("col" + i));
            
            assertNotNull(schemaDao.getIndex(tableName, index.getName()));
            
            schemaDao.dropIndex(tableName, index.getName());
            
            assertNull(schemaDao.getColumn(tableName, index.getName()));
        }
        
        Index index1 = new Index ("my_test_index1", "col1", "col2", "col3");
        Index index2 = new Index ("my_test_index2", "col4");
        
        schemaDao.addIndex(tableName, index1);
        schemaDao.addIndex(tableName, index2);
        
        table2 = schemaDao.getTable(tableName);
        assertNotNull(table2);
        assertEquals(2, table2.getIndexes().size());
        for (Index index : table2.getIndexes())
        {
            if(index.getColumnNames().size() == 3)
            {
                assertEquals(index1.toLowerCase(), index.toLowerCase());
            }
            else if(index.getColumnNames().size() == 1)
            {
                assertEquals(index2.toLowerCase(), index.toLowerCase());
            }
            else
            {
                fail ("Index of unexpected number of columns");
            }
        }
        
        assertEquals(index1.toLowerCase(), schemaDao.getIndex(tableName, index1.getName()).toLowerCase());
        assertEquals(index2.toLowerCase(), schemaDao.getIndex(tableName, index2.getName()).toLowerCase());
    }
    
    @Test
    public void testVarcharColumnLength ()
    {
        TableName tableName = new TableName (TEST_SCHEMA, "table1");
        Table table = new Table(tableName);
        Column column1 = new Column("string", ColumnType.VARCHAR, 1024);
        table.addColumn(column1);
        
        schemaDao.createTable(table);
        
        Column column2 = schemaDao.getColumn(tableName, column1.getName());
        
        assertNotNull(column2);
        assertEquals(column1.getLength(), column2.getLength());
    }
    
    @Test
    public void testBigDecimalPrecision ()
    {
        TableName tableName = new TableName (TEST_SCHEMA, "table1");
        Table table = new Table(tableName);
        Column column1 = new Column("PRECISION_TEST", ColumnType.DECIMAL);
        column1.setPrecision(10);
        column1.setScale(5);
        table.addColumn(column1);
        
        schemaDao.createTable(table);
        
        Column column2 = schemaDao.getColumn(tableName, column1.getName());
        
        assertNotNull(column2);
        assertEquals(column1.getPrecision(), column2.getPrecision());
        assertEquals(column1.getScale(), column2.getScale());
    }
    
    @Test
    public void testAlterColumnType ()
    {
        TableName tableName = new TableName (TEST_SCHEMA, "table1");
        Table table = new Table(tableName);
        Column column1 = new Column("changable", ColumnType.INTEGER);
        table.addColumn(column1);
        
        schemaDao.createTable(table);
        
        Column column2 = schemaDao.getColumn(tableName, column1.getName());
        
        assertNotNull(column2);
        assertEquals(ColumnType.INTEGER, column2.getType());
        
        column1.setType(ColumnType.BIGINT);
        
        schemaDao.alterColumnType(tableName, column1); 
        
        column2 = schemaDao.getColumn(tableName, column1.getName());
        
        assertNotNull(column2);
        assertEquals(ColumnType.BIGINT, column2.getType());
    }
}
