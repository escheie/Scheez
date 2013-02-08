package org.scheez.schema.parts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.scheez.util.BaseObject;

public class Table extends BaseObject
{
    private TableName name;

    private List<Column> columns;

    private List<Index> indexes;

    public Table(TableName name)
    {
        this.name = name;
        this.columns = new ArrayList<Column>();
        this.indexes = new ArrayList<Index>();
    }

    public String getSchemaName()
    {
        return name.getSchemaName();
    }

    public String getName()
    {
        return name.getTableName();
    }

    public TableName getTableName()
    {
        return name;
    }

    public List<Column> getColumns()
    {
        return Collections.unmodifiableList(columns);
    }

    public void addColumn(Column column)
    {
        columns.add(column);
    }

    public void setColumns(Collection<Column> columns)
    {
        this.columns.clear();
        this.columns.addAll(columns);
    }

    public List<Index> getIndexes()
    {
        return Collections.unmodifiableList(indexes);
    }
    
    public void addIndex (Index index)
    {
        this.indexes.add(index);
    }

    public void setIndexes (Collection<Index> indexes)
    {
        this.indexes.clear();
        this.indexes.addAll(indexes);
    }

}
