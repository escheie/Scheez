package org.scheez.test.db;

import javax.sql.DataSource;

import org.scheez.schema.def.ColumnType;

public class PostgresqlTestDatabase extends AbstractTestDatabase
{
    public PostgresqlTestDatabase(String url, DataSource dataSource)
    {
        super("postgresql", url, dataSource);
    }

    @Override
    public ColumnType getExpectedColumnType(ColumnType columnType)
    {
        if(columnType == ColumnType.TINYINT)
        {
            columnType = ColumnType.SMALLINT;
        }
        else if (columnType == ColumnType.FLOAT)
        {
            columnType = ColumnType.DOUBLE;
        }
        return columnType;
    }
}
