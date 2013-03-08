package org.scheez.schema.dao;

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
import org.scheez.schema.def.ColumnMetaDataKey;
import org.scheez.schema.def.ColumnType;
import org.scheez.schema.def.IndexMetaDataKey;
import org.scheez.schema.def.ReferenceMetaDataKey;
import org.scheez.schema.def.TableMetaDataKey;
import org.scheez.schema.model.Column;
import org.scheez.schema.model.ForeignKey;
import org.scheez.schema.model.Index;
import org.scheez.schema.model.Key;
import org.scheez.schema.model.Table;
import org.scheez.schema.model.TableName;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

public class SchemaDaoAnsi implements SchemaDao
{  
    private static final Log log = LogFactory.getLog(SchemaDaoAnsi.class);

    private static final int DEFAULT_LENGTH = 255;

    private static final int DEFAULT_PRECISION = 18;

    private static final int DEFAULT_SCALE = 0;

    private JdbcTemplate jdbcTemplate;

    public SchemaDaoAnsi(DataSource dataSource)
    {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public DataSource getDataSource()
    {
        return jdbcTemplate.getDataSource();
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

    protected String getColumnName(String columnName)
    {
        return columnName;
    }

    protected String getIndexName(TableName tableName, Index index)
    {
        return index.getName();
    }

    protected Integer getColumnLength(Column column)
    {
        Integer length = column.getLength();
        if (length == null)
        {
            length = DEFAULT_LENGTH;
        }
        return length;
    }

    protected Integer getColumnPrecision(Column column)
    {
        Integer precision = column.getPrecision();
        if (precision == null)
        {
            precision = DEFAULT_PRECISION;
        }
        return precision;
    }

    protected Integer getColumnScale(Column column)
    {
        Integer scale = column.getScale();
        if (scale == null)
        {
            scale = DEFAULT_SCALE;
        }
        return scale;
    }

    protected void execute(String sql)
    {
        jdbcTemplate.execute(sql);
    }

    @Override
    public void createSchema(String schemaName)
    {
        StringBuilder sb = new StringBuilder("CREATE SCHEMA ");
        sb.append(schemaName);
        execute(sb.toString());
    }

    @Override
    public void dropSchema(String schemaName)
    {
        StringBuilder sb = new StringBuilder("DROP SCHEMA ");
        sb.append(schemaName);
        sb.append(" CASCADE");
        execute(sb.toString());
    }

    @Override
    public boolean schemaExists(final String schemaName)
    {
        return jdbcTemplate.execute(new ConnectionCallback<Boolean>()
        {
            @Override
            public Boolean doInConnection(Connection con) throws SQLException, DataAccessException
            {
                TableName tableName = new TableName(schemaName, null);
                DatabaseMetaData metaData = con.getMetaData();
                ResultSet resultSet = metaData.getSchemas(getCatalogName(tableName),
                        getSchemaName(tableName));
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
            public List<String> doInConnection(Connection con) throws SQLException,
                    DataAccessException
            {
                DatabaseMetaData metaData = con.getMetaData();
                ResultSet resultSet = metaData.getSchemas();
                LinkedList<String> schemaNames = new LinkedList<String>();
                while (resultSet.next())
                {
                    schemaNames.add(resultSet.getString(TableMetaDataKey.TABLE_SCHEM.toString()));
                }
                return schemaNames;
            }
        });
    }

    @Override
    public void dropTable(TableName tableName)
    {
        StringBuilder sb = new StringBuilder("DROP TABLE ");
        sb.append(tableName);
        execute(sb.toString());
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
        execute(sb.toString());
    }

    @Override
    public Table getTable(final TableName tableName)
    {
        return jdbcTemplate.execute(new ConnectionCallback<Table>()
        {
            @Override
            public Table doInConnection(Connection con) throws SQLException, DataAccessException
            {
                DatabaseMetaData metaData = con.getMetaData();
                Table table = null;
                ResultSet tableResultSet = metaData.getTables(getCatalogName(tableName),
                        getSchemaName(tableName),
                        getTableName(tableName), null);
                if (tableResultSet.next())
                {
                    table = new Table(tableName);
                }
                if (table != null)
                {
                    getTableDetails(table, metaData);
                }
                return table;
            }
        });
    }

    @Override
    public List<Table> getTables(final String schemaName)
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
                ResultSet rs = metaData.getTables(getCatalogName(tableName),
                        getSchemaName(tableName),
                        getTableName(tableName), new String[] { "TABLE" });
                while (rs.next())
                {
                    if (log.isDebugEnabled())
                    {
                        ResultSetMetaData rsmd = rs.getMetaData();
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
                            sb.append(rs.getObject(index));
                        }
                        log.debug(sb.toString());
                    }
                    Table table = new Table(new TableName(schemaName, rs
                            .getString(TableMetaDataKey.TABLE_NAME
                                    .toString())));
                    getTableDetails(table, metaData);
                    tables.add(table);
                }
                return tables;
            }
        });
    }

    protected void getTableDetails(Table table, DatabaseMetaData metaData) throws SQLException
    {
        ResultSet columns = metaData.getColumns(getCatalogName(table.getTableName()),
                getSchemaName(table.getTableName()), getTableName(table.getTableName()), null);
        while (columns.next())
        {
            table.addColumn(getColumn(columns));
        }
        ResultSet indexes = metaData.getIndexInfo(getCatalogName(table.getTableName()),
                getSchemaName(table.getTableName()), getTableName(table.getTableName()), false,
                false);
        Index lastIndex = null;
        while (indexes.next())
        {
            Index index = getIndex(indexes, lastIndex);
            if (index != null)
            {
                if ((lastIndex == null) || (!index.getName().equalsIgnoreCase(lastIndex.getName())))
                {
                    table.addIndex(index);
                    lastIndex = index;
                }
            }
        }
        ResultSet pk = metaData.getPrimaryKeys(getCatalogName(table.getTableName()),
                getSchemaName(table.getTableName()), getTableName(table.getTableName()));
        Key primaryKey = null;
        while (pk.next())
        {
            if (primaryKey == null)
            {
                primaryKey = new Key(table.getTableName(),
                        pk.getString(ReferenceMetaDataKey.PK_NAME.toString()));
            }
            primaryKey.addColumnName(pk.getInt(ReferenceMetaDataKey.KEY_SEQ.toString()),
                    pk.getString(ColumnMetaDataKey.COLUMN_NAME.toString()));
        }
        table.setPrimaryKey(primaryKey);
        ResultSet fks = metaData.getImportedKeys(getCatalogName(table.getTableName()),
                getSchemaName(table.getTableName()), getTableName(table.getTableName()));
        int lastKeySeq = 1;
        ForeignKey foreignKey = null;
        while (fks.next())
        {
            int keySeq = fks.getInt(ReferenceMetaDataKey.KEY_SEQ.toString());
            if (lastKeySeq >= keySeq)
            {
                lastKeySeq = keySeq;
                TableName pkTableName = new TableName(
                        fks.getString(ReferenceMetaDataKey.PKTABLE_SCHEM.toString()),
                        fks.getString(ReferenceMetaDataKey.PKTABLE_NAME.toString()));
                foreignKey = new ForeignKey(table.getTableName(),
                        fks.getString(ReferenceMetaDataKey.FK_NAME.toString()));
                foreignKey.setReferencedPrimaryKey(new Key(pkTableName, fks
                        .getString(ReferenceMetaDataKey.PK_NAME.toString())));
                table.addForeignKey(foreignKey);
            }
            foreignKey.addColumnName(keySeq, fks.getString(ReferenceMetaDataKey.FKCOLUMN_NAME.toString()));
            foreignKey.getReferencedPrimaryKey().addColumnName(keySeq, fks.getString(ReferenceMetaDataKey.PKCOLUMN_NAME.toString()));
        }
    }

    @Override
    public void addColumn(TableName tableName, Column column)
    {
        StringBuilder sb = new StringBuilder("ALTER TABLE ");
        sb.append(tableName);
        sb.append(" ADD ");
        sb.append(getColumnString(column));
        execute(sb.toString());
    }

    @Override
    public void dropColumn(TableName tableName, String columnName)
    {
        StringBuilder sb = new StringBuilder("ALTER TABLE ");
        sb.append(tableName);
        sb.append(" DROP ");
        sb.append(columnName);
        execute(sb.toString());
    }

    @Override
    public Column getColumn(final TableName tableName, final String columnName)
    {
        return jdbcTemplate.execute(new ConnectionCallback<Column>()
        {
            @Override
            public Column doInConnection(Connection con) throws SQLException, DataAccessException
            {
                DatabaseMetaData metaData = con.getMetaData();
                ResultSet columns = metaData.getColumns(getCatalogName(tableName),
                        getSchemaName(tableName),
                        getTableName(tableName), getColumnName(columnName));
                Column column = null;
                if (columns.next())
                {
                    column = getColumn(columns);
                }
                return column;
            }
        });
    }

    @Override
    public void alterColumnType(TableName tableName, Column column)
    {
        StringBuilder sb = new StringBuilder("ALTER TABLE ");
        sb.append(tableName);
        sb.append(" ALTER ");
        sb.append(getColumnString(column));
        execute(sb.toString());
    }

    @Override
    public ColumnType getExpectedColumnType(ColumnType columnType)
    {
        return columnType;
    }

    @Override
    public void addIndex(TableName tableName, Index index)
    {
        StringBuilder sb = new StringBuilder("CREATE INDEX ");
        sb.append(getIndexName(tableName, index));
        sb.append(" ON ");
        sb.append(tableName);
        sb.append(" (");
        boolean first = true;
        for (String columnName : index.getColumnNames())
        {
            if (first)
            {
                first = false;
            }
            else
            {
                sb.append(", ");
            }
            sb.append(columnName);
        }
        sb.append(")");
        execute(sb.toString());
    }

    @Override
    public void dropIndex(TableName tableName, String indexName)
    {
        StringBuilder sb = new StringBuilder("DROP INDEX ");
        sb.append(tableName.getSchemaName());
        sb.append(".");
        sb.append(indexName);
        execute(sb.toString());
    }

    @Override
    public Index getIndex(final TableName tableName, final String indexName)
    {
        return jdbcTemplate.execute(new ConnectionCallback<Index>()
        {
            @Override
            public Index doInConnection(Connection con) throws SQLException, DataAccessException
            {
                DatabaseMetaData metaData = con.getMetaData();
                ResultSet indexes = metaData.getIndexInfo(getCatalogName(tableName),
                        getSchemaName(tableName), getTableName(tableName), false, true);
                Index index = null, lastIndex = null;
                while (indexes.next())
                {
                    lastIndex = getIndex(indexes, lastIndex);
                    if ((lastIndex != null) && (lastIndex.getName().equalsIgnoreCase(indexName)))
                    {
                        index = lastIndex;
                    }
                }
                return index;
            }
        });
    }

    protected List<String> getCatalogs()
    {
        return jdbcTemplate.execute(new ConnectionCallback<List<String>>()
        {
            @Override
            public List<String> doInConnection(Connection con) throws SQLException,
                    DataAccessException
            {
                DatabaseMetaData metaData = con.getMetaData();
                ResultSet resultSet = metaData.getCatalogs();
                LinkedList<String> schemaNames = new LinkedList<String>();
                while (resultSet.next())
                {
                    schemaNames.add(resultSet.getString(TableMetaDataKey.TABLE_CAT.toString()));
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
            public Boolean doInConnection(Connection con) throws SQLException, DataAccessException
            {
                boolean exists = false;
                DatabaseMetaData metaData = con.getMetaData();
                ResultSet resultSet = metaData.getCatalogs();
                while (resultSet.next())
                {
                    if (catalogName.equalsIgnoreCase(resultSet.getString(TableMetaDataKey.TABLE_CAT
                            .name())))
                    {
                        exists = true;
                        break;
                    }
                }
                return exists;
            }
        });
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
        Column column = new Column(resultSet.getString(ColumnMetaDataKey.COLUMN_NAME.name()),
                ColumnType.getType(resultSet.getInt(ColumnMetaDataKey.DATA_TYPE.name())));
        if (column.getType().isLengthSupported())
        {
            column.setLength(resultSet.getInt(ColumnMetaDataKey.COLUMN_SIZE.name()));
        }
        else if (column.getType() == ColumnType.DECIMAL)
        {
            column.setPrecision(resultSet.getInt(ColumnMetaDataKey.COLUMN_SIZE.name()));
            column.setScale(resultSet.getInt(ColumnMetaDataKey.DECIMAL_DIGITS.name()));
        }
        column.setNullable(getBoolean(resultSet, ColumnMetaDataKey.IS_NULLABLE.toString()));
        return column;
    }

    private Boolean getBoolean (ResultSet resultSet, String columnName) throws SQLException
    {
        Boolean retval = null;
        String value = resultSet.getString(columnName);
        if(value != null)
        {
            if(value.equalsIgnoreCase("YES"))
            {
                retval = Boolean.TRUE;
            }
            else if (value.equalsIgnoreCase("NO"))
            {
                retval = Boolean.FALSE;
            }
        }
        return retval;
    }

    protected Index getIndex(ResultSet resultSet, Index lastIndex) throws SQLException
    {
        if (log.isDebugEnabled())
        {
            ResultSetMetaData rsmd = resultSet.getMetaData();
            StringBuilder sb = new StringBuilder("Index: ");
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
        String indexName = resultSet.getString(IndexMetaDataKey.INDEX_NAME.name());
        Index index = null;
        if ((lastIndex != null) && (lastIndex.getName().equalsIgnoreCase(indexName)))
        {
            index = lastIndex;
        }
        else if (indexName != null)
        {
            index = new Index(indexName);
            index.setUnique(!resultSet.getBoolean(IndexMetaDataKey.NON_UNIQUE.toString()));
        }
        if (index != null)
        {
            index.addColumnName(resultSet.getString(IndexMetaDataKey.COLUMN_NAME.name()));
        }
        return index;
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
                Integer length = getColumnLength(column);
                if (length != null)
                {
                    typeStr = "VARCHAR(" + length + ")";
                }
                else
                {
                    typeStr = "VARCHAR";
                }
                break;
            case CHAR:
                length = getColumnLength(column);
                if (length != null)
                {
                    typeStr = "CHAR(" + length + ")";
                }
                else
                {
                    typeStr = "CHAR";
                }
                break;
            case TIMESTAMP:
                typeStr = "TIMESTAMP WITH TIME ZONE";
                break;
            case DECIMAL:
                Integer precision = getColumnPrecision(column);
                Integer scale = getColumnScale(column);
                typeStr = "DECIMAL (" + precision + ", " + scale + ")";
                break;
            default:
                typeStr = column.getType().name();
        }
        return typeStr;
    }
}
