package org.scheez.schema.classgen;

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
import org.scheez.schema.model.Column;
import org.scheez.schema.model.Table;
import org.scheez.schema.model.TableName;
import org.scheez.util.DbC;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

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

    public GeneratedClass generateClass(String pkgName, Table table) throws IOException
    {
        return generateClass(getFullyQualifiedClassName(pkgName, codeTemplate.getClassName(table.getTableName())),
                table.getTableName(), table.getColumns());
    }

    public GeneratedClass generateClass(String pkgName, String clsName, Table table) throws IOException
    {
        return generateClass(getFullyQualifiedClassName(pkgName, clsName), table.getTableName(), table.getColumns());
    }

    public GeneratedClass generateClass(String pkgName, TableName tableName, ResultSet resultSet) throws SQLException
    {
        return generateClass(getFullyQualifiedClassName(pkgName, codeTemplate.getClassName(tableName)), resultSet, tableName);
    }

    public GeneratedClass generateClass(String fullyQualifiedClassName, ResultSet resultSet) throws SQLException
    {
        return generateClass(fullyQualifiedClassName, resultSet, null);
    }
    
    private GeneratedClass generateClass (String fullyQualifiedClassName, ResultSet resultSet, TableName tableName) throws SQLException
    {
        ResultSetMetaData metaData = resultSet.getMetaData();
        List<Column> columns = new ArrayList<Column>();
        for (int colIndex = 1; colIndex <= metaData.getColumnCount(); colIndex++)
        {
            Column column = new Column(metaData.getColumnLabel(colIndex), ColumnType.getType(metaData
                    .getColumnType(colIndex)));
            columns.add(column);
            log.debug(column);
            log.debug(metaData.getColumnType(colIndex));
        }
        try
        {
            return generateClass(fullyQualifiedClassName, tableName, columns);
        }
        catch (IOException e)
        {
            throw new SQLException("Unable to generate class.", e);
        }
    }
    
    public ResultSetExtractor<GeneratedClass> generateClass(final String pkgName, final TableName tableName)
    {
        return new ResultSetExtractor<GeneratedClass>()
        {

            @Override
            public GeneratedClass extractData(ResultSet rs) throws SQLException, DataAccessException
            {
                return generateClass(getFullyQualifiedClassName(pkgName, codeTemplate.getClassName(tableName)), rs, tableName);
            }

        };
    }

    public ResultSetExtractor<GeneratedClass> generateClass(final String fullyQualifiedClassName)
    {
        return new ResultSetExtractor<GeneratedClass>()
        {

            @Override
            public GeneratedClass extractData(ResultSet rs) throws SQLException, DataAccessException
            {
                return generateClass(fullyQualifiedClassName, rs, null);
            }

        };
    }

    private String getFullyQualifiedClassName(String pkg, String className)
    {
        DbC.throwIfNullArg(pkg, className);
        StringBuilder clsName = new StringBuilder(pkg);
        if (!pkg.endsWith("."))
        {
            clsName.append(".");
        }
        clsName.append(className);
        return clsName.toString();
    }

    private GeneratedClass generateClass(String fullyQualifiedClass, TableName tableName, List<Column> columns)
            throws IOException
    {
        int index = fullyQualifiedClass.lastIndexOf(".");
        String packageName = fullyQualifiedClass.substring(0, index);
        String clsName = fullyQualifiedClass.substring(index + 1);
        StringBuilder sb = new StringBuilder();
        appendOptional(sb, codeTemplate.getFileHeader(packageName, clsName, tableName), 1);
        appendRequired(sb, codeTemplate.getPackage(packageName), 2, "getPackage");
        appendOptional(sb, codeTemplate.getImports(columns), 2);
        appendOptional(sb, codeTemplate.getClassComment(clsName, tableName), 1);
        appendRequired(sb, codeTemplate.getClassDeclaration(clsName), 1, "getClassDeclaration");
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
        return save(fullyQualifiedClass, sb.toString());
    }

    protected GeneratedClass save(String fullyQualifiedClassName, String classDefinition) throws IOException
    {
        if (log.isDebugEnabled())
        {
            log.debug(classDefinition);
        }
        BufferedWriter writer = null;
        File file = getFile(fullyQualifiedClassName);
        try
        {
            writer = new BufferedWriter(new FileWriter(file));
            StringTokenizer tokenizer = new StringTokenizer(classDefinition, "\n", true);
            while (tokenizer.hasMoreTokens())
            {
                String token = tokenizer.nextToken();
                if (token.equals("\n"))
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

        return new GeneratedClass(file, fullyQualifiedClassName);
    }

    protected File getFile(String fullyQualifiedClassName)
    {
        File file = sourceDir;
        StringTokenizer tokenizer = new StringTokenizer(fullyQualifiedClassName, ".");
        while (tokenizer.hasMoreTokens())
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

    private void appendRequired(StringBuilder sb, String str, int newLineCount, String methodName)
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
