package org.scheez.dao;

import org.junit.Before;
import org.junit.Test;
import org.scheez.dao.ansi.SchemaDaoAnsi;
import org.scheez.ddl.Column;
import org.scheez.ddl.Table;
import org.scheez.ddl.Column.Type;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class SchemaDaoTest
{
    private SchemaDao schemaDao;
    
    @Before
    public void setUp ()
    {
        DriverManagerDataSource dataSource = new DriverManagerDataSource("jdbc:hsqldb:mem:testdb", "SA", "");
        
        schemaDao = new SchemaDaoAnsi(dataSource);
        //crudDao = new CrudDaoAnsi(dataSource);
        
    }
    
    @Test
    public void test()
    {
        Table table = new Table("table1");
        int columnNumber = 1;
        for(Column.Type type : Column.Type.values())
        {
            if(type != Type.USER_DEFINED)
            {
                table.addColumn(new Column("col" + columnNumber++, type,  (type == Type.VARCHAR) ? 256 : null));
            }
        } 
        schemaDao.createTable(table);
    }
}
