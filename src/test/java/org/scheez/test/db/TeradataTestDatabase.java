/*
 * Copyright (C) 2013 by Teradata Corporation.
 * All Rights Reserved.
 * TERADATA CORPORATION CONFIDENTIAL AND TRADE SECRET
 */
package org.scheez.test.db;

import javax.sql.DataSource;

import org.scheez.schema.def.ColumnType;

/**
 * @author es151000
 * @version $Id: $
 */
public class TeradataTestDatabase extends AbstractTestDatabase
{
    public TeradataTestDatabase (DataSource dataSource)
    {
        super("teradata", dataSource);
    }
    
    @Override
    public ColumnType getExpectedColumnType(ColumnType columnType)
    {
        if(columnType == ColumnType.DOUBLE)
        {
            columnType = ColumnType.FLOAT;
        }
        else if(columnType == ColumnType.BOOLEAN)
        {
            columnType = ColumnType.TINYINT;
        }
        return columnType;
    }
    
    

   

}
