package org.scheez.schema.mapper;

import org.scheez.reflect.PersistentClass;
import org.scheez.reflect.PersistentField;
import org.scheez.schema.model.Column;
import org.scheez.schema.model.Table;
import org.scheez.schema.model.TableName;

public interface SchemaMapper
{     
    String mapClassToTableName (PersistentClass cls);
    
    Table mapClassToTable (TableName tableName, PersistentClass cls);
    
    Column mapFieldToColumn (Table table, PersistentField field);
    
    PersistentField mapColumnToField (PersistentClass cls, String columnName);
}
