package org.scheez.schema.dao.impl;

import javax.sql.DataSource;

import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.dao.SchemaDaoFactory;
import org.scheez.schema.parts.TableName;
import org.scheez.util.DbC;
import org.springframework.jdbc.core.JdbcTemplate;

public class SchemaDaoHsqldb extends SchemaDaoAnsi
{
    public SchemaDaoHsqldb(DataSource dataSource)
    {
        super(dataSource);
    }

    public SchemaDaoHsqldb(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
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
            return new SchemaDaoHsqldb (dataSource);
        }

    }

}
