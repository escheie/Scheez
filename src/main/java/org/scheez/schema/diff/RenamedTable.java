package org.scheez.schema.diff;

import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.model.Table;
import org.scheez.schema.model.TableName;

public class RenamedTable extends SchemaDifferenceTable
{
    private String newName;
    
    public RenamedTable(Table table, Class<?> tableClass, String newName)
    {
        super(table, tableClass);
        this.newName = newName;
    }

    @Override
    public Type getType()
    {
        return Type.RENAMED_TABLE;
    }

    @Override
    public String getDescription()
    {
        return "Missing Table \"" + table.getTableName() + "\" renamed to \"" + newName + "\".";
    }

    @Override
    public void resolveDifference(SchemaDao schemaDao)
    {
        schemaDao.renameTable(table.getTableName(), new TableName(table.getSchemaName(), newName));
    }

}
