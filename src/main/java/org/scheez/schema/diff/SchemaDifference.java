package org.scheez.schema.diff;

public interface SchemaDifference
{
    enum Type 
    {
        TABLE_MISSING,
        
        EXTRA_TABLE,
        
        TABLE_NAME_CHANGE,
        
        COLUMN_MISSING,
        
        EXTRA_COLUMN,
        
        COLUMN_NAME_CHANGE,
        
        COLUMN_TYPE_CHANGE,
        
        MISSING_FOREIGN_KEY,
        
        EXTRA_FOREIGN_KEY,
        
        MISSING_CONSTRAINT,
        
        EXTRA_CONSTRAINT,
        
        MISSING_INDEX,
        
        EXTRA_INDEX
    }
    
    Type getType ();
    
    String getMessage ();
}
