package org.scheez.schema.parts;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.scheez.util.BaseObject;
import org.scheez.util.DbC;

public class Index extends BaseObject
{
    private String name;

    private List<String> columnNames;
    
    public Index (String name, String... columnNames)
    {
        DbC.throwIfNullArg("name", name);
        this.name = name;
        this.columnNames = new LinkedList<String>();
        setColumnNames(Arrays.asList(columnNames));
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<String> getColumnNames()
    {
        return Collections.unmodifiableList(columnNames);
    }
    
    public void addColumnName (String columnName)
    {
        columnNames.add(columnName);
    }

    public void setColumnNames(Collection<String> columnNames)
    {
        this.columnNames.clear();
        this.columnNames.addAll(columnNames);
    }
    
    public Index toLowerCase ()
    {
        Index index = new Index(name.toLowerCase());
        for(String columnName : columnNames)
        {
            index.addColumnName(columnName.toLowerCase());
        }
        return index;
    }

    @Override
    public int hashCode()
    {
        return toLowerCase().hashCode();
    }
}
