package org.scheez.classgen;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.scheez.map.UnderscoreToCamelCaseColumnMapper;
import org.scheez.map.ColumnMapper;
import org.scheez.schema.objects.Column;

public class DefaultClassTemplate implements ClassTemplate
{
    private ColumnMapper columnMapper;
   
    public DefaultClassTemplate()
    {
        this(new UnderscoreToCamelCaseColumnMapper());
    }

    public DefaultClassTemplate (ColumnMapper columnMapper)
    {
        this.columnMapper = columnMapper;
    }
    
    @Override
    public String getFileHeader(String packageName, String clsName)
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
            columnClasses.add(column.getType().getJavaClass());
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
    public String getClassComment(String clsName)
    {
        return "/**\n"
                + " * This class was auto generated from the contents of a ResultSet using scheez.\n"
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
        return "    private " + column.getType().getJavaClass().getSimpleName()
                + " " + columnMapper.mapColumn(column.getName()) + ";";
    }

    @Override
    public String getConstructors(List<Column> columns)
    {
        return null;
    }

    @Override
    public String getSetterComment(Column column)
    {
        String columnName = columnMapper.mapColumn(column.getName());
        return "    /**\n" + "     * Setter for " + columnName + ".\n"
                + "     *\n" + "     * @param " + columnName
                + "  The value to set.\n" + "     */";
    }

    @Override
    public String getSetter(Column column)
    {
        String columnName = columnMapper.mapColumn(column.getName());
        return "    public void set"
                + Character.toUpperCase(columnName.charAt(0))
                + columnName.substring(1) + "("
                + column.getType().getJavaClass().getSimpleName() + " "
                + columnName + ")\n" +
                "    {\n" +
                "        this." + columnName + " = " + columnName + ";\n" +
                "    }";
    }

    @Override
    public String getGetterComment(Column column)
    {
        String columnName = columnMapper.mapColumn(column.getName());
        return "    /**\n" + "     * Getter for " + columnName + ".\n"
                + "     *\n" + "     * @return The value of "
                + columnName + ".\n" + "     */";
    }

    @Override
    public String getGetter(Column column)
    {
        String columnName = columnMapper.mapColumn(column.getName());
        return "    public " + column.getType().getJavaClass().getSimpleName() + " get"
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
