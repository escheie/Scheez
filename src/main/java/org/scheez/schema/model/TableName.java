package org.scheez.schema.model;

import org.scheez.util.BaseObject;

public class TableName extends BaseObject
{
    private String schemaName;
    private String tableName;

    public TableName(String tableName)
    {
        this(null, tableName);
    }

    public TableName(String schemaName, String tableName)
    {
        this.schemaName = schemaName;
        this.tableName = tableName;
    }

    public String getSchemaName()
    {
        return schemaName;
    }

    public String getTableName()
    {
        return tableName;
    }
    
    public TableName toUpperCase ()
    {
        String schemaNameUpper = (schemaName == null) ? null : schemaName
                .toUpperCase();
        String tableNameUpper = (tableName == null) ? null : tableName
                .toUpperCase();
        return new TableName(schemaNameUpper, tableNameUpper);
    }
    
    public TableName toLowerCase ()
    {
        String schemaNameLower = (schemaName == null) ? null : schemaName
                .toLowerCase();
        String tableNameLower = (tableName == null) ? null : tableName
                .toLowerCase();
        return new TableName(schemaNameLower, tableNameLower);
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        if (schemaName != null)
        {
            sb.append(schemaName);
            sb.append(".");
        }
        sb.append(tableName);
        return sb.toString();
    }
}
