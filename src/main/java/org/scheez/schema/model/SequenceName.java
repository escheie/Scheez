package org.scheez.schema.model;

public class SequenceName extends ObjectName
{

    public SequenceName(String schemaName, String name)
    {
        super(schemaName, name);
        // TODO Auto-generated constructor stub
    }

    public SequenceName(String name)
    {
        super(name);
        // TODO Auto-generated constructor stub
    }
    
    public String getSequenceName ()
    {
        return name;
    }

    @Override
    protected SequenceName newInstance(String schemaName, String sequenceName)
    {
        return new SequenceName (schemaName, sequenceName);
    }

}
