package org.scheez.schema.dao.impl;

import javax.sql.DataSource;

import org.scheez.schema.def.ColumnType;
import org.scheez.schema.parts.Column;
import org.scheez.schema.parts.TableName;
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
        Integer length = null;
        if(column.getType() != ColumnType.VARCHAR)
        {
            length = super.getColumnLength(column);
        }
        return length;
    }
    
    @Override
    protected String getColumnName (String columnName)
    {
        return columnName.toLowerCase();
    }
    
    @Override
    public void alterColumnType(TableName tableName, Column column)
    {
        StringBuilder sb = new StringBuilder("ALTER TABLE ");
        sb.append(tableName);
        sb.append(" ALTER COLUMN ");
        sb.append(getColumnName(column.getName()));
        sb.append(" TYPE ");
        sb.append( getColumnTypeString(column));
        jdbcTemplate.execute(sb.toString());
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
