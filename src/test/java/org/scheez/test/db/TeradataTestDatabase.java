/*
 * Copyright (C) 2013 by Teradata Corporation.
 * All Rights Reserved.
 * TERADATA CORPORATION CONFIDENTIAL AND TRADE SECRET
 */
package org.scheez.test.db;

import javax.sql.DataSource;

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

   

}
