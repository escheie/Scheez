package org.scheez.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Transient;

import org.scheez.schema.def.ColumnType;
import org.springframework.util.ReflectionUtils;

public class PersistentField
{
    private Field field;

    private Column config;

    public PersistentField(Field field)
    {
        this.field = field;
        this.config = field.getAnnotation(Column.class);
    }

    public Field getField()
    {
        return field;
    }
    
    public String getName ()
    {
        return field.getName();
    }

    public String getColumnName ()
    {
        String name = null;
        if ((config != null) && (config.name() != null) && (!config.name().isEmpty()))
        {
            name = config.name();
        }
        return name;
    }

    public ColumnType getType()
    {
        ColumnType type = ColumnType.getType(field.getType());
        if (type == null)
        {
            throw new UnsupportedOperationException("The java type " + field.getType() + " is not supported");
        }
        return type;
    }

    public boolean isTransient()
    {
        boolean retval = Modifier.isTransient(field.getModifiers());
        if (!retval)
        {
            if (field.getAnnotation(Transient.class) != null)
            {
                retval = true;
            }
        }
        return retval;
    }
    
    public Integer getLength ()
    {
        Integer length = null;
        if (config != null) 
        {
            length = config.length();
        }
        return length;
    }
    
    public Integer getPrecision ()
    {
        Integer precision = null;
        if (config != null) 
        {
            precision = config.precision();
        }
        return precision;
    }
    
    public Integer getScale ()
    {
        Integer scale = null;
        if (config != null) 
        {
            scale = config.scale();
        }
        return scale;
    }
    
    public void set (Object target, Object value)
    {
        if(!Modifier.isPublic(field.getModifiers()))
        {
            field.setAccessible(true);
        }
        ReflectionUtils.setField(field, target, value);
    }

    public static List<PersistentField> getPersistentFields(Class<?> cls)
    {
        List<PersistentField> fields = new LinkedList<PersistentField>();
        while (cls != null)
        {
            for (Field field : cls.getDeclaredFields())
            {
                PersistentField f = new PersistentField(field);
                if (!f.isTransient())
                {
                    fields.add(f);
                }
            }
            cls = cls.getSuperclass();
        }
        return fields;
    }
}
