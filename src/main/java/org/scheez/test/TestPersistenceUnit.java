/*
 * Copyright (C) 2013 by Teradata Corporation. All Rights Reserved. TERADATA CORPORATION
 * CONFIDENTIAL AND TRADE SECRET
 */
package org.scheez.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author es151000
 * @version $Id: $
 */
public class TestPersistenceUnit
{
    private Log log = LogFactory.getLog(getClass());

    private Map<String, EntityManagerFactoryWrapper> entityManagerFactory;

    private String persistenceUnitName;

    private String jndiDataSourceName;

    private Properties properties;

    public TestPersistenceUnit(String persistenceUnitName, String jndiDataSourceName)
    {
        this.persistenceUnitName = persistenceUnitName;
        this.jndiDataSourceName = jndiDataSourceName;
        entityManagerFactory = new HashMap<String, EntityManagerFactoryWrapper>();
        properties = new Properties();
    }

    /**
     * @return the persistenceUnitName
     */
    public String getPersistenceUnitName()
    {
        return persistenceUnitName;
    }

    /**
     * @return the jndiDataSourceName
     */
    public String getJndiDataSourceName()
    {
        return jndiDataSourceName;
    }

    /**
     * @return the properties
     */
    public Properties getProperties()
    {
        return properties;
    }

    public EntityManagerFactory getEntityManagerFactory(TestDatabase testDatabase)
    {
        EntityManagerFactoryWrapper factory = null;
        synchronized (this)
        {
            factory = entityManagerFactory.get(testDatabase.getName());
            if (factory == null)
            {
                factory = new EntityManagerFactoryWrapper(testDatabase);
                entityManagerFactory.put(testDatabase.getName(), factory);
            }
        }
        return factory.get();
    }

    protected void setUp(TestDatabase testDatabase)
    {

    }

    protected void load(TestDatabase testDatabase, EntityManagerFactory factory)
    {

    }

    private class EntityManagerFactoryWrapper
    {
        private TestDatabase testDatabase;

        private EntityManagerFactory factory;

        public EntityManagerFactoryWrapper(TestDatabase testDatabase)
        {
            this.testDatabase = testDatabase;
        }

        public synchronized EntityManagerFactory get()
        {
            if (factory == null)
            {
                factory = initFactory();
            }
            return factory;
        }

        /**
         * 
         */
        private EntityManagerFactory initFactory()
        {
            setUp(testDatabase);
            log.info("Creating new EntityManagerFactory.  Database: " + testDatabase.getName() + ", PersistenceUnit: " + persistenceUnitName);
           
            ScheezTestConfiguration.getInstance().resetThreadLocalJndiObjects()
                    .put(jndiDataSourceName, testDatabase.getDataSource());
            EntityManagerFactory factory = Persistence.createEntityManagerFactory(persistenceUnitName, properties);
            load(testDatabase, factory);
            return factory;
        }
    }
}
