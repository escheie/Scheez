package org.scheez.schema.model;


public class TableName extends ObjectName
{

    public TableName(String schemaName, String name)
    {
        super(schemaName, name);
        // TODO Auto-generated constructor stub
    }

    public TableName(String name)
    {
        super(name);
        // TODO Auto-generated constructor stub
    }
    
    public String getTableName ()
    {
        return name;
    }

    @Override
    protected TableName newInstance(String schemaName, String tableName)
    {
        return new TableName(schemaName, tableName);
    }

}
