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
public class DefaultNameMapperFieldTest
{
    private String columnName;

    private String fieldName;

    public DefaultNameMapperFieldTest(String columnName, String fieldName)
    {
        this.columnName = columnName;
        this.fieldName = fieldName;
    }

    @Test
    public void testMapColumnNameToFieldName()
    {
        DefaultNameMapper nameMapper = new DefaultNameMapper();

        assertEquals(fieldName, nameMapper.mapColumnNameToFieldName(columnName));
    }

    @Test
    public void testMapFieldNameToColumnName()
    {
        DefaultNameMapper nameMapper = new DefaultNameMapper();

        assertEquals(columnName, nameMapper.mapFieldNameToColumnName(fieldName));
    }

    @Parameters(name = "{0}")
    public static Collection<Object[]> testNames()
    {
        List<Object[]> testParams = new ArrayList<Object[]>();
        add(testParams, "first_name", "firstName");
        add(testParams, "date_of_birth", "dateOfBirth");
        add(testParams, "lastname", "lastname");
        return testParams;
    }

    private static void add(List<Object[]> list, Object... objects)
    {
        list.add(objects);
    }
}
