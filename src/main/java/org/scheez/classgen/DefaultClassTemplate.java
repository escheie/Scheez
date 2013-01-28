package org.scheez.classgen;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.scheez.schema.objects.Column;

public class DefaultClassTemplate implements ClassTemplate
{
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
                + " " + column.getName() + ";";
    }

    @Override
    public String getConstructors(List<Column> columns)
    {
        return null;
    }

    @Override
    public String getSetterComment(Column column)
    {
        return "    /**\n" + "     * Setter for " + column.getName() + ".\n"
                + "     *\n" + "     * @param " + column.getName()
                + "  The value to set.\n" + "     */";
    }

    @Override
    public String getSetter(Column column)
    {
        return "    public void set"
                + Character.toUpperCase(column.getName().charAt(0))
                + column.getName().substring(1) + "("
                + column.getType().getJavaClass().getSimpleName() + " "
                + column.getName() + ")\n" +
                "    {\n" +
                "        this." + column.getName() + " = " + column.getName() + ";\n" +
                "    }";
    }

    @Override
    public String getGetterComment(Column column)
    {
        return "    /**\n" + "     * Getter for " + column.getName() + ".\n"
                + "     *\n" + "     * @return The value of "
                + column.getName() + ".\n" + "     */";
    }

    @Override
    public String getGetter(Column column)
    {
        return "    public " + column.getType().getJavaClass().getSimpleName() + " get"
                + Character.toUpperCase(column.getName().charAt(0))
                + column.getName().substring(1) + "()\n" +
                "    {\n" +
                "        return " + column.getName() + ";\n" +
                "    }";
    }

    @Override
    public String getBottomContent()
    {
        return null;
    }
}
