package org.scheez.test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class TestConfiguration
{
    public static final String PROPERTY_DATABASES = "databases";
    
    private static TestConfiguration config;
    
    private List<TestDatabase> testDatabases;

    public synchronized static TestConfiguration getInstance()
    {
        if (config == null)
        {
            config = new TestConfiguration();
            try
            {
                config.load(System.getProperty("database.properties", "classpath:database.properties"));
            }
            catch (Exception e)
            {
                throw new RuntimeException("Unable to load test database properties.", e);
            }
        }
        return config;
    }

    private TestConfiguration()
    {
        testDatabases = new LinkedList<TestDatabase>();
    }
    
    public List<TestDatabase> getTestDatabases ()
    {
        return Collections.unmodifiableList(testDatabases);
    }

    public void load (String resourceName) throws Exception
    {
        testDatabases.clear();
        TestDatabaseProperties properties = TestDatabaseProperties.load(resourceName);   
        String databases = properties.getProperty(PROPERTY_DATABASES, true, true);
        
        StringTokenizer tokenizer = new StringTokenizer(databases, ", \t\n", false);
        if(!tokenizer.hasMoreTokens())
        {
            throw new IllegalArgumentException("Missing value for property: databases");
        }
       
        while(tokenizer.hasMoreTokens())
        {
            String database = tokenizer.nextToken();
            TestDatabaseProperties p = properties.withPrefix(database);
            
            String type = p.getProperty("type", false, true);
            if(type == null)
            {
                type = SimpleTestDatabase.class.getName();
            }
            
            @SuppressWarnings("unchecked")
            Class<TestDatabase> cls = (Class<TestDatabase>)Class.forName(type);
            TestDatabase testDatabase = cls.newInstance();
            testDatabase.initialize(database, p);
            
            testDatabases.add(testDatabase);        
        }
    }
    
    
}
