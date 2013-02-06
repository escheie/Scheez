package org.scheez.schema.mapper;

import java.util.List;

import org.atteo.evo.inflector.English;
import org.scheez.reflect.PersistentField;
import org.scheez.schema.parts.Column;
import org.scheez.schema.parts.Table;
import org.scheez.schema.parts.TableName;
import org.scheez.util.DbC;
import org.springframework.dao.DuplicateKeyException;

public class DefaultSchemaMapper implements SchemaMapper
{
    private NameMapper nameMapper;

    public DefaultSchemaMapper()
    {
        this(new DefaultNameMapper());
    }

    public DefaultSchemaMapper(NameMapper nameMapper)
    {
        DbC.throwIfNullArg(nameMapper);
        this.nameMapper = nameMapper;
    }

    @Override
    public String mapClassToTableName(Class<?> cls)
    {
        DbC.throwIfNullArg(cls);
        
        javax.persistence.Table config = cls.getAnnotation(javax.persistence.Table.class);

        String name = nameMapper.mapJavaNameToDatabaseName(cls.getSimpleName());
        if ((config != null) && (config.name() != null) && (!config.name().isEmpty()))
        {
            name = config.name();
        }
        else
        {
            int index = name.lastIndexOf('_');
            if (index < 0)
            {
                name = English.plural(name);
            }
            else
            {
                name = name.substring(0, index + 1) + English.plural(name.substring(index + 1));
            }
        }
        return name;
    }

    @Override
    public Table mapClassToTable(TableName tableName, Class<?> cls)
    {
        DbC.throwIfNullArg(tableName, cls);
        
        Table table = new Table (tableName);
        
        for (PersistentField field : getPersistentFields(cls))
        {
            table.addColumn(mapFieldToColumn(field));
        }
        
        return table;
    }

    @Override
    public Column mapFieldToColumn(PersistentField field)
    {
        DbC.throwIfNullArg(field);
        
        String name = field.getColumnName();
        if (name == null)
        {
            name = nameMapper.mapJavaNameToDatabaseName(field.getName());
        }

        Column column = new Column(name, field.getType());

        if (column.getType().isLengthSupported())
        {
            column.setLength(field.getLength());
        }
        else if (column.getType().isPrecisionSupported())
        {
            column.setPrecision(field.getPrecision());
            column.setScale(field.getScale());
        }

        return column;
    }

    @Override
    public List<PersistentField> getPersistentFields(Class<?> cls)
    {
        DbC.throwIfNullArg(cls);
        return PersistentField.getPersistentFields(cls);
    }

    @Override
    public PersistentField mapColumnToField(Class<?> cls, String name)
    {
        DbC.throwIfNullArg(cls, name);
        String fieldName = nameMapper.mapDatabaseNameToJavaName(name);

        PersistentField retval = null;
        for (PersistentField field : getPersistentFields(cls))
        {
            PersistentField f = null;
            String columnName = field.getColumnName();
            if (columnName != null)
            {
                if (name.equalsIgnoreCase(columnName))
                {
                    f = field;
                }
            }
            else if (field.getName().equalsIgnoreCase(fieldName))
            {
                f = field;
            }
            if (f != null)
            {
                if (retval != null)
                {
                    throw new DuplicateKeyException("There are more than one field in " + cls.getName()
                            + " or its super classes that map to the column \"" + name + "\".");
                }
                retval = f;
            }
        }

        return retval;
    }
}
