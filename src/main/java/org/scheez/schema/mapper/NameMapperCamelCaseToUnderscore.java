package org.scheez.schema.mapper;

public class NameMapperCamelCaseToUnderscore implements NameMapper
{
    @Override
    public String mapName (String name)
    {
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
        }
        return sb.toString();
    }
}
