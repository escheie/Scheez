package org.scheez.dao;

import org.scheez.schema.objects.Table;
import org.scheez.schema.objects.TableName;

public interface SchemaDao
{
    void createSchema (String schemaName);
    
    void dropSchema (String schemaName);
    
    boolean schemaExists (String schemaName);
    
    void createTable (Table table);
    
    void dropTable (TableName tableName);
    
    Table getTable (TableName tableName);
}
