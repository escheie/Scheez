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
public class DefaultNameMapperTest
{ 
    private String databaseName;
    
    private String javaName;
    
    public DefaultNameMapperTest (String databaseName, String javaName)
    {
        this.databaseName = databaseName;
        this.javaName = javaName;
    }
    
    @Test
    public void testMapDatabaseNameToJavaName()
    {
        DefaultNameMapper nameMapper = new DefaultNameMapper ();
        
        assertEquals(javaName, nameMapper.mapDatabaseNameToJavaName(databaseName));
    }

    @Test
    public void testMapJavaNameToDatabaseName()
    {
        DefaultNameMapper nameMapper = new DefaultNameMapper ();
        
        assertEquals(databaseName, nameMapper.mapJavaNameToDatabaseName(javaName));
    }
    
    @Parameters (name="{0}")
    public static Collection<Object[]> testNames ()
    {
        List<Object[]> testParams = new ArrayList<Object[]>();
        add(testParams, "first_name", "firstName");
        return testParams;
    } 
        
    private static void add (List<Object[]> list, Object... objects)
    {
        list.add(objects);
    }
}
