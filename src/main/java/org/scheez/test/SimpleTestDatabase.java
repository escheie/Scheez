package org.scheez.test;

import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import org.scheez.schema.parts.TableName;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class SimpleTestDatabase implements TestDatabase
{
    protected String name;
    
    protected String url;
    
    protected String driverClass;
    
    protected String username;
    
    protected String password;
    
    protected TestDatabaseProperties properties;

    protected SimpleTestDatabase(String name, TestDatabaseProperties properties)
    {
        this.name = name;
        this.properties = properties;
    }
    
    public static TestDatabase getInstance (String name, TestDatabaseProperties properties)
    {
        SimpleTestDatabase testDatabase = new SimpleTestDatabase(name,  properties);
        testDatabase.init();
        return testDatabase;
    }
    
    protected void init  () 
    {
        url = properties.getProperty("url", true, true);
        driverClass = properties.getProperty("driverClass", false, true);
        username = properties.getProperty("username", false, true);
        password = properties.getProperty("password", false, false);
        
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
    
    @Override
    public void start(boolean wait)
    {
       // Do nothing.
    }

    @Override
    public void terminate()
    {   
        // Do nothing.
    }

    @Override
    public boolean isOnline()
    {
        return true;
    }

    public String toString ()
    {
        return name;
    }
}
