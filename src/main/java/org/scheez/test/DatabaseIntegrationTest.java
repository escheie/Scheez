package org.scheez.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parallelized.class)
public abstract class DatabaseIntegrationTest
{
    @Parameters(name = "{0}")
    public static Collection<Object[]> testDatabases()
    {
        List<Object[]> databases = new ArrayList<Object[]>();
        for (TestDatabase database : TestConfiguration.getInstance().getTestDatabases())
        {
            databases.add(new Object[] { database });
        }
        return databases;
    }
}
