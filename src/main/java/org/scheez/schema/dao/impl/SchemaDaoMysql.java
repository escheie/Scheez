package org.scheez.schema.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import org.scheez.schema.def.ColumnType;
import org.scheez.schema.objects.Column;
import org.scheez.schema.objects.TableName;
import org.springframework.jdbc.core.JdbcTemplate;

public class SchemaDaoMysql extends SchemaDaoAnsi
{
    public SchemaDaoMysql(DataSource dataSource)
    {
        super(dataSource);
        supportsCascade = false;
    }

    public SchemaDaoMysql(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
        supportsCascade = false;
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