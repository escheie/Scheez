package org.scheez.ddl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Table
{
    private String schemaName;
    private String name;
    private List<Column> columns;
    private Object extraInfo;

    public Table(String name)
    {
        this(null, name);
    }

    public Table(String schemaName, String name)
    {
        super();
        this.schemaName = schemaName;
        this.name = name;
        this.columns = new ArrayList<Column>();
    }

    public void setSchemaName(String schemaName)
    {
        this.schemaName = schemaName;
    }

    public String getSchemaName()
    {
        return schemaName;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<Column> getColumns()
    {
        return Collections.unmodifiableList(columns);
    }
    
    public void addColumn (Column column)
    {
        columns.add(column);
    }

    public void addColumns(List<Column> columns)
    {
        this.columns.addAll(columns);
    }

    public Object getExtraInfo()
    {
        return extraInfo;
    }

    public void setExtraInfo(Object extraInfo)
    {
        this.extraInfo = extraInfo;
    }
}
