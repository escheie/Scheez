package org.scheez.schema.def;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Types;

public enum ColumnType
{ 
    TINYINT (Byte.class, Types.TINYINT),
    
    SMALLINT (Short.class, Types.SMALLINT), 
    
    INTEGER (Integer.class, Types.INTEGER),  
    
    BIGINT (Long.class, Types.BIGINT),
    
    FLOAT (Float.class, Types.FLOAT, Types.REAL),
    
    DOUBLE (Double.class, Types.DOUBLE),
    
    DECIMAL (BigDecimal.class, Types.DECIMAL, Types.NUMERIC),
    
    BOOLEAN (Boolean.class, Types.BOOLEAN, Types.BIT),
    
    CHAR (String.class, Types.CHAR),
    
    VARCHAR (String.class, Types.VARCHAR), 
    
    TIMESTAMP (Timestamp.class, Types.TIMESTAMP, Types.TIME, Types.DATE), 
    
    BINARY (byte[].class, Types.BINARY, Types.BLOB, Types.OTHER, Types.ARRAY);
    
    private Class<? extends Object> cls;
      
    private Integer[] types;
    
    private ColumnType(Class<? extends Object> cls, Integer... types)
    {
        this.cls = cls;
        this.types = types;
    }
    
    public Class<? extends Object> getJavaClass ()
    {
        return cls;
    }
    
    public boolean isEquivalent (int sqlTypeCode)
    {
        boolean eq = false;
        for(int type : types)
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
}
