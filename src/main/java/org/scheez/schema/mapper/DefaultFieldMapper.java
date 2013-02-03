package org.scheez.schema.mapper;

import java.lang.reflect.Field;

public class DefaultFieldMapper implements FieldMapper
{
    private NameMapper nameMapper;

    public DefaultFieldMapper()
    {
        this(new DefaultNameMapper());
    }

    public DefaultFieldMapper(NameMapper nameMapper)
    {
        this.nameMapper = nameMapper;
    }

    @Override
    public String mapField(Field field)
    {
        return nameMapper.mapJavaNameToDatabaseName(field.getName());
    }

    @Override
    public Field mapField(Class<?> cls, String name)
    {
        String fieldName = nameMapper.mapDatabaseNameToJavaName(name);

        Field retval = null;
        while((retval == null) && (cls != null))
        {
            for (Field field : cls.getDeclaredFields())
            {
                if (field.getName().equalsIgnoreCase(fieldName))
                {
                    retval = field;
                    break;
                }
            }
            cls = cls.getSuperclass();
        }

        return retval;
    }
}
