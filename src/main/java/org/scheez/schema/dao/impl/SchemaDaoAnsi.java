package org.scheez.schema.dao.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.def.ColumnMetaDataKey;
import org.scheez.schema.def.ColumnType;
import org.scheez.schema.def.TableMetaDataKey;
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
        if (supportsCascade)
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
                TableName tableName = new TableName(schemaName, null);
                DatabaseMetaData metaData = con.getMetaData();
                ResultSet resultSet = metaData.getSchemas(
                        getCatalogName(tableName), getSchemaName(tableName));
                return resultSet.next();
            }
        });
    }

    @Override
    public List<String> getSchemas()
    {
        return jdbcTemplate.execute(new ConnectionCallback<List<String>>()
        {
            @Override
            public List<String> doInConnection(Connection con)
                    throws SQLException, DataAccessException
            {
                DatabaseMetaData metaData = con.getMetaData();
                ResultSet resultSet = metaData.getSchemas(null, null);
                LinkedList<String> schemaNames = new LinkedList<String>();
                while (resultSet.next())
                {
                    schemaNames.add(resultSet
                            .getString(TableMetaDataKey.TABLE_SCHEM.toString()));
                }
                return schemaNames;
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
                Table table = null;
                ResultSet tableResultSet = metaData.getTables(
                        getCatalogName(tableName), getSchemaName(tableName),
                        getTableName(tableName), null);
                if (tableResultSet.next())
                {
                    table = new Table(tableName);
                }
                if (table != null)
                {
                    ResultSet columns = metaData.getColumns(
                            getCatalogName(table.getTableName()),
                            getSchemaName(table.getTableName()),
                            getTableName(table.getTableName()), null);
                    while (columns.next())
                    {
                        table.addColumn(getColumn(columns));
                    }
                }
                return table;
            }
        });
    }

    @Override
    public List<Table> getTables (final String schemaName)
    {
        return jdbcTemplate.execute(new ConnectionCallback<List<Table>>()
        {
            @Override
            public List<Table> doInConnection(Connection con) throws SQLException,
                    DataAccessException
            {
                TableName tableName = new TableName(schemaName, null);
                DatabaseMetaData metaData = con.getMetaData();
                List<Table> tables = new LinkedList<Table>();
                ResultSet rs = metaData.getTables(
                        getCatalogName(tableName), getSchemaName(tableName),
                        getTableName(tableName), null);
                while (rs.next())
                {
                    Table table = new Table(new TableName(schemaName, rs.getString(TableMetaDataKey.TABLE_NAME.toString())));
                    ResultSet columns = metaData.getColumns(
                            getCatalogName(table.getTableName()),
                            getSchemaName(table.getTableName()),
                            getTableName(table.getTableName()), null);
                    while (columns.next())
                    {
                        table.addColumn(getColumn(columns));
                    }
                    tables.add(table);
                }
                return tables;
            }
        });
    }

    protected List<String> getCatalogs()
    {
        return jdbcTemplate.execute(new ConnectionCallback<List<String>>()
        {
            @Override
            public List<String> doInConnection(Connection con)
                    throws SQLException, DataAccessException
            {
                DatabaseMetaData metaData = con.getMetaData();
                ResultSet resultSet = metaData.getCatalogs();
                LinkedList<String> schemaNames = new LinkedList<String>();
                while (resultSet.next())
                {
                    schemaNames.add(resultSet
                            .getString(TableMetaDataKey.TABLE_CAT.toString()));
                }
                return schemaNames;
            }
        });
    }

    protected boolean catalogExists(final String catalogName)
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
                while (resultSet.next())
                {
                    if (catalogName.equalsIgnoreCase(resultSet
                            .getString(TableMetaDataKey.TABLE_CAT.name())))
                    {
                        exists = true;
                        break;
                    }
                }
                return exists;
            }
        });
    }

    protected String getCatalogName(TableName tableName)
    {
        return null;
    }

    protected String getSchemaName(TableName tableName)
    {
        return tableName.getSchemaName();
    }

    protected String getTableName(TableName tableName)
    {
        return tableName.getTableName();
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
