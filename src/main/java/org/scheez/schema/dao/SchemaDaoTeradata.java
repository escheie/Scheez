package org.scheez.schema.dao;

import javax.sql.DataSource;

import org.scheez.schema.def.ColumnType;
import org.scheez.schema.model.Column;
import org.scheez.schema.model.Index;
import org.scheez.schema.model.TableName;
import org.scheez.util.DbC;

public class SchemaDaoTeradata extends SchemaDaoAnsi
{
    /**
     * DEFAULT perm space for new databases is 1 GB.
     */
    private static long DEFAULT_PERM_SPACE = 1000000000;
    
    private long defaultPermSpace = DEFAULT_PERM_SPACE;

    public SchemaDaoTeradata(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public void createSchema(String schemaName)
    {
        StringBuilder sb = new StringBuilder("CREATE DATABASE ");
        sb.append(schemaName);
        sb.append(" AS PERMANENT = ");
        sb.append(defaultPermSpace);
        sb.append(" BYTES");
        execute(sb.toString());
    }

    @Override
    public void dropSchema(String schemaName)
    {
        StringBuilder sb = new StringBuilder("DELETE DATABASE ");
        sb.append(schemaName);
        execute(sb.toString());
        sb = new StringBuilder("DROP DATABASE ");
        sb.append(schemaName);
        execute(sb.toString());
    }
    

    /** 
     * @inheritDoc
     */
    @Override
    public boolean schemaExists(String schemaName)
    {
        boolean exists = false;
        for (String schema : getSchemas())
        {
            if(schema.equalsIgnoreCase(schemaName))
            {
                exists = true;
                break;
            }
        }
        return exists;
    }
    
    @Override
    public void alterColumnType(TableName tableName, Column column)
    {
        StringBuilder sb = new StringBuilder("ALTER TABLE ");
        sb.append(tableName);
        sb.append(" ADD ");
        sb.append(getColumnString(column));
        execute(sb.toString());
    }
    
    @Override
    public ColumnType getExpectedColumnType(ColumnType columnType)
    {
        if(columnType == ColumnType.DOUBLE)
        {
            columnType = ColumnType.FLOAT;
        }
        else if(columnType == ColumnType.BOOLEAN)
        {
            columnType = ColumnType.TINYINT;
        }
        return columnType;
    }
    
    @Override
    public void addIndex(TableName tableName, Index index)
    {
        StringBuilder sb = new StringBuilder("CREATE INDEX ");
        sb.append(index.getName());
        sb.append(" (");
        boolean first = true;
        for (String columnName : index.getColumnNames())
        {
            if (first)
            {
                first = false;
            }
            else
            {
                sb.append(", ");
            }
            sb.append(columnName);
        }
        sb.append(") ON ");
        sb.append(tableName);
        execute(sb.toString());
    }
    
    @Override
    public void dropIndex(TableName tableName, String indexName)
    {
        StringBuilder sb = new StringBuilder("DROP INDEX ");
        sb.append(indexName);
        sb.append(" ON ");
        sb.append(tableName);
        execute(sb.toString());
    }

    
    @Override
    protected String getColumnTypeString(Column column)
    {
        String typeStr = null;
        switch (column.getType())
        {
            case TINYINT:
                typeStr = "BYTEINT";
                break;
            case DOUBLE:
                typeStr = "DOUBLE PRECISION";
                break;
            case BOOLEAN:
                typeStr = "BYTEINT";
                break;
            case BINARY:
                typeStr = "BLOB";
                break;
            default:
                typeStr = super.getColumnTypeString(column);
                
        }
        return typeStr;
    }

    /**
     * @return the defaultPermSpace
     */
    public long getDefaultPermSpace()
    {
        return defaultPermSpace;
    }

    /**
     * @param defaultPermSpace
     *            the defaultPermSpace to set
     */
    public void setDefaultPermSpace(long defaultPermSpace)
    {
        this.defaultPermSpace = defaultPermSpace;
    }

    public static class Factory extends SchemaDaoFactory
    {

        @Override
        public boolean isSupported(String databaseProduct, String databaseVersion)
        {
            DbC.throwIfNullArg(databaseProduct);
            return databaseProduct.equalsIgnoreCase("Teradata");
        }

        @Override
        public SchemaDao create (DataSource dataSource)
        {
            DbC.throwIfNullArg(dataSource);
            return new SchemaDaoTeradata (dataSource);
        }

    }
}
