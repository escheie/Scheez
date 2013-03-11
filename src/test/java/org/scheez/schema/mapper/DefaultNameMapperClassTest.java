package org.scheez.schema.mapper;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class DefaultNameMapperClassTest
{
    private String tableName;

    private String className;

    public DefaultNameMapperClassTest(String tableName, String className)
    {
        this.tableName = tableName;
        this.className = className;
    }

    @Test
    public void testMapTableNameToClassName()
    {
        DefaultNameMapper nameMapper = new DefaultNameMapper();

        assertEquals(className, nameMapper.mapTableNameToClassName(tableName));
    }

    @Test
    public void testMapClassNameToTableName()
    {
        DefaultNameMapper nameMapper = new DefaultNameMapper();

        assertEquals(tableName, nameMapper.mapClassNameToTableName(className));
    }

    @Parameters(name = "{0}")
    public static Collection<Object[]> testNames()
    {
        List<Object[]> testParams = new ArrayList<Object[]>();
        add(testParams, "employees", "Employee");
        add(testParams, "collection_lists", "CollectionList");
        add(testParams, "car_part_orders", "CarPartOrder");
        return testParams;
    }

    private static void add(List<Object[]> list, Object... objects)
    {
        list.add(objects);
    }
}
