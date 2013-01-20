package org.scheez.ddl;

public class Column
{
    public enum Type
    {
        BOOLEAN, INTEGER, BIGINT, FLOAT, DOUBLE, VARCHAR, TIMESTAMP_WITH_TIME_ZONE, BINARY, USER_DEFINED
    }

    private String name;
    private Type type;
    private Integer size;
    private Object extraInfo;

    public Column(String name, Type type)
    {
        this(name, type, null);
    }
    
    public Column(String name, Type type, Integer size)
    {
        super();
        this.name = name;
        this.type = type;
        this.size = size;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public Type getType()
    {
        return type;
    }

    public void setType(Type type)
    {
        this.type = type;
    }

    public Integer getSize()
    {
        return size;
    }

    public void setSize(Integer size)
    {
        this.size = size;
    }

    public Object getExtraInfo()
    {
        return extraInfo;
    }

    public void setExtraInfo(Object extraInfo)
    {
        this.extraInfo = extraInfo;
    }
}
