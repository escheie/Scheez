package org.scheez.schema.manger;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.reflections.Reflections;

public class SchemaClasses implements Iterable<Class<?>>
{
    private Set<Class<?>> includes;
    
    private Set<Class<?>> excludes;

    public SchemaClasses()
    {
        includes = new TreeSet<Class<?>>();
        excludes = new TreeSet<Class<?>>();
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
        TreeSet<Class<?>> set = new TreeSet<Class<?>>(includes);
        set.removeAll(excludes);
        return Collections.unmodifiableSet(set).iterator();
    }
}
