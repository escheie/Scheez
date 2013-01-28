/**
 * 
 */
package org.scheez.util;

import org.junit.Test;

/**
 * @author Eric
 *
 */
public class DbCTest
{
    /**
     * Test method for {@link org.scheez.util.DbC#throwIfNullArg(java.lang.Object[])}.
     */
    @Test (expected = IllegalArgumentException.class )
    public void testThrowIfNullArgObjectArray()
    {
        DbC.throwIfNullArg("cool", null, Integer.class);
    }

    /**
     * Test method for {@link org.scheez.util.DbC#throwIfNullArg(java.lang.String, java.lang.Object)}.
     */
    @Test (expected = IllegalArgumentException.class )
    public void testThrowIfNullArgStringObject()
    {
        DbC.throwIfNullArg("argName", null);
    }
}
