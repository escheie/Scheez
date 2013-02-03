package org.scheez.schema.diff;

import org.scheez.schema.objects.Column;
import org.scheez.schema.objects.Table;

public class UnknownColumn extends AbstractSchemaDifference
{
    private Column column;

    public UnknownColumn(Table table, Column column)
    {
        super(table);
        this.column = column;
    }

    @Override
    public Type getType()
    {
        return Type.UNKNOWN_COLUMN;
    }

    @Override
    public String getMessage()
    {
        return "Unknown column \"" + column.getName() + "\" on table \"" + getTable().getTableName() + "\".  No matching field found.";
    }

    public Column getColumn()
    {
        return column;
    }

}
