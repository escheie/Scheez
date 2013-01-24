package org.scheez.classgen;

import java.util.List;

import org.scheez.schema.objects.Column;

public interface ClassTemplate
{
    String getFileHeader (String packageName, String clsName);
    
    String getPackage (String packageName);
    
    String getImports (List<Column> columns);
    
    String getClassComment (String clsName);
    
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
