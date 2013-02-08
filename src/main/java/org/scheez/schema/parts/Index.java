package org.scheez.schema.parts;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.scheez.util.BaseObject;

public class Index extends BaseObject
{
    private String name;

    private List<String> columnNames;

    private boolean unique;
    
    public Index (String name, String... columnNames)
    {
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

    public boolean isUnique()
    {
        return unique;
    }

    public void setUnique(boolean unique)
    {
        this.unique = unique;
    }
}
