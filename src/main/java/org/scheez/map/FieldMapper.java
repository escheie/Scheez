package org.scheez.map;

public interface FieldMapper
{
    
    
    String mapField (String fieldName);
    
    /*@Override
    public String mapField (String fieldName)
    {
        StringBuilder sb = new StringBuilder();
        boolean lastCapital = true;
        for(int index = 0; index < fieldName.length(); index++)
        {
            char ch = fieldName.charAt(index);
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
    }*/
}
