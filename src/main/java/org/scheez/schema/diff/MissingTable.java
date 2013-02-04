package org.scheez.schema.diff;

public class MissingTable extends AbstractSchemaDifference
{
    private String tableName;

    private Class<?> cls;

    public MissingTable(String tableName, Class<?> cls)
    {
        super();
        this.tableName = tableName;
        this.cls = cls;
    }

    @Override
    public Type getType()
    {
        return Type.MISSING_TABLE;
    }

    @Override
    public String getMessage()
    {
        return "Missing table \"" + tableName + "\" for class " + cls.getName() + ".";
    }

    @Override
    public String getTableName()
    {
        return tableName;
    }

    public Class<?> getTableClass ()
    {
        return cls;
    }

}
