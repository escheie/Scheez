package org.scheez.codegen;

import java.util.List;

import org.scheez.schema.objects.Column;

public class DefaultCodeTemplate implements CodeTemplate
{
    @Override
    public String getFileHeader(String packageName, String clsName)
    {
        return "/**\n" +
        	   " * " + clsName + ".java\n" +
               " */";
    }

    @Override
    public String getPackage(String packageName)
    {
        return "package " + packageName + ";";
    }

    @Override
    public String getImports(List<Column> columns)
    {
        return "import org.scheez.util.BaseObject";
    }

    @Override
    public String getClassComment(String clsName)
    {
        return "/**\n" +
               " * " + clsName + "was auto generated from a SQL query using scheez.\n" +
               " * @author : " + System.getProperty("user.name") + "\n" + 
               " * @version : $Id$\n" +
               " */";
    }

    @Override
    public String getClassDefinition(String clsName)
    {
        return "public class " + clsName + " extends BaseObject";
    }

    @Override
    public String getTopContent()
    {
        return "";
    }

    @Override
    public String getMemberComment(Column column)
    {
        return "";
    }

    @Override
    public String getMemberVariable(Column column)
    {
        return "    private " + column.getType().getJavaClass().getSimpleName() + " " + column.getName() + ";";
    }

    @Override
    public String getConstructors(List<Column> columns)
    {
        return "";
    }

    @Override
    public String getSetterComment(Column column)
    {
        return "";
    }

    @Override
    public String getSetter(Column column)
    {
        return "";
    }

    @Override
    public String getGetterComment(Column column)
    {
        return "";
    }

    @Override
    public String getGetter(Column column)
    {
        return "";
    }

    @Override
    public String getBottomContent()
    {
        return "";
    }
}
