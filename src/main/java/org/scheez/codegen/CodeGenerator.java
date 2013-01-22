package org.scheez.codegen;

import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scheez.schema.def.ColumnType;
import org.scheez.schema.objects.Column;
import org.springframework.jdbc.core.RowMapper;

public class CodeGenerator implements RowMapper<Object>
{
    private static final Log log = LogFactory.getLog(CodeGenerator.class);
    
    private String className;
    private File sourceDir;
    private CodeTemplate codeTemplate;

    public CodeGenerator(String className, File sourceDir,
            CodeTemplate codeTemplate)
    {
        this.className = className;
        this.sourceDir = sourceDir;
        this.codeTemplate = codeTemplate;
    }

    @Override
    public Object mapRow (ResultSet rs, int rowNum) throws SQLException
    {
        if(rowNum == 0)
        {
            log.info(generateClass (rs));
        }
        
        return null;
    }

    private String generateClass (ResultSet rs) throws SQLException
    {
        ResultSetMetaData metaData = rs.getMetaData();
        List<Column> columns = new ArrayList<Column>();
        for(int colIndex = 1; colIndex <= metaData.getColumnCount(); colIndex++)
        {
            Column column = new Column(metaData.getColumnLabel(colIndex), 
                    ColumnType.getType(metaData.getColumnType(colIndex))); 
            columns.add(column);
            log.debug(column);
            log.debug(metaData.getColumnType(colIndex));
        }
        
        int index = className.lastIndexOf(".");
        String packageName = className.substring(0, index);
        String clsName = className.substring(index + 1);
        
        StringBuilder sb = new StringBuilder();
        appendOptional(sb, codeTemplate.getFileHeader(packageName, clsName), 1);
        appendRequired(sb, codeTemplate.getPackage(packageName), 2, "getPackage");
        appendOptional(sb, codeTemplate.getImports(columns), 2);
        appendOptional(sb, codeTemplate.getClassComment(clsName), 1);
        appendRequired(sb, codeTemplate.getClassDeclaration(clsName), 1, "getClassDeclaration");
        sb.append("{\n");
        appendOptional(sb, codeTemplate.getTopContent(), 2);
        for(Column column : columns)
        {
            appendOptional(sb, codeTemplate.getMemberComment(column), 1);
            appendOptional(sb, codeTemplate.getMemberVariable(column), 2);
        }
        appendOptional(sb, codeTemplate.getConstructors(columns), 2);
        for(Column column : columns)
        {
            appendOptional(sb, codeTemplate.getSetterComment(column), 1);
            appendOptional(sb, codeTemplate.getSetter(column), 2);
            appendOptional(sb, codeTemplate.getGetterComment(column), 1);
            appendOptional(sb, codeTemplate.getGetter(column), 2);
        }
        appendOptional(sb, codeTemplate.getBottomContent(), 1);
        sb.append("}\n");
        return sb.toString();
    }

    private void appendOptional (StringBuilder sb, String str, int newLineCount)
    {
        if(str != null)
        {
            sb.append(str);
            for(int index = 0; index < newLineCount; index++)
            {
                sb.append("\n");
            }
        }
    }
    
    private void appendRequired (StringBuilder sb, String str, int newLineCount, String methodName)
    {
        if(str != null)
        {
            sb.append(str);
            for(int index = 0; index < newLineCount; index++)
            {
                sb.append("\n");
            }
        }
        else
        {
            throw new IllegalArgumentException("CodeTemplate." + methodName + " is required to return a non-null value.");
        }
        
    }
}
