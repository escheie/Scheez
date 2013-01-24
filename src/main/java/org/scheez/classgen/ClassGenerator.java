package org.scheez.classgen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scheez.schema.def.ColumnType;
import org.scheez.schema.objects.Column;
import org.scheez.schema.objects.Table;

public class ClassGenerator 
{
    private static final Log log = LogFactory.getLog(ClassGenerator.class);

    private File sourceDir;

    private ClassTemplate codeTemplate;

    public ClassGenerator(File sourceDir, ClassTemplate codeTemplate)
    {
        this.sourceDir = sourceDir;
        this.codeTemplate = codeTemplate;
    }

    public void generateClass(String fullyQualifiedClass, Table table) throws IOException
    {
        generateClass(fullyQualifiedClass, table.getColumns());
    }

    public void generateClass(String fullyQualifiedClass, ResultSet resultSet) throws SQLException
    {
        ResultSetMetaData metaData = resultSet.getMetaData();
        List<Column> columns = new ArrayList<Column>();
        for (int colIndex = 1; colIndex <= metaData.getColumnCount(); colIndex++)
        {
            Column column = new Column(metaData.getColumnLabel(colIndex),
                    ColumnType.getType(metaData.getColumnType(colIndex)));
            columns.add(column);
            log.debug(column);
            log.debug(metaData.getColumnType(colIndex));
        }
        try
        {
            generateClass(fullyQualifiedClass, columns);
        }
        catch (IOException e)
        {
            throw new SQLException("Unable to generate class.", e.getMessage());
        }
    }

    private void generateClass(String fullyQualifiedClass, List<Column> columns) throws IOException
    {
        int index = fullyQualifiedClass.lastIndexOf(".");
        String packageName = fullyQualifiedClass.substring(0, index);
        String clsName = fullyQualifiedClass.substring(index + 1);
        StringBuilder sb = new StringBuilder();
        appendOptional(sb, codeTemplate.getFileHeader(packageName, clsName), 1);
        appendRequired(sb, codeTemplate.getPackage(packageName), 2,
                "getPackage");
        appendOptional(sb, codeTemplate.getImports(columns), 2);
        appendOptional(sb, codeTemplate.getClassComment(clsName), 1);
        appendRequired(sb, codeTemplate.getClassDeclaration(clsName), 1,
                "getClassDeclaration");
        sb.append("{\n");
        appendOptional(sb, codeTemplate.getTopContent(), 2);
        for (Column column : columns)
        {
            appendOptional(sb, codeTemplate.getMemberComment(column), 1);
            appendOptional(sb, codeTemplate.getMemberVariable(column), 2);
        }
        appendOptional(sb, codeTemplate.getConstructors(columns), 2);
        for (Column column : columns)
        {
            appendOptional(sb, codeTemplate.getSetterComment(column), 1);
            appendOptional(sb, codeTemplate.getSetter(column), 2);
            appendOptional(sb, codeTemplate.getGetterComment(column), 1);
            appendOptional(sb, codeTemplate.getGetter(column), 2);
        }
        appendOptional(sb, codeTemplate.getBottomContent(), 1);
        sb.append("}\n");
        save(fullyQualifiedClass, sb.toString());
    }

    protected void save(String fullyQualifiedClass, String classDefinition) throws IOException
    {
        if (log.isDebugEnabled())
        {
            log.debug(classDefinition);
        }
        BufferedWriter writer = null;
        try
        {
            writer = new BufferedWriter(new FileWriter(getFile(fullyQualifiedClass)));
            StringTokenizer tokenizer = new StringTokenizer(classDefinition, "\n", true);
            while (tokenizer.hasMoreTokens())
            {
                String token = tokenizer.nextToken();
                if(token.equals("\n"))
                {
                    writer.newLine();
                }
                else
                {
                    writer.write(token);
                }
            }
        }
        finally
        {
            if (writer != null)
            {
                writer.close();
            }
        }
    }

    protected File getFile(String fullyQualifiedClass)
    {
        File file = sourceDir;
        StringTokenizer tokenizer = new StringTokenizer(fullyQualifiedClass, ".");
        while(tokenizer.hasMoreTokens())
        {
            file.mkdir();
            file = new File(file, tokenizer.nextToken());
        }
        file = new File(file.getParentFile(), file.getName() + ".java");
        return file;
    }

    private void appendOptional(StringBuilder sb, String str, int newLineCount)
    {
        if (str != null)
        {
            sb.append(str);
            for (int index = 0; index < newLineCount; index++)
            {
                sb.append("\n");
            }
        }
    }

    private void appendRequired(StringBuilder sb, String str, int newLineCount,
            String methodName)
    {
        if (str != null)
        {
            sb.append(str);
            for (int index = 0; index < newLineCount; index++)
            {
                sb.append("\n");
            }
        }
        else
        {
            throw new IllegalArgumentException("CodeTemplate." + methodName
                    + " is required to return a non-null value.");
        }
    }
}
