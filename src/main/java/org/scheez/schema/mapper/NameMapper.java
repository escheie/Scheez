package org.scheez.schema.mapper;


public interface NameMapper
{
    String mapDatabaseNameToJavaName (String name);
    
    String mapJavaNameToDatabaseName (String name);
}
