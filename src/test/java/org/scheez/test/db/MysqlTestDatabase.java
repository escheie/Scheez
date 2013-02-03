package org.scheez.test.db;

import javax.sql.DataSource;

public class MysqlTestDatabase extends AbstractTestDatabase
{

    public MysqlTestDatabase(String url, DataSource dataSource)
    {
        super("mysql", url, dataSource);
    }
}
