package org.scheez.schema.diff;

import org.scheez.schema.objects.Table;
import org.scheez.util.BaseObject;

public abstract class AbstractSchemaDifference extends BaseObject implements SchemaDifference
{
    private Table table;
    
    public AbstractSchemaDifference ()
    {
        
    }

    public AbstractSchemaDifference(Table table)
    {
        this.table = table;
    }

    @Override
    public String getTableName()
    {
        return table.getName();
    }
    
    public Table getTable ()
    {
        return table;
    }

    public String toString()
    {
        return getType().name() + ": " + getMessage();
    }
}
