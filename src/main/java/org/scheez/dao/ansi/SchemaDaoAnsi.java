package org.scheez.dao.ansi;

import javax.sql.DataSource;

import org.scheez.dao.AbstractSchemaDao;
import org.scheez.ddl.Column;
import org.scheez.ddl.Table;
import org.springframework.jdbc.core.JdbcTemplate;

public class SchemaDaoAnsi extends AbstractSchemaDao
{
    public SchemaDaoAnsi(DataSource dataSource)
    {
        super(dataSource);
        // TODO Auto-generated constructor stub
    }

    public SchemaDaoAnsi(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
        // TODO Auto-generated constructor stub
    }
    
    

    @Override
    public void createTable(Table table)
    {
        StringBuilder sb = new StringBuilder("CREATE TABLE ");
        if(table.getSchemaName() != null)
        {
            sb.append(table.getSchemaName());
            sb.append(".");
        }
        sb.append(table.getName());
        sb.append(" (");
        for (Column column : table.getColumns())
        {
            sb.append(getColumnDefinition(column));
            sb.append(", ");
        }
        sb.append(")");
        
        jdbcTemplate.execute(sb.toString());
    }

    @Override
    public Table getTable(String schemaName, String tableName)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    public String getColumnDefinition (Column column)
    {
        StringBuilder sb = new StringBuilder(column.getName());
        sb.append(" ");
        sb.append(getColumnType(column));
        return sb.toString();
    }
    
    public String getColumnType (Column column)
    {
        String typeStr = null;
        switch (column.getType())
        {
            case USER_DEFINED:
                throw new UnsupportedOperationException ("User defined types are not supported by this implementation.");
            case VARCHAR:
                if(column.getSize() != null)
                {
                    typeStr = "VARCHAR(" + column.getSize() + ")";
                }
                else
                {
                    typeStr = "VARCHAR";
                }
                break;
            case TIMESTAMP_WITH_TIME_ZONE:
                typeStr = "TIMESTAMP WITH TIME ZONE";
                break;
            default:
                typeStr = column.getType().name();
        }
        return typeStr;   
    }
}
