package org.scheez.test.ec2;

import org.scheez.test.TestDatabase;
import org.scheez.test.TestDatabaseFactory;
import org.scheez.test.TestDatabaseProperties;


public class Ec2TestDatabaseFactory implements TestDatabaseFactory
{
    /**
     * @inheritDoc
     */
    @Override
    public TestDatabase getTestDatabase (String name, TestDatabaseProperties properties)
    {
        Ec2TestDatabase database = new Ec2TestDatabase (name);
        database.initializeFromProperties(properties);
        return database;
    }

    

}
