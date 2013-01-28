package org.scheez.test.db;

import javax.sql.DataSource;

import org.scheez.schema.def.ColumnType;

public class HsqldbTestDatabase extends AbstractTestDatabase
{
    public HsqldbTestDatabase(String url, DataSource dataSource)
    {
        super("hsqldb", url, dataSource);
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
