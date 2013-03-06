package org.scheez.test;

import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;
import javax.naming.spi.NamingManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mock.jndi.SimpleNamingContext;

public class ScheezTestConfiguration implements InitialContextFactoryBuilder
{
    private static final Log log = LogFactory.getLog(ScheezTestConfiguration.class);
    
    public static final String PROPERTY_DATABASES = "test.databases";
    
    public static final String PROPERTY_FACTORY = "factory";
    
    private static ScheezTestConfiguration config;
    
    private List<TestDatabase> testDatabases;
    
    private Hashtable<String, Object> jndiObjects;

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
        jndiObjects = new Hashtable<String, Object>();
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
        
        log.info("Test Databases: " + databases);
        
        StringTokenizer tokenizer = new StringTokenizer(databases, ", \t\n", false);
        if(!tokenizer.hasMoreTokens())
        {
            throw new IllegalArgumentException("Missing value for property: " + PROPERTY_DATABASES);
        }
        
        NamingManager.setInitialContextFactoryBuilder(this);
        
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
            
            TestDatabase testdb = testDatabaseFactory.getTestDatabase(database, p);
            testDatabases.add(testDatabaseFactory.getTestDatabase(database, p));
            
            String jndiName = "jndi:jdbc/" + database + "/DataSource";
            
            log.info("Binding test DataSource to JNDI context: " + jndiName);
            jndiObjects.put(jndiName, new TestDataSourceProxy(testdb));
        }
    }

    /** 
     * @inheritDoc
     */
    @Override
    public InitialContextFactory createInitialContextFactory(Hashtable<?, ?> environment)
            throws NamingException
    {
        return new InitialContextFactory() {
            public Context getInitialContext(Hashtable<?, ?> environment) {
                return new SimpleNamingContext("", jndiObjects, environment)
                {
                    /** 
                     * @inheritDoc
                     */
                    @Override
                    public NameParser getNameParser(String name) throws NamingException
                    {
                        // TODO Auto-generated method stub
                        return new NameParser ()
                        {
                            /** 
                             * @inheritDoc
                             */
                            @Override
                            public Name parse(String name) throws NamingException
                            {
                                return new CompositeName(name);
                            }
                            
                        };
                    }

                    /** 
                     * @inheritDoc
                     */
                    @Override
                    public Object lookup(Name name) throws NamingException
                    {
                        return super.lookup(name.toString());
                    }
                };
            }
        };
    }
}
