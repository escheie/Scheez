package org.scheez.schema.diff;

import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.parts.Column;
import org.scheez.schema.parts.Table;

public class UnknownColumn extends SchemaDifferenceColumn
{
    public UnknownColumn(Table table, Column column, Class<?> cls)
    {
        super(table, cls, column, null);
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
        schemaDao.dropColumn(table.getTableName(), column.getName());
    }
}
