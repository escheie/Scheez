package org.scheez.schema.mapper;

import java.lang.reflect.Field;

import org.scheez.schema.parts.Column;

public interface FieldMapper
{
    Column mapField (Field field);
    
    Field mapField (Class<?> cls, String name);
}
