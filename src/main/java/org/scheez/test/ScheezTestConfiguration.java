package org.scheez.test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ScheezTestConfiguration
{
    private static final Log log = LogFactory.getLog(ScheezTestConfiguration.class);
    
    public static final String PROPERTY_DATABASES = "test.databases";
    
    public static final String PROPERTY_FACTORY = "factory";
    
    private static ScheezTestConfiguration config;
    
    private List<TestDatabase> testDatabases;

    public synchronized static ScheezTestConfiguration getInstance()
    {
        if (config == null)
        {
            String propertiesFile = System.getProperty("scheeztest.properties", "classpath:scheeztest.properties");
            config = new ScheezTestConfiguration();
            try
            {
                config.load(propertiesFile);
            }
            catch (Exception e)
            {
                throw new RuntimeException("Problem with Scheez test properties file: " + propertiesFile, e);
            }
        }
        return config;
    }

    private ScheezTestConfiguration()
    {
        testDatabases = new LinkedList<TestDatabase>();
    }
    
    public List<TestDatabase> getTestDatabases ()
    {
        return Collections.unmodifiableList(testDatabases);
    }

    public void load (String resourceName) throws Exception
    {
        log.info("Loading Test Database properties from " + resourceName + ".");
        
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
            
            String factory = p.getProperty(PROPERTY_FACTORY, false, true);
            if(factory == null)
            {
                factory = DefaultTestDatabaseFactory.class.getName();
            }
            
            @SuppressWarnings("unchecked")
            Class<TestDatabaseFactory> cls = (Class<TestDatabaseFactory>)Class.forName(factory);
            TestDatabaseFactory testDatabaseFactory = cls.newInstance();
            
            testDatabases.add(testDatabaseFactory.getTestDatabase(database, p));        
        }
    }
    
    
}
