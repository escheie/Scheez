package org.scheez.schema.diff;

import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.objects.Table;

public class UnknownTable extends SchemaDifferenceTable
{
    public UnknownTable(Table table)
    {
        super(table, null);
    }

    @Override
    public Type getType()
    {
        return Type.UNKNOWN_TABLE;
    }

    @Override
    public String getDescription()
    {
        return "Unknown table \"" + table.getTableName() + "\".  No matching class found.";
    }

    @Override
    public void resolveDifference(SchemaDao schemaDao)
    {
        schemaDao.dropTable(table.getTableName());
    }
}
