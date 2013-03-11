package org.scheez.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.scheez.persistence.Rename;
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
            if (field.getType().isEnum())
            {
                type = ColumnType.VARCHAR;
            }
            else 
            {
                PersistentClass cls = new PersistentClass (field.getType());
                PersistentField id = cls.getIdField();
                if (id != null)
                {
                    type = id.getType();
                }
            }
        }
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
    
    public boolean isCollection ()
    {
        return Collection.class.isAssignableFrom(field.getType());
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
    
    public String[] getPreviousNames ()
    {
        String previousNames[] = new String[0];
        Rename rename = field.getAnnotation(Rename.class);
        if(rename != null)
        {
            previousNames = rename.previousNames();
        }
        return previousNames;
    }
    
    public PersistentField getReference ()
    {
        return new PersistentClass(field.getType()).getIdField();
    }
    
    public PersistentClass getDeclaringClass ()
    {
        return new PersistentClass(field.getDeclaringClass());
    }
    
    public boolean isId()
    {
        return field.getAnnotation(Id.class) != null;
    }
    
    public void set (Object target, Object value)
    {
        if(!Modifier.isPublic(field.getModifiers()))
        {
            field.setAccessible(true);
        }
        ReflectionUtils.setField(field, target, value);
    }

   
}
