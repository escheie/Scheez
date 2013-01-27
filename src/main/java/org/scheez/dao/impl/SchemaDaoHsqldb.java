package org.scheez.dao.impl;

import javax.sql.DataSource;

import org.scheez.schema.objects.Table;
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
    public boolean schemaExists(String schemaName)
    {
        return super.schemaExists(schemaName.toUpperCase());
    }

    @Override
    public Table getTable(TableName tableName)
    {
        return super.getTable(tableName.toUpperCase());
    }
}
