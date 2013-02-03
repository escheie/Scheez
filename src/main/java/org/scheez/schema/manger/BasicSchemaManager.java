package org.scheez.schema.manger;

import java.util.LinkedList;
import java.util.List;

import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.diff.SchemaDifference;

public class BasicSchemaManager implements SchemaManager
{
    private String schemaName;
    
    private SchemaDao schemaDao;
    
    private Iterable<Class<?>> classes;
   
    public BasicSchemaManager (String schemaName, SchemaDao schemaDao, Iterable<Class<?>> classes)
    {
        this.schemaName = schemaName;
        this.schemaDao = schemaDao;
        this.classes = classes;
    }
    
    @Override
    public List<SchemaDifference> diff()
    {
        List<SchemaDifference> diff = new LinkedList<SchemaDifference> ();
        
        
        
        
        return diff;
    }
}
