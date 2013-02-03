package org.scheez.schema.diff;

import java.lang.reflect.Field;

import org.scheez.schema.objects.Table;

public class MissingColumn extends AbstractSchemaDifference
{
    private Field field;

    private String columnName;

    public MissingColumn(Table table, Field field, String columnName)
    {
        super(table);
        this.field = field;
        this.columnName = columnName;
    }

    @Override
    public Type getType()
    {
        return Type.MISSING_COLUMN;
    }

    @Override
    public String getMessage()
    {
        return "Table " + getTable().getTableName() + " is missing column \"" + columnName + "\" for field \"" + field.toString() + "\".";
    }
    
    public Field getField()
    {
        return field;
    }

    public String getColumnName()
    {
        return columnName;
    }

}
