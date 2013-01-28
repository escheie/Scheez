/**
 * 
 */
package org.scheez.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Eric
 */
public class BaseObjectTest
{
    /**
     * Test method for {@link org.scheez.util.BaseObject#hashCode()}.
     */
    @Test
    public void testHashCode()
    {
        assertEquals(new BaseObject().hashCode(), new BaseObject().hashCode());
    }

    /**
     * Test method for
     * {@link org.scheez.util.BaseObject#equals(java.lang.Object)}.
     */
    @Test
    public void testEqualsObject()
    {
        assertEquals(new BaseObject(), new BaseObject());
    }

    /**
     * Test method for {@link org.scheez.util.BaseObject#toString()}.
     */
    @Test
    public void testToString()
    {
        assertNotNull(new BaseObject().toString());
    }

    /**
     * Test method for {@link org.scheez.util.BaseObject#hasNulls()}.
     */
    @Test
    public void testHasNulls()
    {
        assertFalse(new BaseObject().hasNulls());
        TestObject testObject = new TestObject();
        assertTrue(testObject.hasNulls());
        testObject.setString("string");
        assertTrue(testObject.hasNulls());
        testObject.setInteger(14);
        assertFalse(testObject.hasNulls());
    }

    /**
     * Test method for {@link org.scheez.util.BaseObject#hasValues()}.
     */
    @Test
    public void testHasValues()
    {
        assertFalse(new BaseObject().hasValues());
        TestObject testObject = new TestObject();
        assertFalse(testObject.hasValues());
        testObject.setString("string");
        assertTrue(testObject.hasValues());
        testObject.setInteger(14);
        assertTrue(testObject.hasValues());
    }

    public class TestObject extends BaseObject
    {
        public String string;

        public Integer integer;

        public String getString()
        {
            return string;
        }

        public void setString(String string)
        {
            this.string = string;
        }

        public Integer getInteger()
        {
            return integer;
        }

        public void setInteger(Integer integer)
        {
            this.integer = integer;
        }
    }
}
