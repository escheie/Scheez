package org.scheez.schema.parts;

import org.scheez.schema.def.ColumnType;
import org.scheez.util.BaseObject;

public class Column extends BaseObject
{
    private String name;

    private ColumnType type;

    private Integer length;

    private Integer precision;

    private Integer scale;

    private String definition;

    public Column(String name, ColumnType type)
    {
        this(name, type, null);
    }

    public Column(String name, ColumnType type, Integer length)
    {
        super();
        this.name = name;
        this.type = type;
        this.length = length;
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

    public Integer getLength()
    {
        return length;
    }

    public void setLength(Integer length)
    {
        this.length = length;
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

    public String getDefinition()
    {
        return definition;
    }

    public void setDefinition(String definition)
    {
        this.definition = definition;
    }

}
