package org.scheez.schema.dao;

import javax.sql.DataSource;

import org.scheez.schema.def.ColumnType;
import org.scheez.schema.model.Column;
import org.scheez.schema.model.ObjectName;
import org.scheez.schema.model.TableName;
import org.scheez.util.DbC;

public class SchemaDaoPostgresql extends SchemaDaoAnsi
{
    public SchemaDaoPostgresql(DataSource dataSource)
    {
        super(dataSource);
    }
   
    @Override
    public void renameTable(TableName oldName, TableName newName)
    {
        if (!oldName.getSchemaName().equalsIgnoreCase(newName.getSchemaName()))
        {
            StringBuilder sb = new StringBuilder("ALTER TABLE ");
            sb.append(oldName);
            sb.append(" SET SCHEMA ");
            sb.append(newName.getSchemaName());
            execute(sb.toString());
            oldName = new TableName(newName.getSchemaName(), oldName.getTableName());
        }
        if (!oldName.getTableName().equalsIgnoreCase(newName.getTableName()))
        {
            StringBuilder sb = new StringBuilder("ALTER TABLE ");
            sb.append(oldName);
            sb.append(" RENAME TO ");
            sb.append(newName.getTableName());
            execute(sb.toString());
        }
    }

    @Override
    public void renameColumn(TableName tableName, String oldName, String newName)
    {
        StringBuilder sb = new StringBuilder("ALTER TABLE ");
        sb.append(tableName);
        sb.append(" RENAME COLUMN ");
        sb.append(oldName);
        sb.append(" TO ");
        sb.append(newName);
        execute(sb.toString());
    }
    
    @Override
    public boolean schemaExists(final String schemaName)
    {
        return super.schemaExists(schemaName);
    }

    @Override
    protected String getAutoIncrement()
    {
        return "";
    }

    protected Integer getColumnLength(Column column)
    {
        Integer length = super.getColumnLength(column);
        ;
        if (column.getType() != ColumnType.VARCHAR)
        {
            // / Don't use default length as postgresql supports VARCHAR without
            // requiring a specific length.
            length = column.getLength();
        }
        return length;
    }
    
    @Override
    protected String getSchemaName(ObjectName objectName)
    {
        return objectName.toLowerCase().getSchemaName();
    }

    @Override
    protected String getObjectName (ObjectName objectName)
    {
        return objectName.toLowerCase().getObjectName();
    }

    @Override
    protected String getColumnName(String columnName)
    {
        return columnName.toLowerCase();
    }

    @Override
    public void alterColumn(TableName tableName, Column column)
    {
        StringBuilder sb = new StringBuilder("ALTER TABLE ");
        sb.append(tableName);
        sb.append(" ALTER COLUMN ");
        sb.append(getColumnName(column.getName()));
        sb.append(" TYPE ");
        sb.append(getColumnTypeString(column));
        execute(sb.toString());
    }

    @Override
    public ColumnType getExpectedColumnType(ColumnType columnType)
    {
        if (columnType == ColumnType.TINYINT)
        {
            columnType = ColumnType.SMALLINT;
        }
        else if (columnType == ColumnType.FLOAT)
        {
            columnType = ColumnType.DOUBLE;
        }
        return columnType;
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
        public SchemaDao create(DataSource dataSource)
        {
            DbC.throwIfNullArg(dataSource);
            return new SchemaDaoPostgresql(dataSource);
        }

    }
}
