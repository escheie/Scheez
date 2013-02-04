package org.scheez.schema.manger;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.reflections.Reflections;

public class SchemaClasses implements Iterable<Class<?>>
{
    private List<Class<?>> includes;
    
    private Set<Class<?>> excludes;

    public SchemaClasses()
    {
        includes = new LinkedList<Class<?>>();
        excludes = new HashSet<Class<?>>();
    }

    public SchemaClasses include (Package... packages)
    {
        for (Package p : packages)
        {
            Reflections reflections = new Reflections(p.getName());
            Set<Class<? extends Object>> allClasses = 
                    reflections.getSubTypesOf(Object.class);
            for (Class<? extends Object> cls : allClasses)
            {
                includes.add(cls);
            }
        }
        return this;
    }
    
    public SchemaClasses include (Class<?>... classes)
    {
        for (Class<?> cls : classes)
        {
            includes.add(cls);
        }
        return this;
    }
    
    public SchemaClasses exclude (Class<?>... classes)
    {
        for (Class<?> cls : classes)
        {
            excludes.add(cls);
        }
        return this;
    }

    @Override
    public Iterator<Class<?>> iterator()
    {
        List<Class<?>> list = new LinkedList<Class<?>>(includes);
        list.removeAll(excludes);
        return list.iterator();
    }
}
