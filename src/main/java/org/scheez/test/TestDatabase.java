package org.scheez.test;

import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import org.scheez.schema.parts.TableName;

public class TestDatabase
{
    protected DataSource dataSource;

    protected String name;

    public TestDatabase(String name, DataSource dataSource)
    {
        this.name = name;
        this.dataSource = dataSource;
    }

    public String getName()
    {
        return name;
    }
    
    public DataSource getDataSource()
    {
        return dataSource;
    }
    
    public List<TableName> getSystemTableNames()
    {
        List<TableName> tableNames = new LinkedList<TableName>();
        tableNames.add(new TableName("INFORMATION_SCHEMA", "TABLES"));
        return tableNames;
    }
    
    public String toString ()
    {
        return name;
    }
}
