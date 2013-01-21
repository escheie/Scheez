package org.scheez.schema.dao.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.scheez.schema.def.ColumnType;
import org.scheez.schema.def.TableMetaDataKey;
import org.scheez.schema.objects.Column;
import org.scheez.schema.objects.Table;
import org.scheez.schema.objects.TableName;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
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
        return jdbcTemplate.execute(new ConnectionCallback<Boolean>()
        {
            @Override
            public Boolean doInConnection(Connection con) throws SQLException,
                    DataAccessException
            {
                boolean exists = false;
                DatabaseMetaData metaData = con.getMetaData();
                ResultSet resultSet = metaData.getCatalogs();
                while(resultSet.next())
                {
                    if(schemaName.equalsIgnoreCase(resultSet.getString(TableMetaDataKey.TABLE_CAT.name())))
                    {
                        exists = true;
                        break;
                    }
                }
                return exists;
               
            }
        });
    }

    @Override
    protected Table getTable(TableName tableName, DatabaseMetaData metaData)
            throws SQLException
    {   
        Table table = null;
        ResultSet tableResultSet = metaData.getTables(tableName.getSchemaName(),
                null, tableName.getTableName(), null);
        if (tableResultSet.next())
        {
            table = new Table(tableName);
        }
        return table;
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
