package org.scheez.schema.dao;

import java.util.List;

import org.scheez.schema.def.ColumnType;
import org.scheez.schema.parts.Column;
import org.scheez.schema.parts.Index;
import org.scheez.schema.parts.Table;
import org.scheez.schema.parts.TableName;

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
    
    void alterColumnType (TableName tableName, Column column);
    
    ColumnType getExpectedColumnType (ColumnType columnType);
    
    void addIndex (TableName tableName, Index index);
    
    void dropIndex (TableName tableName, String indexName);
    
    Index getIndex (TableName tableName, String indexName);
}
