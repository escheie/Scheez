package org.scheez.schema.diff;

import org.scheez.schema.dao.SchemaDao;

/**
 * @author Eric
 */
public class MissingSchema extends SchemaDifferenceTable
{
    private String schemaName;

    public MissingSchema(String schemaName)
    {
        super(null, null);
        this.schemaName = schemaName;
    }

    @Override
    public Type getType()
    {
        return Type.MISSING_SCHEMA;
    }

    @Override
    public String getDescription()
    {
        return "Missing schema \"" + schemaName + "\".";
    }

    @Override
    public void resolveDifference(SchemaDao schemaDao)
    {
        schemaDao.createSchema(schemaName);
    }
}
