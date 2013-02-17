package org.scheez.test.database;

import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public abstract class DatabaseTest
{
    @Parameters(name = "{0}")
    public static Collection<Object[]> testDatabases()
    {
        return TestDatabaseManager.getInstance().getDatabaseParameters();
    }

}
