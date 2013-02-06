package org.scheez.schema.mapper;

import java.util.List;

import org.scheez.reflect.PersistentField;
import org.scheez.schema.parts.Column;
import org.scheez.schema.parts.Table;
import org.scheez.schema.parts.TableName;

public interface SchemaMapper
{ 
    String mapClassToTableName (Class<?> cls);
    
    Table mapClassToTable (TableName tableName, Class<?> cls);
    
    Column mapFieldToColumn (PersistentField field);
    
    PersistentField mapColumnToField (Class<?> cls, String columnName);
    
    List<PersistentField> getPersistentFields (Class<?> cls);
}
