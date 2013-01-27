package org.scheez.schema.dao.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.def.ColumnMetaDataKey;
import org.scheez.schema.def.ColumnType;
import org.scheez.schema.objects.Column;
import org.scheez.schema.objects.Table;
import org.scheez.schema.objects.TableName;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

public class SchemaDaoAnsi extends AbstractDao implements SchemaDao
{
    private static final Log log = LogFactory.getLog(SchemaDaoAnsi.class);
    
    protected boolean supportsCascade = true;
    
    protected boolean supportsIfExist = true;

    public SchemaDaoAnsi(DataSource dataSource)
    {
        super(dataSource);
    }

    public SchemaDaoAnsi(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
    }

    @Override
    public void createSchema(String schemaName)
    {
        StringBuilder sb = new StringBuilder("CREATE SCHEMA ");
        sb.append(schemaName);
        jdbcTemplate.execute(sb.toString());
    }

    @Override
    public void dropSchema(String schemaName)
    {
        StringBuilder sb = new StringBuilder("DROP SCHEMA ");
        sb.append(schemaName);
        if(supportsCascade)
        {
            sb.append(" CASCADE");
        }
        jdbcTemplate.execute(sb.toString());
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
                DatabaseMetaData metaData = con.getMetaData();
                ResultSet resultSet = metaData.getSchemas(null, schemaName);
                return resultSet.next();
            }
        });
    }

    @Override
    public void dropTable(TableName tableName)
    {
        StringBuilder sb = new StringBuilder("DROP TABLE IF EXISTS ");
        sb.append(tableName);
        jdbcTemplate.execute(sb.toString());
    }

    @Override
    public void createTable(Table table)
    {
        StringBuilder sb = new StringBuilder("CREATE TABLE ");
        sb.append(table.getTableName());
        sb.append(" (");
        boolean first = true;
        for (Column column : table.getColumns())
        {
            if (first)
            {
                first = false;
            }
            else
            {
                sb.append(", ");
            }
            sb.append(getColumnString(column));
        }
        sb.append(")");
        jdbcTemplate.execute(sb.toString());
    }

    @Override
    public Table getTable(final TableName tableName)
    {
        return jdbcTemplate.execute(new ConnectionCallback<Table>()
        {
            @Override
            public Table doInConnection(Connection con) throws SQLException,
                    DataAccessException
            {
                DatabaseMetaData metaData = con.getMetaData();
                Table table = getTable(tableName, metaData);
                if (table != null)
                {
                    ResultSet columns = metaData.getColumns(null,
                            tableName.getSchemaName(),
                            tableName.getTableName(), null);
                    while (columns.next())
                    {
                        table.addColumn(getColumn(columns));
                    }
                }
                return table;
            }
        });
    }

    protected Table getTable(TableName tableName, DatabaseMetaData metaData)
            throws SQLException
    {
        Table table = null;
        ResultSet tableResultSet = metaData.getTables(null,
                tableName.getSchemaName(), tableName.getTableName(), null);
        if (tableResultSet.next())
        {
            table = new Table(tableName);
        }
        return table;
    }

    protected Column getColumn(ResultSet resultSet) throws SQLException
    {
        if (log.isDebugEnabled())
        {
            ResultSetMetaData rsmd = resultSet.getMetaData();
            StringBuilder sb = new StringBuilder("Column: ");
            boolean first = true;
            for (int index = 1; index <= rsmd.getColumnCount(); index++)
            {
                if (first)
                {
                    first = false;
                }
                else
                {
                    sb.append(",");
                }
                sb.append(rsmd.getColumnLabel(index));
                sb.append("=");
                sb.append(resultSet.getObject(index));
            }
            log.debug(sb.toString());
        }
        return new Column(resultSet.getString(ColumnMetaDataKey.COLUMN_NAME
                .name()), ColumnType.getType(resultSet
                .getInt(ColumnMetaDataKey.DATA_TYPE.name())));
    }

    protected String getColumnString(Column column)
    {
        StringBuilder sb = new StringBuilder(column.getName());
        sb.append(" ");
        sb.append(getColumnTypeString(column));
        return sb.toString();
    }

    protected String getColumnTypeString(Column column)
    {
        String typeStr = null;
        switch (column.getType())
        {
            case VARCHAR:
                if (column.getSize() != null)
                {
                    typeStr = "VARCHAR(" + column.getSize() + ")";
                }
                else
                {
                    typeStr = "VARCHAR";
                }
                break;
            case TIMESTAMP:
                typeStr = "TIMESTAMP WITH TIME ZONE";
                break;
            default:
                typeStr = column.getType().name();
        }
        return typeStr;
    }
}
