package org.scheez.schema.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scheez.schema.def.ColumnType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.ReflectionUtils;

public class ObjectMapper<T> implements RowMapper<T>
{
    private static final Log log = LogFactory.getLog(ObjectMapper.class);

    private Class<T> cls;

    private FieldMapper fieldMapper;

    public ObjectMapper(Class<T> cls)
    {
        this.cls = cls;
        fieldMapper = new DefaultFieldMapper();
    }

    public Class<T> getMappedClass()
    {
        return cls;
    }

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException
    {
        try
        {
            T t = cls.newInstance();
            ResultSetMetaData rsmd = rs.getMetaData();
            for (int index = 1; index <= rsmd.getColumnCount(); index++)
            {
                String columnName = rsmd.getColumnLabel(index);
                Field field = fieldMapper.mapField(cls, columnName);
                if (field != null)
                {
                    if(!Modifier.isPublic(field.getModifiers()))
                    {
                        field.setAccessible(true);
                    }
                    ReflectionUtils.setField(field, t, 
                            getValue(rs, index, columnName, ColumnType
                                    .getType(rsmd.getColumnType(index)), field.getType()));
                }
            }
            return t;
        }
        catch (Exception e)
        {
            throw new SQLException("Unable to map result set to "
                    + cls.getName() + ".", e);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Object getValue(ResultSet rs, int index, String columnName,
            ColumnType type, Class<?> target) throws SQLException
    {
        Object value = rs.getObject(index);
        if (value != null)
        {
            log.debug(columnName + ", " + type + ", type="
                    + value.getClass().getName() + ", targetType="
                    + target.getName());
            if (!target.isAssignableFrom(value.getClass()))
            {
                if (target.isEnum())
                {
                    Enum.valueOf((Class<? extends Enum>) target,
                            value.toString());
                }
                else if (target.equals(BigDecimal.class))
                {
                    value = rs.getBigDecimal(index);
                }
                else if (target.isAssignableFrom(Blob.class))
                {
                    value = rs.getBlob(index);
                }
                else if (target.equals(Boolean.class))
                {
                    value = rs.getBoolean(index);
                }
                else if (target.equals(Byte.class))
                {
                    value = rs.getByte(index);
                }
                else if (target.equals(byte[].class))
                {
                    value = rs.getBytes(index);
                }
                else if (target.isAssignableFrom(Clob.class))
                {
                    value = rs.getClob(index);
                }
                else if (target.equals(Date.class))
                {
                    value = rs.getDate(index);
                }
                else if (target.equals(Double.class))
                {
                    value = rs.getDouble(index);
                }
                else if (target.equals(Float.class))
                {
                    value = rs.getFloat(index);
                }
                else if (target.equals(Integer.class))
                {
                    value = rs.getInt(index);
                }
                else if (target.equals(Long.class))
                {
                    value = rs.getLong(index);
                }
                else if (target.equals(Short.class))
                {
                    value = rs.getShort(index);
                }
                else if (target.equals(String.class))
                {
                    value = rs.getShort(index);
                }
                else if (target.equals(SQLXML.class))
                {
                    value = rs.getSQLXML(index);
                }
                else if (target.equals(Time.class))
                {
                    value = rs.getTime(index);
                }
                else if (target.equals(Timestamp.class))
                {
                    value = rs.getTimestamp(index);
                }
            }
        }
        else
        {
            log.debug(columnName + ", " + type + ", value=null");
        }
        return value;
    }

    public FieldMapper getFieldMapper()
    {
        return fieldMapper;
    }

    public void setFieldMapper(FieldMapper fieldMapper)
    {
        if (fieldMapper == null)
        {
            throw new IllegalArgumentException(
                    "fieldMapper argument cannot be null.");
        }
        this.fieldMapper = fieldMapper;
    }
}
