package org.scheez.schema.diff;

import org.scheez.schema.objects.Table;

public class UnknownTable extends AbstractSchemaDifference
{
    public UnknownTable(Table table)
    {
        super(table);
    }

    @Override
    public Type getType()
    {
        return Type.UNKNOWN_TABLE;
    }

    @Override
    public String getMessage()
    {
        return "Unknown table \"" + getTable().getTableName() + "\".  No matching class found.";
    }
}
