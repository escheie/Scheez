package org.scheez.test;

/**
 * @author es151000
 * @version $Id: $
 */
public class DefaultTestDatabaseFactory implements TestDatabaseFactory
{
    /**
     * @inheritDoc
     */
    @Override
    public TestDatabase getTestDatabase (String name, TestDatabaseProperties properties)
    {
        DefaultTestDatabase database = new DefaultTestDatabase (name);
        database.initializeFromProperties(properties);
        return database;
    }

 }
