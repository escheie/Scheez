package org.scheez.schema.diff;

import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.model.Table;

public class UnknownIndex extends SchemaDifferenceTable
{
    private String indexName;
    
    public UnknownIndex(Table table, String indexName)
    {
        super(table, null);
        this.indexName = indexName;
    }

    @Override
    public Type getType()
    {
        return Type.UNKNOWN_INDEX;
    }

    @Override
    public String getDescription()
    {
        return "Unknown index \"" + indexName + "\" on table \"" + table.getTableName() + "\".";
    }

    @Override
    public void reconcileDifferences(SchemaDao schemaDao)
    {
        schemaDao.dropIndex(table.getTableName(), indexName);
    }
}
