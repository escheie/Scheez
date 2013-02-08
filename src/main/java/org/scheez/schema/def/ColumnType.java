package org.scheez.schema.def;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;

public enum ColumnType
{ 
    TINYINT (new Class<?>[] {Byte.class, byte.class}, Types.TINYINT),
    
    SMALLINT (new Class<?>[] {Short.class, short.class}, Types.SMALLINT), 
    
    INTEGER (new Class<?>[] {Integer.class, int.class}, Types.INTEGER),  
    
    BIGINT (new Class<?>[] {Long.class, long.class, BigInteger.class}, Types.BIGINT),
    
    FLOAT (new Class<?>[] {Float.class, float.class}, Types.FLOAT, Types.REAL),
    
    DOUBLE (new Class<?>[] {Double.class, double.class}, Types.DOUBLE),
    
    DECIMAL (new Class<?>[] {BigDecimal.class}, Types.DECIMAL, Types.NUMERIC),
    
    BOOLEAN (new Class<?>[] {Boolean.class, boolean.class}, Types.BOOLEAN, Types.BIT),
    
    VARCHAR (new Class<?>[] {String.class, char[].class, char.class, Character[].class, Character.class}, Types.VARCHAR, Types.CLOB),
    
    CHAR (VARCHAR.getTypeClasses(), Types.CHAR),
    
    TIMESTAMP (new Class<?>[] {Timestamp.class, Date.class, Time.class, java.util.Date.class, Calendar.class}, Types.TIMESTAMP, Types.TIME, Types.DATE), 
    
    BINARY (new Class<?>[] { byte[].class, Byte[].class }, Types.BINARY, Types.BLOB, Types.OTHER, Types.ARRAY);
    
    private Class<?>[] classes;
      
    private Integer[] sqlTypes;
    
    private ColumnType(Class<?>[] classes, Integer... sqlTypes)
    {
        this.classes = classes;
        this.sqlTypes = sqlTypes;
    }
    
    public Class<?> getTypeClass ()
    {
        return classes[0];
    }
    
    public Class<?>[] getTypeClasses ()
    {
        return classes;
    }
    
    public boolean isEquivalent (int sqlTypeCode)
    {
        boolean eq = false;
        for(int type : sqlTypes)
        {
            if(type == sqlTypeCode)
            {
                eq = true;
                break;
            }
        }
        return eq;
    }

    /**
     * Maps the type from Java SQL types.
     */
    public static ColumnType getType (int sqlTypeCode)
    {
        ColumnType type = null;
        for (ColumnType t : values())
        {
            if(t.isEquivalent(sqlTypeCode))
            {
                type = t;
                break;
            }
            
        }
        return type;
    }
    
    public boolean isLengthSupported ()
    {
        return (this == CHAR) || (this == VARCHAR);
    }
    
    public boolean isPrecisionSupported ()
    {
        return (this == DECIMAL);
    }
    
    /**
     * Maps the type from Java SQL types.
     */
    public static ColumnType getType (Class<?> cls)
    {
        ColumnType type = null;
        for (ColumnType t : values())
        {
            for (Class<?> c : t.getTypeClasses())
            {
                if(c.equals(cls))
                {
                    type = t;
                    break;
                }
            }
            if (type != null)
            {
                break;
            }
        }
        return type;
    }
}
