package org.scheez.schema.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import org.scheez.schema.def.ColumnType;
import org.scheez.schema.parts.Column;
import org.scheez.schema.parts.TableName;
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
}
