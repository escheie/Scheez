package org.scheez.schema.diff;

import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.model.Column;
import org.scheez.schema.model.Table;

public class UnknownColumn extends SchemaDifferenceColumn
{
    public UnknownColumn(Table table, Column existingColumn, Class<?> cls)
    {
        super(table, existingColumn, null, cls, null);
    }

    @Override
    public Type getType()
    {
        return Type.UNKNOWN_COLUMN;
    }

    @Override
    public String getDescription()
    {
        return "Unknown column \"" + existingColumn.getName() + "\" on table \"" + table.getTableName() + "\".  No matching field found.";
    }

    @Override
    public void reconcileDifferences(SchemaDao schemaDao)
    {
        schemaDao.dropColumn(table.getTableName(), existingColumn.getName());
    }
}
