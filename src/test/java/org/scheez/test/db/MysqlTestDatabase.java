package org.scheez.test.db;

import javax.sql.DataSource;

import org.scheez.schema.def.ColumnType;

public class MysqlTestDatabase extends AbstractTestDatabase
{

    public MysqlTestDatabase(String url, DataSource dataSource)
    {
        super("mysql", url, dataSource);
    }

    @Override
    public ColumnType getExpectedColumnType(ColumnType columnType)
    {
        if (columnType == ColumnType.FLOAT)
        {
            columnType = ColumnType.DOUBLE;
        }
        return columnType;
    }  
}
