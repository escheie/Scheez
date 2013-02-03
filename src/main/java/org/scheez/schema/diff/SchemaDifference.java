package org.scheez.schema.diff;

public interface SchemaDifference
{
    enum Type 
    {
        MISSING_TABLE,
        
        MISSING_COLUMN,
        
        UNKNOWN_TABLE,
        
        UNKNOWN_COLUMN,
    }
    
    Type getType ();
    
    String getMessage ();
    
    String getTableName ();
}
