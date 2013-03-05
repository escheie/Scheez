/*
 * Copyright (C) 2013 by Teradata Corporation.
 * All Rights Reserved.
 * TERADATA CORPORATION CONFIDENTIAL AND TRADE SECRET
 */
package org.scheez.test;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author es151000
 * @version $Id: $
 */
public class TestDatabasePropertiesTest
{

    @Test
    public void test()
    {
        TestDatabaseProperties properties = new TestDatabaseProperties();
        properties.setProperty("test.1", "red fish");
        properties.setProperty("test.2", "blue fish");
        properties.setProperty("test.3", "${test.1} foot");
        properties.setProperty("test.4", "one foot ${test.2} foot");
        properties.setProperty("test.5", "${test.1} ${test.4}");
        properties.setProperty("test.6", "${test.3} ${test.5}");
        properties.setProperty("test.7", "${test.1} ${123456}");
        properties.setProperty("test.8", "$${test.1}");
        
        properties = properties.withPrefix("test");
        
        assertEquals("red fish", properties.getProperty("1", true, true));
        assertEquals("blue fish", properties.getProperty("2", true, true));
        assertEquals("red fish foot", properties.getProperty("3", true, true));
        assertEquals("one foot blue fish foot", properties.getProperty("4", true, true));
        assertEquals("red fish one foot blue fish foot", properties.getProperty("5", true, true));
        assertEquals("red fish foot red fish one foot blue fish foot", properties.getProperty("6", true, true));
        assertEquals("red fish ${123456}", properties.getProperty("7", true, true));
        assertEquals("${test.1}", properties.getProperty("8", true, true));
    }
}
