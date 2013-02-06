package org.scheez.schema.dao.impl;

import javax.sql.DataSource;

import org.scheez.schema.def.ColumnType;
import org.scheez.schema.parts.Column;
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
    
    protected Integer getColumnLength (Column column)
    {
        return column.getLength();
    }
    
    @Override
    protected String getColumnName (String columnName)
    {
        return columnName.toLowerCase();
    }

    @Override
    protected String getColumnTypeString(Column column)
    {
        String typeStr = null;
        switch (column.getType())
        {
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
