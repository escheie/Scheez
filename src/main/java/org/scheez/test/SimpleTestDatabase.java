package org.scheez.test;

import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import org.scheez.schema.parts.TableName;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class SimpleTestDatabase implements TestDatabase
{
    public static String PROPERTY_URL = "url";
    
    public static String PROPERTY_DRIVER_CLASS = "driverClass";
    
    public static String PROPERTY_USERNAME = "username";
    
    public static String PROPERTY_PASSWORD = "password";
    
    protected String name;
    
    protected String url;
    
    protected String driverClass;
    
    protected String username;
    
    protected String password;
    
    protected TestDatabaseProperties properties;
    
    public void initialize  (String name, TestDatabaseProperties properties) 
    {
        this.name = name;
        this.properties = properties;
        
        url = properties.getProperty(PROPERTY_URL, true, true);
        driverClass = properties.getProperty(PROPERTY_DRIVER_CLASS, false, true);
        username = properties.getProperty(PROPERTY_USERNAME, false, true);
        password = properties.getProperty(PROPERTY_PASSWORD, false, false);
        
        if (driverClass != null)
        {
            try
            {
                Class.forName(driverClass);
            }
            catch (ClassNotFoundException e)
            {
                throw new RuntimeException("Unable to find driver class: " + driverClass, e);
            }
        }
    }

    public String getName()
    {
        return name;
    }
    
    public String getUrl()
    {
        return url;
    }

    public String getDriverClass()
    {
        return driverClass;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public TestDatabaseProperties getProperties()
    {
        return properties;
    }

    public DataSource getDataSource()
    {
        return new DriverManagerDataSource(url, username, password);
    }
    
    public List<TableName> getSystemTableNames()
    {
        List<TableName> tableNames = new LinkedList<TableName>();
        tableNames.add(new TableName("INFORMATION_SCHEMA", "TABLES"));
        return tableNames;
    }

    public String toString ()
    {
        return name;
    }
}
