package org.scheez.schema.diff;

import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.parts.Table;

public interface SchemaDifference
{
    enum Type 
    {
        MISSING_TABLE,
        
        MISSING_COLUMN,
        
        UNKNOWN_TABLE,
        
        UNKNOWN_COLUMN,
        
        MISMATCHED_COLUMN_TYPE,
        
        MISMATCHED_COLUMN_LENGTH,
        
        MISMATCHED_COLUMN_PRECISION
    }
    
    Type getType ();
    
    String getDescription ();
    
    Table getTable ();
    
    Class<?> getTableClass ();
    
    void resolveDifference (SchemaDao schemaDao);
}
