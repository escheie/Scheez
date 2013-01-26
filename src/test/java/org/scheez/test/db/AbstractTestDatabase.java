package org.scheez.test.db;

import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import org.scheez.schema.def.ColumnType;
import org.scheez.schema.objects.TableName;

public class AbstractTestDatabase implements TestDatabase
{
    protected DataSource dataSource;
    
    protected String name;

    public AbstractTestDatabase(String name, DataSource dataSource)
    {
        this.name = name;
        this.dataSource = dataSource;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public DataSource getDataSource()
    {
        return dataSource;
    }

    @Override
    public ColumnType getExpectedColumnType(ColumnType columnType)
    {
        return columnType;
    }
    
    @Override
    public List<TableName> getSystemTableNames()
    {
        List<TableName> tableNames = new LinkedList<TableName>();
        tableNames.add(new TableName("INFORMATION_SCHEMA", "TABLES"));
        //tableNames.add(new TableName("INFORMATION_SCHEMA", "SYSTEM_TABLES"));
        return tableNames;
    }
}
