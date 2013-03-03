package org.scheez.schema.diff;

import org.scheez.reflect.PersistentField;
import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.model.Column;
import org.scheez.schema.model.Table;

public class MismatchedColumnPrecision extends SchemaDifferenceColumn
{

    public MismatchedColumnPrecision(Table table, Column existingColumn, Column expectedColumn, PersistentField field)
    {
        super(table, existingColumn, expectedColumn, field.getField().getDeclaringClass(), field);
    }

    @Override
    public Type getType()
    {
        return Type.MISMATCHED_COLUMN_PRECISION;
    }

    @Override
    public String getDescription()
    {
        return "Column \"" + existingColumn.getName() + "\" of table \"" + table.getTableName()
                + "\" has precision (" + existingColumn.getPrecision() + ", " + existingColumn.getScale() + ") but the expected precision is ("
                + expectedColumn.getPrecision() + ", " + expectedColumn.getScale()  + ").";
    }

    @Override
    public void resolveDifference(SchemaDao schemaDao)
    {
        schemaDao.alterColumnType(table.getTableName(), expectedColumn);
    }

}
