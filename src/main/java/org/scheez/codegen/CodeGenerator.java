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

public class CodeGenerator implements RowMapper
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
        sb.append(codeTemplate.getFileHeader(packageName, clsName));
        sb.append(codeTemplate.getPackage(packageName));
        sb.append("\n\n");
        sb.append(codeTemplate.getImports(columns));
        sb.append("\n\n");
        sb.append(codeTemplate.getClassComment(clsName));
        sb.append("\n");
        sb.append(codeTemplate.getClassDefinition(clsName));
        sb.append("\n{\n");
        sb.append(codeTemplate.getTopContent());
        sb.append("\n\n");
        for(Column column : columns)
        {
            sb.append(codeTemplate.getMemberComment(column));
            sb.append("\n");
            sb.append(codeTemplate.getMemberVariable(column));
            sb.append("\n\n");
        }
        sb.append(codeTemplate.getConstructors(columns));
        sb.append("\n\n");
        for(Column column : columns)
        {
            sb.append(codeTemplate.getSetterComment(column));
            sb.append("\n");
            sb.append(codeTemplate.getSetter(column));
            sb.append("\n\n");
            sb.append(codeTemplate.getGetterComment(column));
            sb.append("\n");
            sb.append(codeTemplate.getGetter(column));
            sb.append("\n\n");
        }
        sb.append(codeTemplate.getBottomContent());
        sb.append("\n}\n");
        return sb.toString();
    }
}
