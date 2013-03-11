package org.scheez.schema.model;

import org.scheez.util.BaseObject;

public abstract class ObjectName extends BaseObject
{
    private String schemaName;
    protected String name;

    public ObjectName(String name)    
    {
        this(null, name);
    }

    public ObjectName(String schemaName, String name)
    {
        this.schemaName = schemaName;
        this.name = name;
    }

    public String getSchemaName()
    {
        return schemaName;
    }
    
    public String getObjectName ()
    {
        return name;
    }
    
    public ObjectName toUpperCase ()
    {
        String schemaNameUpper = (schemaName == null) ? null : schemaName
                .toUpperCase();
        String nameUpper = (name == null) ? null : name.toUpperCase();
        return newInstance (schemaNameUpper, nameUpper);
    }
    
    public ObjectName toLowerCase ()
    {
        String schemaNameLower = (schemaName == null) ? null : schemaName
                .toLowerCase();
        String nameLower = (name == null) ? null : name.toLowerCase();
        return newInstance(schemaNameLower, nameLower);
    }
    
    protected abstract ObjectName newInstance (String schemaName, String nameLower);

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        if (schemaName != null)
        {
            sb.append(schemaName);
            sb.append(".");
        }
        sb.append(name);
        return sb.toString();
    }
}
