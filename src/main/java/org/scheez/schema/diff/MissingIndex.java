package org.scheez.schema.diff;

import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.model.Index;
import org.scheez.schema.model.Table;

public class MissingIndex extends SchemaDifferenceTable
{
    private Index index;

    public MissingIndex(Table table, Class<?> tableClass, Index index)
    {
        super(table, tableClass);
        this.index = index;
    }

    /**
     * @return the index
     */
    public Index getIndex()
    {
        return index;
    }

    @Override
    public Type getType()
    {
        return Type.MISSING_INDEX;
    }

    @Override
    public String getDescription()
    {
        return "Missing index \"" + index.getName() + "\" on table \"" + table.getTableName() + "\".";
    }

    @Override
    public void reconcileDifferences(SchemaDao schemaDao)
    {
        schemaDao.addIndex(table.getTableName(), index);
    }
}
