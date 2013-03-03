package org.scheez.schema.diff;

import org.scheez.reflect.PersistentField;
import org.scheez.schema.model.Column;
import org.scheez.schema.model.Table;

public abstract class SchemaDifferenceColumn extends SchemaDifferenceTable
{
    protected PersistentField field;

    protected Column existingColumn;
    
    protected Column expectedColumn;

    public SchemaDifferenceColumn(Table table, Column existingColumn, Column expectedColumn, Class<?> cls, PersistentField field)
    {
        super(table, cls);
        this.existingColumn = existingColumn;
        this.expectedColumn = expectedColumn;
        this.field = field;
    }

    public PersistentField getField()
    {
        return field;
    }

    public Column getExistingColumn()
    {
        return existingColumn;
    }
    
    public Column getExpectedColumn()
    {
        return expectedColumn;
    }

}
