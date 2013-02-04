package org.scheez.schema.dao;

import java.util.List;

import org.scheez.schema.objects.Column;
import org.scheez.schema.objects.Table;
import org.scheez.schema.objects.TableName;

public interface SchemaDao
{
    void createSchema (String schemaName);
    
    void dropSchema (String schemaName);
    
    boolean schemaExists (String schemaName);
    
    List<String> getSchemas ();
    
    void createTable (Table table);
    
    void dropTable (TableName tableName);
    
    Table getTable (TableName tableName);
    
    List<Table> getTables (String schemaName);
    
    void addColumn (TableName tableName, Column column);
    
    void dropColumn (TableName tableName, String columnName);
    
    Column getColumn (TableName tableName, String columnName);
}
