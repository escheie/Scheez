package org.scheez.schema.diff;

import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.objects.Column;
import org.scheez.schema.objects.Table;

public class UnknownColumn extends SchemaDifferenceColumn
{
    private Column column;

    public UnknownColumn(Table table, Column column)
    {
        super(table, column, null);
    }

    @Override
    public Type getType()
    {
        return Type.UNKNOWN_COLUMN;
    }

    @Override
    public String getDescription()
    {
        return "Unknown column \"" + column.getName() + "\" on table \"" + table.getTableName() + "\".  No matching field found.";
    }

    @Override
    public void resolveDifference(SchemaDao schemaDao)
    {
        throw new UnsupportedOperationException();
    }
}
