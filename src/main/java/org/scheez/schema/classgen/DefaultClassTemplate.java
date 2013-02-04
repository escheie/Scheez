package org.scheez.schema.classgen;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.scheez.schema.mapper.DefaultNameMapper;
import org.scheez.schema.mapper.NameMapper;
import org.scheez.schema.objects.Column;
import org.scheez.schema.objects.TableName;

public class DefaultClassTemplate implements ClassTemplate
{
    private NameMapper nameMapper;
   
    public DefaultClassTemplate()
    {
        this(new DefaultNameMapper());
    }

    public DefaultClassTemplate (NameMapper nameMapper)
    {
        this.nameMapper = nameMapper;
    }
    
    @Override
    public String getClassName (TableName tableName)
    {
        String clsName = nameMapper.mapDatabaseNameToJavaName(tableName.getTableName());
        return clsName.substring(0 , 1).toUpperCase() + clsName.substring(1);
    }

    @Override
    public String getFileHeader(String packageName, String clsName, TableName tableName)
    {
        return "/**\n" + " * " + clsName + ".java\n" + " */";
    }

    @Override
    public String getPackage(String packageName)
    {
        return "package " + packageName + ";";
    }

    @Override
    public String getImports(List<Column> columns)
    {
        StringBuilder sb = new StringBuilder("import org.scheez.util.BaseObject;\n");
        Set<Class<?>> columnClasses = new HashSet<Class<?>>();
        for (Column column : columns)
        {
            columnClasses.add(column.getType().getTypeClass());
        }
        for (Class<?> cls : columnClasses)
        {
            if(!cls.getPackage().getName().equals("java.lang"))
            {
                sb.append("import ");
                sb.append(cls.getName());
                sb.append(";\n");
            }
        }
        return sb.toString();
    }

    @Override
    public String getClassComment(String clsName, TableName tableName)
    {
        return "/**\n" + ((tableName == null) ?
                " * This class was auto generated from the contents of a ResultSet.\n" :
                " * This class was auto generated from the table named \"" + tableName + "\".\n" )       
                + " *\n" + " * @author : " + System.getProperty("user.name")
                + "\n" + " * @version : $Id$\n" + " */";
    }

    @Override
    public String getClassDeclaration(String clsName)
    {
        return "public class " + clsName + " extends BaseObject";
    }

    @Override
    public String getTopContent()
    {
        return null;
    }

    @Override
    public String getMemberComment(Column column)
    {
        return null;
    }

    @Override
    public String getMemberVariable(Column column)
    {
        return "    private " + column.getType().getTypeClass().getSimpleName()
                + " " + nameMapper.mapDatabaseNameToJavaName(column.getName()) + ";";
    }

    @Override
    public String getConstructors(List<Column> columns)
    {
        return null;
    }

    @Override
    public String getSetterComment(Column column)
    {
        String columnName = nameMapper.mapDatabaseNameToJavaName(column.getName());
        return "    /**\n" + "     * Setter for " + columnName + ".\n"
                + "     *\n" + "     * @param " + columnName
                + "  The value to set.\n" + "     */";
    }

    @Override
    public String getSetter(Column column)
    {
        String columnName = nameMapper.mapDatabaseNameToJavaName(column.getName());
        return "    public void set"
                + Character.toUpperCase(columnName.charAt(0))
                + columnName.substring(1) + "("
                + column.getType().getTypeClass().getSimpleName() + " "
                + columnName + ")\n" +
                "    {\n" +
                "        this." + columnName + " = " + columnName + ";\n" +
                "    }";
    }

    @Override
    public String getGetterComment(Column column)
    {
        String columnName = nameMapper.mapDatabaseNameToJavaName(column.getName());
        return "    /**\n" + "     * Getter for " + columnName + ".\n"
                + "     *\n" + "     * @return The value of "
                + columnName + ".\n" + "     */";
    }

    @Override
    public String getGetter(Column column)
    {
        String columnName = nameMapper.mapDatabaseNameToJavaName(column.getName());
        return "    public " + column.getType().getTypeClass().getSimpleName() + " get"
                + Character.toUpperCase(columnName.charAt(0))
                + columnName.substring(1) + "()\n" +
                "    {\n" +
                "        return " + columnName + ";\n" +
                "    }";
    }

    @Override
    public String getBottomContent()
    {
        return null;
    }
}
