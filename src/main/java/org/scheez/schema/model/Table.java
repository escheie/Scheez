package org.scheez.schema.model;

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

    private Key primaryKey;

    private List<ForeignKey> foreignKeys;

    public Table(TableName name)
    {
        this.name = name;
        this.columns = new ArrayList<Column>();
        this.indexes = new ArrayList<Index>();
        this.foreignKeys = new ArrayList<ForeignKey>();
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

    public void addIndex(Index index)
    {
        this.indexes.add(index);
    }

    public void setIndexes(Collection<Index> indexes)
    {
        this.indexes.clear();
        this.indexes.addAll(indexes);
    }

    /**
     * @return the primaryKey
     */
    public Key getPrimaryKey()
    {
        return primaryKey;
    }

    /**
     * @param primaryKey
     *            the primaryKey to set
     */
    public void setPrimaryKey(Key primaryKey)
    {
        this.primaryKey = primaryKey;
    }

    /**
     * @return the foreignKeys
     */
    public List<ForeignKey> getForeignKeys()
    {
        return foreignKeys;
    }

    /**
     * @param foreignKeys
     *            the foreignKeys to set
     */
    public void setForeignKeys(List<ForeignKey> foreignKeys)
    {
        this.foreignKeys = foreignKeys;
    }

    public void addForeignKey(ForeignKey foreignKey)
    {
        this.foreignKeys.add(foreignKey);
    }

}
