package org.scheez.schema.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.scheez.util.BaseObject;

public class Table extends BaseObject
{
    private TableName name;
    private List<Column> columns;
    private Object extraInfo;

    public Table (TableName name)
    {
        this.name = name;
        this.columns = new ArrayList<Column>();
    }

    public String getSchemaName()
    {
        return name.getSchemaName();
    }

    public String getName()
    {
        return name.getTableName();
    }
    
    public TableName getTableName ()
    {
        return name;
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
