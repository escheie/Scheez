package org.scheez.schema.mapper;

import java.lang.reflect.Field;

import javassist.Modifier;

import org.atteo.evo.inflector.English;
import org.scheez.schema.objects.Table;
import org.scheez.schema.objects.TableName;

public class DefaultClassMapper implements ClassMapper
{
    private NameMapper nameMapper;

    private FieldMapper fieldMapper;

    public DefaultClassMapper ()
    {
        this.nameMapper = new DefaultNameMapper();
        this.fieldMapper = new DefaultFieldMapper();
    }

    @Override
    public String mapClass(Class<?> cls)
    {
        String name = nameMapper.mapJavaNameToDatabaseName(cls.getSimpleName());
        int index = name.lastIndexOf('_');
        if (index < 0)
        {
            name = English.plural(name);
        }
        else
        {
            name = name.substring(0, index + 1) + English.plural(name.substring(index + 1));
        }
        return name;
    }

    @Override
    public Table mapClass  (Class<?> cls, TableName tableName)
    {
        Table table = new Table (tableName);
        
        while (cls != null)
        {
            for (Field field : cls.getDeclaredFields())
            {
                if(!Modifier.isTransient(field.getModifiers()))
                {
                    table.addColumn(fieldMapper.mapField(field));
                }
            }
            cls = cls.getSuperclass();
        }
        
        return table;
    }

    public NameMapper getNameMapper()
    {
        return nameMapper;
    }

    public void setNameMapper(NameMapper nameMapper)
    {
        this.nameMapper = nameMapper;
    }

    public FieldMapper getFieldMapper()
    {
        return fieldMapper;
    }

    public void setFieldMapper(FieldMapper fieldMapper)
    {
        this.fieldMapper = fieldMapper;
    }

}
