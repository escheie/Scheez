/*
 * Copyright (C) 2013 by Teradata Corporation. All Rights Reserved. TERADATA CORPORATION
 * CONFIDENTIAL AND TRADE SECRET
 */
package org.scheez.schema.model;

/**
 * @author es151000
 * @version $Id: $
 */
public class ForeignKey extends Key
{
    private Key referencedPrimaryKey;

    public ForeignKey(TableName tableName, String keyName)
    {
        super(tableName, keyName);
    }

    /**
     * @return the referencedPrimaryKey
     */
    public Key getReferencedPrimaryKey()
    {
        return referencedPrimaryKey;
    }

    /**
     * @param referencedPrimaryKey
     *            the referencedPrimaryKey to set
     */
    public void setReferencedPrimaryKey(Key referencedPrimaryKey)
    {
        this.referencedPrimaryKey = referencedPrimaryKey;
    }

}
