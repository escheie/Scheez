package org.scheez.schema.mapper;

import java.util.StringTokenizer;

import org.scheez.util.DbC;

public class DefaultNameMapper implements NameMapper
{
    
    @Override
    public String mapDatabaseNameToJavaName (String name)
    {
        DbC.throwIfNullArg(name);
        
        boolean first = true;
        StringTokenizer tokenizer = new StringTokenizer(
                name.toLowerCase(), "_");
        StringBuilder sb = new StringBuilder();
        while (tokenizer.hasMoreTokens())
        {
            String token = tokenizer.nextToken();
            if (first)
            {
                sb.append(token);
                first = false;
            }
            else
            {
                sb.append(Character.toUpperCase(token.charAt(0)));
                if (token.length() > 1)
                {
                    sb.append(token.substring(1, token.length()));
                }
            }
        }
        return sb.toString();
    }
    
    @Override
    public String mapJavaNameToDatabaseName (String name)
    {
        DbC.throwIfNullArg(name);
        
        StringBuilder sb = new StringBuilder();
        boolean lastCapital = true;
        for(int index = 0; index < name.length(); index++)
        {
            char ch = name.charAt(index);
            if(Character.isUpperCase(ch))
            {
                if(!lastCapital)
                {
                    sb.append("_");
                }
                sb.append(Character.toLowerCase(ch));
                lastCapital = true;
            }
            else
            {
                sb.append(ch);
            }
            lastCapital = false;
        }
        return sb.toString();
    }
}
