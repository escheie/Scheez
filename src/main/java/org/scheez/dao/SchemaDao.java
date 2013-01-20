package org.scheez.dao;

import org.scheez.ddl.Table;

public interface SchemaDao
{
    void createTable (Table table);
    
    Table getTable (String schemaName, String tableName);
}
