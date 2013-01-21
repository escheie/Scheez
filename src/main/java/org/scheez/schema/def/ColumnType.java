package org.scheez.schema.def;

import java.sql.Types;

public enum ColumnType
{
    TINYINT (Types.TINYINT),
    
    SMALLINT (Types.SMALLINT), 
    
    INTEGER (Types.INTEGER),  
    
    BIGINT (Types.BIGINT),
    
    FLOAT (Types.FLOAT, Types.REAL),
    
    DOUBLE (Types.DOUBLE),
    
    DECIMAL (Types.DECIMAL, Types.NUMERIC),
    
    BOOLEAN (Types.BOOLEAN, Types.BIT),
    
    CHAR (Types.CHAR),
    
    VARCHAR (Types.VARCHAR), 
    
    TIMESTAMP (Types.TIMESTAMP, Types.TIME, Types.DATE), 
    
    BINARY (Types.BINARY, Types.BLOB);
      
    private Integer[] types;
    
    private ColumnType(Integer... types)
    {
        this.types = types;
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
