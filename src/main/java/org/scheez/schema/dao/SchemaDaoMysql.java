package org.scheez.schema.dao;

import java.util.List;

import javax.sql.DataSource;

import org.scheez.schema.def.ColumnType;
import org.scheez.schema.model.Column;
import org.scheez.schema.model.TableName;
import org.scheez.util.DbC;
import org.springframework.jdbc.core.JdbcTemplate;

public class SchemaDaoMysql extends SchemaDaoAnsi
{
    public SchemaDaoMysql(DataSource dataSource)
    {
        super(dataSource);
    }

    public SchemaDaoMysql(JdbcTemplate jdbcTemplate)
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

    @Override
    public void alterColumnType(TableName tableName, Column column)
    {
        StringBuilder sb = new StringBuilder("ALTER TABLE ");
        sb.append(tableName);
        sb.append(" MODIFY COLUMN ");
        sb.append(getColumnString(column));
        jdbcTemplate.execute(sb.toString());
    }
    
    @Override
    public void dropIndex(TableName tableName, String indexName)
    {
        StringBuilder sb = new StringBuilder("DROP INDEX ");
        sb.append(indexName);
        sb.append(" ON ");
        sb.append(tableName);
        jdbcTemplate.execute(sb.toString());
    }

    @Override
    protected String getColumnTypeString(Column column)
    {
        String typeStr = null;
        switch (column.getType())
        {
            case TIMESTAMP:
                typeStr = ColumnType.TIMESTAMP.name();
                break;
            default:
                typeStr = super.getColumnTypeString(column);
        }
        return typeStr;
    }

    public static class Factory extends SchemaDaoFactory
    {

        @Override
        public boolean isSupported(String databaseProduct, String databaseVersion)
        {
            DbC.throwIfNullArg(databaseProduct);
            return databaseProduct.equalsIgnoreCase("MySQL");
        }

        @Override
        public SchemaDao create (JdbcTemplate jdbcTemplate)
        {
            DbC.throwIfNullArg(jdbcTemplate);
            return new SchemaDaoMysql (jdbcTemplate);
        }

    }
}
