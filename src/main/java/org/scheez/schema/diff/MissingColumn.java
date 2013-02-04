package org.scheez.schema.diff;

import java.lang.reflect.Field;

import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.objects.Column;
import org.scheez.schema.objects.Table;

public class MissingColumn extends SchemaDifferenceColumn
{
    public MissingColumn(Table table, Column column, Field field)
    {
        super(table, column, field);
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
        throw new UnsupportedOperationException();
    }
}
