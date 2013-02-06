package org.scheez.schema.diff;

import org.scheez.reflect.PersistentField;
import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.parts.Column;
import org.scheez.schema.parts.Table;

public class MissingColumn extends SchemaDifferenceColumn
{
    public MissingColumn(Table table, Column column, PersistentField field)
    {
        super(table, field.getField().getDeclaringClass(), column, field);
    }

    @Override
    public Type getType()
    {
        return Type.MISSING_COLUMN;
    }

    @Override
    public String getDescription()
    {
        return "Table " + table.getTableName() + " is missing column \"" + column.getName() + "\" for field \"" + field.toString() + "\".";
    }

    @Override
    public void resolveDifference(SchemaDao schemaDao)
    {
        schemaDao.addColumn(table.getTableName(), column);
    }
}
