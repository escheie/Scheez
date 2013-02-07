package org.scheez.test.db;

import javax.sql.DataSource;

public class MysqlTestDatabase extends AbstractTestDatabase
{

    public MysqlTestDatabase(DataSource dataSource)
    {
        super("mysql", dataSource);
    }
}
