package org.scheez.test.util;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class DataSourceUtil
{  
    public static DataSource getMysqlDataSource ()
    {
        return new DriverManagerDataSource("jdbc:mysql://localhost/scheez", "scheez", "scheez");
    } 
    
    public static DataSource getPostgresqlDataSource ()
    {
        return new DriverManagerDataSource("jdbc:postgresql://localhost/scheez", "postgres", "dbc");
    }
    
    public static DataSource getHsqldbDataSource ()
    {
        return new DriverManagerDataSource("jdbc:hsqldb:mem:testdb", "SA", "");
    }
}
