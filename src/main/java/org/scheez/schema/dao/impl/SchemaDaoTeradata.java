package org.scheez.schema.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.dao.SchemaDaoFactory;
import org.scheez.schema.parts.TableName;
import org.scheez.util.DbC;
import org.springframework.jdbc.core.JdbcTemplate;

public class SchemaDaoTeradata extends SchemaDaoAnsi 
{
    public SchemaDaoTeradata(DataSource dataSource)
    {
        super(dataSource);
    }

    public SchemaDaoTeradata(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
    }
    
    @Override
    public void createSchema(String schemaName)
    {
        StringBuilder sb = new StringBuilder("CREATE DATABASE ");
        sb.append(schemaName);
        jdbcTemplate.execute(sb.toString());
    }

    @Override
    public void dropSchema(String schemaName)
    {
        StringBuilder sb = new StringBuilder("DROP DATABASE ");
        sb.append(schemaName);
        jdbcTemplate.execute(sb.toString());
    }

    @Override
    public boolean schemaExists(final String schemaName)
    {
        return catalogExists(schemaName);
    }

    @Override
    public List<String> getSchemas()
    {
        return getCatalogs();
    }

    @Override
    protected String getCatalogName(TableName tableName)
    {
        return tableName.getSchemaName();
    }

    @Override
    protected String getSchemaName(TableName tableName)
    {
        return null;
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
        public SchemaDao create (JdbcTemplate jdbcTemplate)
        {
            DbC.throwIfNullArg(jdbcTemplate);
            return new SchemaDaoTeradata (jdbcTemplate);
        }

    }
}
