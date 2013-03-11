package org.scheez.reflect;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.scheez.persistence.Rename;

public class PersistentClass
{
    private Class<?> cls;

    public PersistentClass(Class<?> cls)
    {
        super();
        this.cls = cls;
    }
    
    public Class<?> getType ()
    {
        return cls;
    }
    
    public boolean isEntity ()
    {
        return cls.getAnnotation(Entity.class) != null;
    }

    public String getTableName()
    {
        Table config = cls.getAnnotation(Table.class);

        String name = null;
        if ((config != null) && (config.name() != null) && (!config.name().isEmpty()))
        {
            name = config.name();
        }
        return name;
    }
    
    public String[] getPreviousNames ()
    {
        String previousNames[] = new String[0];
        Rename rename = cls.getAnnotation(Rename.class);
        if(rename != null)
        {
            previousNames = rename.previousNames();
        }
        return previousNames;
    }

    public List<PersistentField> getPersistentFields()
    {
        List<PersistentField> fields = new LinkedList<PersistentField>();
        Class<?> c = cls;
        while (c != null)
        {
            for (Field field : c.getDeclaredFields())
            {
                PersistentField f = new PersistentField(field);
                if ((!f.isTransient()) && (!f.isCollection()))
                {
                    fields.add(f);
                }
            }
            c = c.getSuperclass();
        }
        return fields;
    }

    public PersistentField getIdField()
    {
        PersistentField retval = null;
        if (isEntity())
        {
            for (PersistentField field : getPersistentFields())
            {
                if (field.isId())
                {
                    retval = field;
                    break;
                }
            }
        }
        return retval;
    }

}
