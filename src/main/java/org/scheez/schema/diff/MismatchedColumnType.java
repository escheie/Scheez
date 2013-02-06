package org.scheez.schema.diff;

import org.scheez.reflect.PersistentField;
import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.parts.Column;
import org.scheez.schema.parts.Table;

public class MismatchedColumnType extends SchemaDifferenceColumn
{

    public MismatchedColumnType(Table table, Column existingColumn, Column expectedColumn, PersistentField field)
    {
        super(table, existingColumn, expectedColumn, field.getField().getDeclaringClass(), field);
    }

    @Override
    public Type getType()
    {
        return Type.MISMATCHED_COLUMN_TYPE;
    }

    @Override
    public String getDescription()
    {
        return "Column \"" + existingColumn.getName() + "\" of table \"" + table.getTableName()
                + "\" has type " + existingColumn.getType().name() + " but the expected type is "
                + expectedColumn.getType().name() + ".";
    }

    @Override
    public void resolveDifference(SchemaDao schemaDao)
    {
        schemaDao.alterColumnType(table.getTableName(), expectedColumn);
    }

}