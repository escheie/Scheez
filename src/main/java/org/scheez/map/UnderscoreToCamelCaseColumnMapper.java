package org.scheez.map;

import java.util.StringTokenizer;

public class UnderscoreToCamelCaseColumnMapper implements ColumnMapper
{
    @Override
    public String mapColumn(String columnName)
    {
        boolean first = true;
        StringTokenizer tokenizer = new StringTokenizer(
                columnName.toLowerCase(), "_");
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
}
