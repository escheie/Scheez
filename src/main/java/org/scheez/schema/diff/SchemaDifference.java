package org.scheez.schema.diff;

import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.model.Table;

public interface SchemaDifference
{
    enum Type 
    {
        MISSING_SCHEMA,
        
        MISSING_TABLE,
        
        MISSING_COLUMN,
        
        MISSING_INDEX,
        
        UNKNOWN_TABLE,
        
        UNKNOWN_COLUMN,
        
        UNKNOWN_INDEX,
        
        RENAMED_TABLE,
        
        RENAMED_COLUMN,
        
        MISMATCHED_COLUMN_TYPE,
        
        MISMATCHED_COLUMN_LENGTH,
        
        MISMATCHED_COLUMN_PRECISION
    }
    
    Type getType ();
    
    String getDescription ();
    
    Table getTable ();
    
    Class<?> getTableClass ();
    
    void reconcileDifferences (SchemaDao schemaDao);
}
