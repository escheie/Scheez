package org.scheez.schema.dao.impl;

import javax.sql.DataSource;

import org.scheez.schema.def.ColumnType;
import org.scheez.schema.objects.Column;
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
