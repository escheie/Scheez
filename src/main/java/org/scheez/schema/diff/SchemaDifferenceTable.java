package org.scheez.schema.diff;

import org.scheez.schema.model.Table;
import org.scheez.util.BaseObject;

public abstract class SchemaDifferenceTable extends BaseObject implements SchemaDifference
{
    protected Table table;

    protected Class<?> tableClass;

    public SchemaDifferenceTable(Table table, Class<?> tableClass)
    {
        this.table = table;
        this.tableClass = tableClass;
    }

    @Override
    public Table getTable()
    {
        return table;
    }

    @Override
    public Class<?> getTableClass()
    {
        return tableClass;
    }

    public String toString()
    {
        return getType().name() + ": " + getDescription();
    }
}
