package org.scheez.schema.mapper;

import java.lang.reflect.Field;

public interface FieldMapper
{
    String mapField (Field field);
    
    Field mapField (Class<?> cls, String name);
}
