package org.scheez.schema.objects;

import org.scheez.schema.def.ColumnType;
import org.scheez.util.BaseObject;

public class Column extends BaseObject
{
    private String name;
    private ColumnType type;
    private Integer size;
    private Integer precision;
    private Integer scale;
    private Object extraInfo;

    public Column(String name, ColumnType type)
    {
        this(name, type, null);
    }

    public Column(String name, ColumnType type, Integer size)
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

    public ColumnType getType()
    {
        return type;
    }

    public void setType(ColumnType type)
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

    public Integer getPrecision()
    {
        return precision;
    }

    public void setPrecision(Integer precision)
    {
        this.precision = precision;
    }

    public Integer getScale()
    {
        return scale;
    }

    public void setScale(Integer scale)
    {
        this.scale = scale;
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
