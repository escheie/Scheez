package org.scheez.schema.diff;

import java.lang.reflect.Field;

import org.scheez.schema.objects.Column;
import org.scheez.schema.objects.Table;

public abstract class SchemaDifferenceColumn extends SchemaDifferenceTable
{
    protected Field field;

    protected Column column;

    public SchemaDifferenceColumn(Table table, Class<?> cls, Column column, Field field)
    {
        super(table, cls);
        this.column = column;
        this.field = field;
    }

    public Field getField()
    {
        return field;
    }

    public Column getColumn()
    {
        return column;
    }

}
