package org.scheez.test;

import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scheez.schema.model.TableName;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class DefaultTestDatabase implements TestDatabase
{
    protected Log log = LogFactory.getLog(getClass());

    public static String PROPERTY_URL = "url";

    public static String PROPERTY_DRIVER_CLASS = "driverClass";

    public static String PROPERTY_USERNAME = "username";

    public static String PROPERTY_PASSWORD = "password";

    public static final String PROPERTY_TEST_SQL = "testSql";

    public static final String DEFAULT_TEST_SQL = "SELECT CURRENT_TIMESTAMP";

    private static final int MAX_RETRY = 10;

    private static final int RETRY_WAIT = 10000;

    protected String name;

    protected String url;

    protected String driverClass;

    protected String username;

    protected String password;

    protected String testSql;

    private SingleConnectionDataSource dataSource;

    /**
     * @param name
     */
    public DefaultTestDatabase(String name)
    {
        this.name = name;
        testSql = DEFAULT_TEST_SQL;
    }

    protected void initializeFromProperties(TestDatabaseProperties properties)
    {
        url = properties.getProperty(PROPERTY_URL, true, true);
        driverClass = properties.getProperty(PROPERTY_DRIVER_CLASS, false, true);
        username = properties.getProperty(PROPERTY_USERNAME, false, true);
        password = properties.getProperty(PROPERTY_PASSWORD, false, false);
        testSql = properties.getProperty(PROPERTY_TEST_SQL, DEFAULT_TEST_SQL);

        if (driverClass != null)
        {
            try
            {
                Class.forName(driverClass);
            }
            catch (ClassNotFoundException e)
            {
                throw new RuntimeException("Unable to find driver class: " + driverClass, e);
            }
        }
    }

    /**
     * @return the url
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * @param url
     *            the url to set
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    /**
     * @return the driverClass
     */
    public String getDriverClass()
    {
        return driverClass;
    }

    /**
     * @param driverClass
     *            the driverClass to set
     */
    public void setDriverClass(String driverClass)
    {
        this.driverClass = driverClass;
    }

    /**
     * @return the username
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * @param username
     *            the username to set
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * @return the testSql
     */
    public String getTestSql()
    {
        return testSql;
    }

    /**
     * @param testSql
     *            the testSql to set
     */
    public void setTestSql(String testSql)
    {
        this.testSql = testSql;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * 
     */
    public DataSource getDataSource()
    {
        DataSource dataSource = initializeDataSource(false);

        int retryCount = 0;
        RuntimeException ex = null;
        while (retryCount++ <= MAX_RETRY)
        {
            if (ex != null)
            {
                log.warn(name + " - Unable to connect to database: " + ex.getMessage());
                log.info(name + " - Reinitializing DataSource after short delay...");
                try
                {
                    Thread.sleep(RETRY_WAIT);
                }
                catch (InterruptedException e)
                {
                    log.warn(e);
                }
                dataSource = initializeDataSource(true);
            }

            try
            {
                JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
                long time = System.currentTimeMillis();
                log.info(name + " - Verifying database connection...");
                String value = jdbcTemplate.queryForObject(testSql, String.class);
                log.info(name + " - Verified database connection.  Test Sql: " + testSql
                        + ",  Result: " + value
                        + ",  Duration: " + (System.currentTimeMillis() - time) / 1000f + "s");
                return dataSource;
            }
            catch (RuntimeException e)
            {
                ex = e;
            }
        }
        throw ex;
    }

    protected synchronized DataSource initializeDataSource(boolean reinitialize)
    {
        if ((dataSource == null) || (reinitialize))
        {
            if (dataSource != null)
            {
                dataSource.destroy();
            }
            dataSource = new SingleConnectionDataSource(url, username, password, true);
        }
        return dataSource;
    }

    @Override
    public void close()
    {
        dataSource.destroy();
    }

    public List<TableName> getSystemTableNames()
    {
        List<TableName> tableNames = new LinkedList<TableName>();
        tableNames.add(new TableName("INFORMATION_SCHEMA", "TABLES"));
        return tableNames;
    }

    /**
     * @inheritDoc
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof DefaultTestDatabase))
        {
            return false;
        }
        DefaultTestDatabase other = (DefaultTestDatabase) obj;
        if (name == null)
        {
            if (other.name != null)
            {
                return false;
            }
        }
        else if (!name.equals(other.name))
        {
            return false;
        }
        return true;
    }

    public String toString()
    {
        return name;
    }
}
