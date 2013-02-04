package org.scheez.schema.mapper;

import java.lang.reflect.Field;

import org.scheez.schema.def.ColumnType;
import org.scheez.schema.objects.Column;

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
    public Column mapField (Field field)
    {
        String name = nameMapper.mapJavaNameToDatabaseName(field.getName());
        ColumnType type = ColumnType.getType(field.getType());
        if(type == null)
        {
            throw new UnsupportedOperationException("The java type " + field.getType() + " is not supported");
        }
        Column column = new Column(name, type);
        return column;
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
