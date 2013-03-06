/*
 * Copyright (C) 2013 by Teradata Corporation.
 * All Rights Reserved.
 * TERADATA CORPORATION CONFIDENTIAL AND TRADE SECRET
 */
package org.scheez.test;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * @author es151000
 * @version $Id: $
 */
public class TestDataSourceProxy implements DataSource 
{
    private TestDatabase testDatabase;
   
    /**
     * 
     */
    public TestDataSourceProxy(TestDatabase testDatabase)
    {
        this.testDatabase = testDatabase;
    }

    /** 
     * @inheritDoc
     */
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException
    {
        return testDatabase.getDataSource().unwrap(iface);
    }

    /** 
     * @inheritDoc
     */
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException
    {
        return testDatabase.getDataSource().isWrapperFor(iface);
    }

    /** 
     * @inheritDoc
     */
    @Override
    public PrintWriter getLogWriter() throws SQLException
    {
        return testDatabase.getDataSource().getLogWriter();
    }

    /** 
     * @inheritDoc
     */
    @Override
    public void setLogWriter(PrintWriter out) throws SQLException
    {
        testDatabase.getDataSource().setLogWriter(out);
        
    }

    /** 
     * @inheritDoc
     */
    @Override
    public void setLoginTimeout(int seconds) throws SQLException
    {
        testDatabase.getDataSource().setLoginTimeout(seconds);
        
    }

    /** 
     * @inheritDoc
     */
    @Override
    public int getLoginTimeout() throws SQLException
    {
        return testDatabase.getDataSource().getLoginTimeout();
    }

    /** 
     * @inheritDoc
     */
    @Override
    public Connection getConnection() throws SQLException
    {
        return testDatabase.getDataSource().getConnection();
    }

    /** 
     * @inheritDoc
     */
    @Override
    public Connection getConnection(String username, String password) throws SQLException
    {
        return testDatabase.getDataSource().getConnection(username, password);
    }
}
