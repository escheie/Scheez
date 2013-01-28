package org.scheez.test.db;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class DataSourceUtil
{  
    public static DriverManagerDataSource getMysqlDataSource ()
    {
        return new DriverManagerDataSource("jdbc:mysql://localhost/scheez", "scheez", "scheez");
    } 
    
    public static DriverManagerDataSource getPostgresqlDataSource ()
    {
        return new DriverManagerDataSource("jdbc:postgresql://localhost/scheez", "postgres", "dbc");
    }
    
    public static DriverManagerDataSource getHsqldbDataSource ()
    {
        return new DriverManagerDataSource("jdbc:hsqldb:mem:testdb", "SA", "");
    }
}
