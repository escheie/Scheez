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
import org.scheez.schema.model.ObjectName;
import org.scheez.schema.model.Sequence;
import org.scheez.schema.model.SequenceName;
import org.scheez.schema.model.Table;
import org.scheez.schema.model.TableName;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class SchemaDaoAnsi implements SchemaDao, SchemaDdlExecutor
{
    private final Log log = LogFactory.getLog(getClass());

    private static final int DEFAULT_LENGTH = 255;

    private static final int DEFAULT_PRECISION = 18;

    private static final int DEFAULT_SCALE = 0;

    private JdbcTemplate jdbcTemplate;

    private SchemaDdlExecutor schemaDdlExecutor;

    public SchemaDaoAnsi(DataSource dataSource)
    {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public DataSource getDataSource()
    {
        return jdbcTemplate.getDataSource();
    }

    protected String getCatalogName(ObjectName objectName)
    {
        return null;
    }

    protected String getSchemaName(ObjectName objectName)
    {
        return objectName.getSchemaName();
    }

    protected String getObjectName(ObjectName objectName)
    {
        return objectName.getObjectName();
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

    @Override
    public void setSchemaDdlExecutor(SchemaDdlExecutor executor)
    {
        schemaDdlExecutor = executor;
    }

    @Override
    public SchemaDdlExecutor getSchemaDdlExecutor()
    {
        return schemaDdlExecutor;
    }

    public void execute(String sql)
    {
        if (schemaDdlExecutor != null)
        {
            schemaDdlExecutor.execute(sql);
        }
        else
        {
            log.info(sql);
            jdbcTemplate.execute(sql);
        }
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
                ResultSet rs = metaData.getSchemas(getCatalogName(tableName), getSchemaName(tableName));
                boolean exists = false;
                while(rs.next())
                {
                    log(rs);
                    exists = true;
                }
                return exists;
            }
        });
    }

    @Override
    public List<String> getSchemas()
    {
        return jdbcTemplate.execute(new ConnectionCallback<List<String>>()
        {
            @Override
            public List<String> doInConnection(Connection con) throws SQLException, DataAccessException
            {
                DatabaseMetaData metaData = con.getMetaData();
                ResultSet resultSet = metaData.getSchemas();
                LinkedList<String> schemaNames = new LinkedList<String>();
                while (resultSet.next())
                {
                    log(resultSet);
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
    public void renameTable(TableName oldName, TableName newName)
    {
        StringBuilder sb = new StringBuilder("ALTER TABLE ");
        sb.append(oldName);
        sb.append(" RENAME TO ");
        sb.append(newName);
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
        if (table.getPrimaryKey() != null)
        {
            sb.append(getPrimaryKey(table.getPrimaryKey()));
        }
        sb.append(")");
        execute(sb.toString());
    }

    private String getPrimaryKey(Key primaryKey)
    {
        boolean first = true;
        StringBuffer sb = new StringBuffer(", PRIMARY KEY (");
        for (String column : primaryKey.getColumnNames())
        {
            if (first)
            {
                first = false;
            }
            else
            {
                sb.append(", ");
            }
            sb.append(column);
        }
        sb.append(")");
        return sb.toString();
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
                ResultSet tableResultSet = metaData.getTables(getCatalogName(tableName), getSchemaName(tableName),
                        getObjectName(tableName), null);
                while (tableResultSet.next())
                {
                    log(tableResultSet);
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
            public List<Table> doInConnection(Connection con) throws SQLException, DataAccessException
            {
                TableName tableName = new TableName(schemaName, null);
                DatabaseMetaData metaData = con.getMetaData();
                List<Table> tables = new LinkedList<Table>();
                ResultSet rs = metaData.getTables(getCatalogName(tableName), getSchemaName(tableName),
                        getObjectName(tableName), new String[] { "TABLE" });
                while (rs.next())
                {
                    log(rs);
                    Table table = new Table(new TableName(schemaName, rs.getString(TableMetaDataKey.TABLE_NAME
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
                getSchemaName(table.getTableName()), getObjectName(table.getTableName()), null);
        while (columns.next())
        {
            log(columns);
            table.addColumn(getColumn(columns));
        }
        ResultSet indexes = metaData.getIndexInfo(getCatalogName(table.getTableName()),
                getSchemaName(table.getTableName()), getObjectName(table.getTableName()), false, false);
        Index lastIndex = null;
        while (indexes.next())
        {
            log(indexes);
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
                getSchemaName(table.getTableName()), getObjectName(table.getTableName()));
        Key primaryKey = null;
        while (pk.next())
        {
            log(pk);
            if (primaryKey == null)
            {
                primaryKey = new Key(table.getTableName(), pk.getString(ReferenceMetaDataKey.PK_NAME.toString()));
            }
            primaryKey.addColumnName(pk.getInt(ReferenceMetaDataKey.KEY_SEQ.toString()),
                    pk.getString(ColumnMetaDataKey.COLUMN_NAME.toString()));
        }
        table.setPrimaryKey(primaryKey);
        ResultSet fks = metaData.getImportedKeys(getCatalogName(table.getTableName()),
                getSchemaName(table.getTableName()), getObjectName(table.getTableName()));
        int lastKeySeq = 1;
        ForeignKey foreignKey = null;
        while (fks.next())
        {
            log(fks);
            int keySeq = fks.getInt(ReferenceMetaDataKey.KEY_SEQ.toString());
            if (lastKeySeq >= keySeq)
            {
                lastKeySeq = keySeq;
                TableName pkTableName = new TableName(fks.getString(ReferenceMetaDataKey.PKTABLE_SCHEM.toString()),
                        fks.getString(ReferenceMetaDataKey.PKTABLE_NAME.toString()));
                foreignKey = new ForeignKey(table.getTableName(),
                        fks.getString(ReferenceMetaDataKey.FK_NAME.toString()));
                foreignKey.setReferencedPrimaryKey(new Key(pkTableName, fks.getString(ReferenceMetaDataKey.PK_NAME
                        .toString())));
                table.addForeignKey(foreignKey);
            }
            foreignKey.addColumnName(keySeq, fks.getString(ReferenceMetaDataKey.FKCOLUMN_NAME.toString()));
            foreignKey.getReferencedPrimaryKey().addColumnName(keySeq,
                    fks.getString(ReferenceMetaDataKey.PKCOLUMN_NAME.toString()));
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
    public void renameColumn(TableName tableName, String oldName, String newName)
    {
        StringBuilder sb = new StringBuilder("ALTER TABLE ");
        sb.append(tableName);
        sb.append(" ALTER COLUMN ");
        sb.append(oldName);
        sb.append(" RENAME TO ");
        sb.append(newName);
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
                ResultSet columns = metaData.getColumns(getCatalogName(tableName), getSchemaName(tableName),
                        getObjectName(tableName), getColumnName(columnName));
                Column column = null;
                if (columns.next())
                {
                    log(columns);
                    column = getColumn(columns);
                }
                return column;
            }
        });
    }

    @Override
    public void alterColumn(TableName tableName, Column column)
    {
        StringBuilder sb = new StringBuilder("ALTER TABLE ");
        sb.append(tableName);
        sb.append(" ALTER COLUMN ");
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
                ResultSet indexes = metaData.getIndexInfo(getCatalogName(tableName), getSchemaName(tableName),
                        getObjectName(tableName), false, true);
                Index index = null, lastIndex = null;
                while (indexes.next())
                {
                    log(indexes);
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

    @Override
    public Sequence getSequence(final SequenceName sequenceName)
    {
        return jdbcTemplate.execute(new ConnectionCallback<Sequence>()
        {
            @Override
            public Sequence doInConnection(Connection con) throws SQLException, DataAccessException
            {
                DatabaseMetaData metaData = con.getMetaData();
                ResultSet rs = metaData.getTables(getCatalogName(sequenceName), getSchemaName(sequenceName),
                        getObjectName(sequenceName), new String[] { "SEQUENCES" });
                Sequence sequence = null;
                while (rs.next())
                {
                    log(rs);
                    sequence = new Sequence (sequenceName);
                }
                return sequence;
           }
        });
    }

    @Override
    public void createSequence(Sequence sequence)
    {
        StringBuilder sb = new StringBuilder("CREATE SEQUENCE ");
        sb.append(sequence.getName());
        execute(sb.toString());
    }

    @Override
    public void dropSequence(SequenceName sequenceName)
    {
        StringBuilder sb = new StringBuilder("DROP SEQUENCE ");
        sb.append(sequenceName);
        execute(sb.toString());
    }

    protected List<String> getCatalogs()
    {
        return jdbcTemplate.execute(new ConnectionCallback<List<String>>()
        {
            @Override
            public List<String> doInConnection(Connection con) throws SQLException, DataAccessException
            {
                DatabaseMetaData metaData = con.getMetaData();
                ResultSet resultSet = metaData.getCatalogs();
                LinkedList<String> schemaNames = new LinkedList<String>();
                while (resultSet.next())
                {
                    log(resultSet);
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
                    log(resultSet);
                    if (catalogName.equalsIgnoreCase(resultSet.getString(TableMetaDataKey.TABLE_CAT.name())))
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

    private Boolean getBoolean(ResultSet resultSet, String columnName) throws SQLException
    {
        Boolean retval = null;
        String value = resultSet.getString(columnName);
        if (value != null)
        {
            if (value.equalsIgnoreCase("YES"))
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
        if (column.isAutoIncrementing())
        {
            sb.append(getAutoIncrement());
        }
        // if(!column.isNullable())
        // {
        // sb.append(getNotNullable());
        // }
        // else
        // {
        // sb.append(getNullable());
        // }
        return sb.toString();
    }

    protected abstract String getAutoIncrement();

    protected String getNotNullable()
    {
        return " DEFAULT NOT NULL";
    }

    protected String getNullable()
    {
        return " NULL";
    }

    protected String getDefault(Column column)
    {
        String defaultValue = column.getDefaultValue();
        if (defaultValue == null)
        {
            switch (column.getType())
            {
                case TIMESTAMP:
                    defaultValue = "CURRENT_TIMESTAMP";
                    break;
                case VARCHAR:
                    defaultValue = "''";
                    break;
                default:
                    defaultValue = "0";
            }
        }
        return defaultValue;
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
    
    private void log (ResultSet rs) throws SQLException
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
    }
}
