package org.scheez.schema.dao.impl;

import javax.sql.DataSource;

import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.dao.SchemaDaoFactory;
import org.scheez.schema.def.ColumnType;
import org.scheez.schema.parts.Column;
import org.scheez.schema.parts.TableName;
import org.scheez.util.DbC;
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
        Integer length = super.getColumnLength(column);;
        if(column.getType() != ColumnType.VARCHAR)
        {
            /// Don't use default length as postgresql supports VARCHAR without requiring a specific length.
            length = column.getLength();
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
    
    public static class Factory extends SchemaDaoFactory
    {

        @Override
        public boolean isSupported(String databaseProduct, String databaseVersion)
        {
            DbC.throwIfNullArg(databaseProduct);
            return databaseProduct.equalsIgnoreCase("PostgreSQL");
        }

        @Override
        public SchemaDao create (JdbcTemplate jdbcTemplate)
        {
            DbC.throwIfNullArg(jdbcTemplate);
            return new SchemaDaoPostgresql (jdbcTemplate);
        }

    }
}
