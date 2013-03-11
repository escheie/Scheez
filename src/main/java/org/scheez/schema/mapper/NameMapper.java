package org.scheez.schema.mapper;


public interface NameMapper
{ 
    String mapClassNameToTableName (String name);
    
    String mapTableNameToClassName (String name);
    
    String mapColumnNameToFieldName (String name);
    
    String mapFieldNameToColumnName (String name);
}
