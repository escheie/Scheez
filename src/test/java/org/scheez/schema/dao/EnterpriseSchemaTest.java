package org.scheez.schema.dao;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.scheez.schema.model.Column;
import org.scheez.schema.model.ForeignKey;
import org.scheez.schema.model.Index;
import org.scheez.schema.model.Table;
import org.scheez.schema.model.TableName;
import org.scheez.test.TestDatabase;
import org.scheez.test.jpa.EnterpriseSchema;
import org.scheez.test.junit.ScheezTestDatabase;


@RunWith(ScheezTestDatabase.class)
public class EnterpriseSchemaTest 
{ 
    private SchemaDao schemaDao;
    
    public EnterpriseSchemaTest (TestDatabase testDatabase)
    {
        schemaDao = SchemaDaoFactory.getSchemaDao(testDatabase.getDataSource());
        EnterpriseSchema.getInstance().init(testDatabase);
    }
    
    @Test
    public void testUniqueness ()
    {
        Table jobTable = schemaDao.getTable(new TableName(EnterpriseSchema.SCHEMA, EnterpriseSchema.TABLE_DEPARTMENT));
        assertNotNull(jobTable);
        
        for(Index index : jobTable.getIndexes())
        {
            System.out.println(index);
        }
    }
    
    @Test
    public void testNullable ()
    {
        Table jobTable = schemaDao.getTable(new TableName(EnterpriseSchema.SCHEMA, EnterpriseSchema.TABLE_JOB));
        assertNotNull(jobTable);
        
        for(Column column : jobTable.getColumns())
        {
            if(!column.getName().equalsIgnoreCase(EnterpriseSchema.COLUMN_ID))
            {
                assertFalse(column.isNullable());
            }
        }
    }
        
    @Test
    public void testKeys ()
    {
        List<Table> tables = schemaDao.getTables(EnterpriseSchema.SCHEMA);
        assertEquals(EnterpriseSchema.TABLE_COUNT, tables.size());
        
        for(Table table : tables)
        {
            assertNotNull(table.getPrimaryKey());
            assertNotNull(table.getPrimaryKey().getKeyName());
            assertNotNull(table.getPrimaryKey().getColumnNames());
            assertEquals(1, table.getPrimaryKey().getColumnNames().size());
            assertTrue(table.getPrimaryKey().getColumnNames().get(0).equalsIgnoreCase(EnterpriseSchema.COLUMN_ID));
            
            int count = 0;
            if(table.getName().equalsIgnoreCase(EnterpriseSchema.TABLE_EMPLOYEE))
            {
                assertEquals(3, table.getForeignKeys().size());
                for (ForeignKey fk : table.getForeignKeys())
                {
                    assertNotNull(fk.getKeyName());
                    assertNotNull(fk.getReferencedPrimaryKey());
                    assertEquals(1, fk.getColumnNames().size());
                    assertEquals(1, fk.getReferencedPrimaryKey().getColumnNames().size());
                    
                    if(fk.getColumnNames().get(0).equalsIgnoreCase(EnterpriseSchema.COLUMN_DEPARTMENT_ID))
                    {
                        assertTrue(fk.getReferencedPrimaryKey().getTableName().getTableName().equalsIgnoreCase(EnterpriseSchema.TABLE_DEPARTMENT));
                        assertTrue(fk.getReferencedPrimaryKey().getColumnNames().get(0).equalsIgnoreCase(EnterpriseSchema.COLUMN_ID));
                        count++;
                    }
                    else if(fk.getColumnNames().get(0).equalsIgnoreCase(EnterpriseSchema.COLUMN_MANAGER_ID))
                    {
                        assertTrue(fk.getReferencedPrimaryKey().getTableName().getTableName().equalsIgnoreCase(EnterpriseSchema.TABLE_EMPLOYEE));
                        assertTrue(fk.getReferencedPrimaryKey().getColumnNames().get(0).equalsIgnoreCase(EnterpriseSchema.COLUMN_ID));
                        count++;
                    }
                    else if(fk.getColumnNames().get(0).equalsIgnoreCase(EnterpriseSchema.COLUMN_JOB_ID))
                    {
                        assertTrue(fk.getReferencedPrimaryKey().getTableName().getTableName().equalsIgnoreCase(EnterpriseSchema.TABLE_JOB));
                        assertTrue(fk.getReferencedPrimaryKey().getColumnNames().get(0).equalsIgnoreCase(EnterpriseSchema.COLUMN_ID));
                        count++;
                    }
                    
                }
                assertEquals(3, count);
            }
            else if(table.getName().equalsIgnoreCase(EnterpriseSchema.TABLE_JOB))
            {
                assertEquals(0, table.getForeignKeys().size());
            }
            else if(table.getName().equalsIgnoreCase(EnterpriseSchema.TABLE_DEPARTMENT))
            {
                assertEquals(1, table.getForeignKeys().size());
                for (ForeignKey fk : table.getForeignKeys())
                {
                    assertNotNull(fk.getKeyName());
                    assertNotNull(fk.getReferencedPrimaryKey());
                    assertEquals(1, fk.getColumnNames().size());
                    assertEquals(1, fk.getReferencedPrimaryKey().getColumnNames().size());
                    
                    if(fk.getColumnNames().get(0).equalsIgnoreCase(EnterpriseSchema.COLUMN_MANAGER_ID))
                    {
                        assertTrue(fk.getReferencedPrimaryKey().getTableName().getTableName().equalsIgnoreCase(EnterpriseSchema.TABLE_EMPLOYEE));
                        assertTrue(fk.getReferencedPrimaryKey().getColumnNames().get(0).equalsIgnoreCase(EnterpriseSchema.COLUMN_ID));
                        count++;
                    }                   
                }
                assertEquals(1, count);
            }
        }
    }
}
