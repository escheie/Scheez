/*
 * Copyright (C) 2013 by Teradata Corporation. All Rights Reserved. TERADATA CORPORATION
 * CONFIDENTIAL AND TRADE SECRET
 */
package org.scheez.schema.model;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.scheez.util.BaseObject;
import org.scheez.util.DbC;

/**
 * @author es151000
 * @version $Id: $
 */
public class Key extends BaseObject
{
    private TableName tableName;

    private String keyName;

    private SortedMap<Integer, String> columnNames;

    public Key(TableName tableName, String keyName)
    {
        DbC.throwIfNullArg("tableName", tableName);
        this.tableName = tableName;
        this.keyName = keyName;
        columnNames = new TreeMap<Integer, String>();
    }

    /**
     * @return the tableName
     */
    public TableName getTableName()
    {
        return tableName;
    }

    /**
     * @param tableName
     *            the tableName to set
     */
    public void setTableName(TableName tableName)
    {
        this.tableName = tableName;
    }

    /**
     * @return the keyName
     */
    public String getKeyName()
    {
        return keyName;
    }

    /**
     * @param keyName
     *            the keyName to set
     */
    public void setKeyName(String keyName)
    {
        this.keyName = keyName;
    }

    public List<String> getColumnNames()
    {
        return new ArrayList<String>(columnNames.values());
    }

    public void addColumnName(int sequence, String columnName)
    {
        columnNames.put(sequence, columnName);
    }
}
