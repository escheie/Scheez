package org.scheez.schema.mapper;

import org.scheez.reflect.PersistentClass;
import org.scheez.reflect.PersistentField;
import org.scheez.schema.model.Column;
import org.scheez.schema.model.Key;
import org.scheez.schema.model.Table;
import org.scheez.schema.model.TableName;
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
    public String mapClassToTableName(PersistentClass cls)
    {
        DbC.throwIfNullArg(cls);

        String name = cls.getTableName();
        if (name == null)
        {
            name = nameMapper.mapClassNameToTableName(cls.getType().getSimpleName());
        }
        return name;
    }

    @Override
    public Table mapClassToTable(TableName tableName, PersistentClass cls)
    {
        DbC.throwIfNullArg(tableName, cls);
        
        Table table = new Table (tableName);
        
        for (PersistentField field : cls.getPersistentFields())
        {
            table.addColumn(mapFieldToColumn(table, field));
        }
        
        return table;
    }

    @Override
    public Column mapFieldToColumn(Table table, PersistentField field)
    {
        DbC.throwIfNullArg(field);
        
        String name = field.getColumnName();
        if (name == null)
        {
            name = nameMapper.mapFieldNameToColumnName(field.getName());
            PersistentField reference = field.getReference();
            if(reference != null)
            {
                Column refCol = mapFieldToColumn(null, reference);
                name += "_" + refCol.getName();
            }
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
        
        if(field.isId())
        {
            column.setAutoIncrementing(true);
            if(table != null)
            {
                table.setPrimaryKey(new Key(table.getTableName(), "pk_" + column.getName()));
                table.getPrimaryKey().addColumnName(1, column.getName());
            }
        }

        return column;
    }

    @Override
    public PersistentField mapColumnToField(PersistentClass cls, String name)
    {
        DbC.throwIfNullArg(cls, name);
        String fieldName = nameMapper.mapColumnNameToFieldName(name);

        PersistentField retval = null;
        for (PersistentField field : cls.getPersistentFields())
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
                    throw new DuplicateKeyException("There are more than one field in " + cls.getType().getName()
                            + " or its super classes that map to the column \"" + name + "\".");
                }
                retval = f;
            }
        }

        return retval;
    }
}
