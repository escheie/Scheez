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
    
    public static DriverManagerDataSource getTeradataDataSource ()
    {
        try
        {
            Class.forName("com.teradata.jdbc.TeraDriver");
        }
        catch (ClassNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new DriverManagerDataSource("jdbc:teradata://sdll3563.labs.teradata.com", "DBC", "DBC");
    }
}
