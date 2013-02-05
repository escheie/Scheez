package org.scheez.schema.classgen;

import java.util.List;

import org.scheez.schema.parts.Column;
import org.scheez.schema.parts.TableName;

public interface ClassTemplate
{
    String getClassName (TableName tableName);
    
    String getFileHeader (String packageName, String clsName, TableName tableName);
    
    String getPackage (String packageName);
    
    String getImports (List<Column> columns);
    
    String getClassComment (String clsName, TableName tableName);
    
    String getClassDeclaration (String clsName);
    
    String getTopContent ();
    
    String getMemberComment (Column column);
    
    String getMemberVariable (Column column);
    
    String getConstructors (List<Column> columns);
    
    String getSetterComment (Column column);
    
    String getSetter (Column column);
    
    String getGetterComment (Column column);
    
    String getGetter (Column column);
    
    String getBottomContent();
}
