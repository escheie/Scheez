package org.scheez.schema.dao.impl;

import javax.sql.DataSource;

import org.scheez.schema.objects.TableName;
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
    
    
}
