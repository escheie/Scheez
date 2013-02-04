package org.scheez.schema.diff;

import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.objects.Table;

public class MissingTable extends SchemaDifferenceTable
{
    public MissingTable(Table table, Class<?> tableClass)
    {
        super(table, tableClass);
    }

    @Override
    public Type getType()
    {
        return Type.MISSING_TABLE;
    }

    @Override
    public String getDescription()
    {
        return "Missing table \"" + table.getTableName() + "\" for class " + tableClass.getName() + ".";
    }

    @Override
    public void resolveDifference (SchemaDao schemaDao)
    {
        schemaDao.createTable(table);
    }
}
