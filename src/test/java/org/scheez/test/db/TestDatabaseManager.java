package org.scheez.test.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.springframework.jdbc.datasource.DriverManagerDataSource;


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
        DriverManagerDataSource dataSource = DataSourceUtil.getHsqldbDataSource();
        testDatabases.add(new HsqldbTestDatabase(dataSource.getUrl(), dataSource));
        dataSource = DataSourceUtil.getMysqlDataSource();
        testDatabases.add(new MysqlTestDatabase(dataSource.getUrl(), dataSource));
        dataSource = DataSourceUtil.getPostgresqlDataSource();
        testDatabases.add(new PostgresqlTestDatabase(dataSource.getUrl(), dataSource));
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
