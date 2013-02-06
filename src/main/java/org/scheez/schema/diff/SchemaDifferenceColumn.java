package org.scheez.schema.diff;

import org.scheez.reflect.PersistentField;
import org.scheez.schema.parts.Column;
import org.scheez.schema.parts.Table;

public abstract class SchemaDifferenceColumn extends SchemaDifferenceTable
{
    protected PersistentField field;

    protected Column column;

    public SchemaDifferenceColumn(Table table, Class<?> cls, Column column, PersistentField field)
    {
        super(table, cls);
        this.column = column;
        this.field = field;
    }

    public PersistentField getField()
    {
        return field;
    }

    public Column getColumn()
    {
        return column;
    }

}
