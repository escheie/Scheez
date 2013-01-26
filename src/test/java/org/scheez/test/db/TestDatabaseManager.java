package org.scheez.test.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


public class TestDatabaseManager
{
    private static TestDatabaseManager databaseManager;
    
    public synchronized static TestDatabaseManager getInstance ()
    {
        if(databaseManager == null)
        {
            databaseManager = new TestDatabaseManager();
        }
        return databaseManager;
    }
    
    private List<TestDatabase> testDatabases;
    
    private TestDatabaseManager ()
    {
        testDatabases = new LinkedList<TestDatabase>();
        testDatabases.add(new HsqldbTestDatabase(DataSourceUtil.getHsqldbDataSource()));
        testDatabases.add(new MysqlTestDatabase(DataSourceUtil.getMysqlDataSource()));
        testDatabases.add(new PostgresqlTestDatabase(DataSourceUtil.getPostgresqlDataSource()));
    }
    
    public Collection<Object[]> getDatabaseParameters()
    {
        List<Object[]> databases = new ArrayList<Object[]>();
        for (TestDatabase database : testDatabases)
        {
            databases.add(new Object[] { database });
        }
        return databases;
    }
}
