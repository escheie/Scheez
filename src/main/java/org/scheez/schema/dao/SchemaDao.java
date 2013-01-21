package org.scheez.schema.dao;

import org.scheez.schema.objects.Table;
import org.scheez.schema.objects.TableName;

public interface SchemaDao
{
    void createTable (Table table);
    
    void dropTable (TableName tableName);
    
    Table getTable (TableName tableName);
}
