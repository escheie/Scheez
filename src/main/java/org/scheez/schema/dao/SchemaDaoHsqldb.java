package org.scheez.schema.dao;

import javax.sql.DataSource;

import org.scheez.schema.def.ColumnType;
import org.scheez.schema.model.TableName;
import org.scheez.util.DbC;

public class SchemaDaoHsqldb extends SchemaDaoAnsi
{
    public SchemaDaoHsqldb(DataSource dataSource)
    {
        super(dataSource);
    }
    
    @Override
    public ColumnType getExpectedColumnType(ColumnType columnType)
    {
        if (columnType == ColumnType.FLOAT)
        {
            columnType = ColumnType.DOUBLE;
        }
        return columnType;
    }

    @Override
    protected String getSchemaName(TableName tableName)
    {
       return tableName.toUpperCase().getSchemaName();
    }

    @Override
    protected String getTableName(TableName tableName)
    {
        return tableName.toUpperCase().getTableName();
    }

    @Override
    protected String getColumnName(String columnName)
    {
        return columnName.toUpperCase();
    }

    public static class Factory extends SchemaDaoFactory
    {

        @Override
        public boolean isSupported(String databaseProduct, String databaseVersion)
        {
            DbC.throwIfNullArg(databaseProduct);
            return databaseProduct.trim().startsWith("HSQL");
        }

        @Override
        public SchemaDao create (DataSource dataSource)
        {
            DbC.throwIfNullArg(dataSource);
            return new SchemaDaoHsqldb (dataSource);
        }

    }

}
