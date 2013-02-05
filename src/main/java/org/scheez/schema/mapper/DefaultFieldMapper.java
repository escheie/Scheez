package org.scheez.schema.mapper;

import java.lang.reflect.Field;

import org.scheez.reflect.PersistentField;
import org.scheez.schema.def.ColumnType;
import org.scheez.schema.parts.Column;
import org.springframework.dao.DuplicateKeyException;

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
    public Column mapField (Field f)
    {
        PersistentField field = new PersistentField (f);
        
        String name = field.getColumnName();
        if(name == null)
        {
            name = nameMapper.mapJavaNameToDatabaseName(field.getName());
        }
        
        Column column = new Column(name, field.getType());
        
        if(column.getType() == ColumnType.VARCHAR)
        {
            column.setLength(field.getLength());
        }
      
        return column;
    }

    @Override
    public Field mapField(Class<?> cls, String name)
    {
        String fieldName = nameMapper.mapDatabaseNameToJavaName(name);
        
        Field retval = null;
        for (PersistentField field : PersistentField.getPersistentFields(cls))
        {
            Field f = null;
            String columnName = field.getColumnName();
            if(columnName != null) 
            {
                if(name.equalsIgnoreCase(columnName))
                {
                    f = field.getField();
                }
            }
            else if (field.getName().equalsIgnoreCase(fieldName))
            {
                f = field.getField();
            }
            if (f != null)
            {
                if(retval != null)
                {
                    throw new DuplicateKeyException("There are more than one field in " + cls.getName() + " or its super classes that map to the column \"" + name + "\".");
                }
                retval = f;
            }
        }
        
        return retval;
    }
}
