package org.scheez.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class TestDatabaseManager
{
    private static TestDatabaseManager databaseManager;

    public synchronized static TestDatabaseManager getInstance()
    {
        if (databaseManager == null)
        {
            databaseManager = new TestDatabaseManager();
            try
            {
                databaseManager.load("database.properties");
            }
            catch (Exception e)
            {
                throw new RuntimeException("Unable to load test database properties.", e);
            }
        }
        return databaseManager;
    }

    private List<TestDatabase> testDatabases;

    private TestDatabaseManager()
    {
        testDatabases = new LinkedList<TestDatabase>();
    }
    
    public List<TestDatabase> getTestDatabases ()
    {
        return Collections.unmodifiableList(testDatabases);
    }

    public void load(String resourceName) throws IOException, ClassNotFoundException
    {
        testDatabases.clear();
        Properties databaseProperties = new Properties(System.getProperties());
        
        InputStream inputStream = null;
        try
        {
            inputStream = getClass().getClassLoader().getResourceAsStream(resourceName);
            if(inputStream == null)
            {
                throw new RuntimeException ("Resource not found: " + resourceName);
            }
            databaseProperties.load(inputStream);
        }
        finally
        {
            if(inputStream != null)
            {
                inputStream.close();
            }
        }  
        
        StringTokenizer tokenizer = new StringTokenizer(getProperty(databaseProperties, "databases", null, true, true), ", \t\n", false);
        if(!tokenizer.hasMoreTokens())
        {
            throw new IllegalArgumentException("Missing value for property: databases");
        }
       
        while(tokenizer.hasMoreTokens())
        {
            String database = tokenizer.nextToken();
            String url = getProperty(databaseProperties, "url", database, true, true);
            String driverClass = getProperty(databaseProperties, "driverClass", database, false, true);
            String username = getProperty(databaseProperties, "username", database, true, true);
            String password = getProperty(databaseProperties, "password", database, true, false);
            
            if (driverClass != null)
            {
                Class.forName(driverClass);
            }
            
            testDatabases.add(new TestDatabase(database, new DriverManagerDataSource(url, username, password)));
        }
    }

    private String getProperty(Properties properties, String key, String keyPrefix, boolean propertyRequired,
            boolean valueRequired)
    {
        String k = (keyPrefix == null) ? key : keyPrefix + "." + key;
        String v = properties.getProperty(k);
        if ((propertyRequired) && (v == null))
        {
            throw new IllegalArgumentException("Missing property: " + k);
        }
        if (v != null)
        {
            v = v.trim();
            if ((valueRequired) && (v.isEmpty()))
            {
                throw new IllegalArgumentException("Missing value for property: " + k);
            }
        }
        return v;
    }
}
