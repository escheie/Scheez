package org.scheez.util;

/**
 * Contains design by contract utility methods.
 * 
 * @author Eric
 */
public class DbC
{ 
    public static void throwIfNullArg (Object... argValues)
    {
        for(int index = 0; index < argValues.length; index++)
        {
            if(argValues[index] == null)
            {
                throw new IllegalArgumentException ("Argument #" + (index + 1) + " must not be null!");
            }      
        }
    }
    
    public static void throwIfNullArg (String argName, Object argValue)
    {
        if (argValue == null)
        {
            throw new IllegalArgumentException ("Argument \"" + argName + "\" must not be null!");
        }
    }
}
