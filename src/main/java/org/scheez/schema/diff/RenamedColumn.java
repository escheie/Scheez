package org.scheez.schema.diff;

import org.scheez.reflect.PersistentField;
import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.model.Column;
import org.scheez.schema.model.Table;

public class RenamedColumn extends SchemaDifferenceColumn
{
    public RenamedColumn(Table table, Column existingColumn, Column expectedColumn, PersistentField field)
    {
        super(table, existingColumn, expectedColumn, field.getField().getDeclaringClass(), field);
    }

    @Override
    public Type getType()
    {
        return Type.RENAMED_COLUMN;
    }

    @Override
    public String getDescription()
    {
        return "Column \"" + existingColumn.getName() + "\" of table \"" + table.getTableName()
        + "\" has been renamed to " + expectedColumn.getName() + ".";
    }

    @Override
    public void reconcileDifferences(SchemaDao schemaDao)
    {
        schemaDao.renameColumn(table.getTableName(), existingColumn.getName(), expectedColumn.getName());
    }

}
