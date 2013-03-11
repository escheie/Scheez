package org.scheez.schema.mapper;

import java.util.StringTokenizer;

import org.modeshape.common.text.Inflector;
import org.scheez.util.DbC;

public class DefaultNameMapper implements NameMapper
{
    private Inflector inflector = Inflector.getInstance();
    
    @Override
    public String mapClassNameToTableName(String name)
    {
        name = mapFieldNameToColumnName(name);
        int index = name.lastIndexOf('_');
        if (index < 0)
        {
            name = inflector.pluralize(name);
        }
        else
        {
            name = name.substring(0, index + 1) + inflector.pluralize(name.substring(index + 1));
        }
        return name;
    }

    @Override
    public String mapTableNameToClassName(String name)
    {
        int index = name.lastIndexOf('_');
        if (index < 0)
        {
            name = inflector.singularize(name);
        }
        else
        {
            name = name.substring(0, index + 1) + inflector.singularize(name.substring(index + 1));
        }
        name = mapColumnNameToFieldName(name);
        return name.substring(0 , 1).toUpperCase() + name.substring(1);
    }

    @Override
    public String mapColumnNameToFieldName(String name)
    {
        DbC.throwIfNullArg(name);

        boolean first = true;
        StringTokenizer tokenizer = new StringTokenizer(name.toLowerCase(), "_");
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
    public String mapFieldNameToColumnName(String name)
    {
        DbC.throwIfNullArg(name);

        StringBuilder sb = new StringBuilder();
        boolean lastCapital = true;
        for (int index = 0; index < name.length(); index++)
        {
            char ch = name.charAt(index);
            if (Character.isUpperCase(ch))
            {
                if (!lastCapital)
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
