package org.scheez.schema.dao.impl;

import javax.sql.DataSource;

import org.scheez.schema.def.ColumnType;
import org.scheez.schema.objects.Column;
import org.springframework.jdbc.core.JdbcTemplate;

public class SchemaDaoPostgresql extends SchemaDaoAnsi 
{
    public SchemaDaoPostgresql(DataSource dataSource)
    {
        super(dataSource);
    }

    public SchemaDaoPostgresql(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
    }

    @Override
    protected String getColumnTypeString(Column column)
    {
        String typeStr = null;
        switch (column.getType())
        {
            case VARCHAR:
                /// No need to specify character limits.
                typeStr = ColumnType.VARCHAR.name();
                break;
            case TINYINT:
                typeStr = ColumnType.SMALLINT.name(); 
                break;
            case DOUBLE:
                typeStr = "DOUBLE PRECISION";
                break;
            case BINARY:
                typeStr = "BYTEA";
                break;
            default:
                typeStr = super.getColumnTypeString(column);
                
        }
        return typeStr;
    }
}
