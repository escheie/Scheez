package org.scheez.schema.diff;

import org.scheez.reflect.PersistentField;
import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.model.Column;
import org.scheez.schema.model.Table;

public class MismatchedColumnLength extends SchemaDifferenceColumn
{

    public MismatchedColumnLength(Table table, Column existingColumn, Column expectedColumn, PersistentField field)
    {
        super(table, existingColumn, expectedColumn, field.getField().getDeclaringClass(), field);
    }

    @Override
    public Type getType()
    {
        return Type.MISMATCHED_COLUMN_LENGTH;
    }

    @Override
    public String getDescription()
    {
        return "Column \"" + expectedColumn.getName() + "\" of table \"" + table.getTableName() + "\" has length "
                + existingColumn.getLength() + " but the expected length is " + expectedColumn.getLength() + ".";
    }

    @Override
    public void reconcileDifferences(SchemaDao schemaDao)
    {
        schemaDao.alterColumn(table.getTableName(), expectedColumn);
    }

}
