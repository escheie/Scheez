package org.scheez.schema.dao;

import javax.sql.DataSource;

import org.scheez.schema.def.ColumnType;
import org.scheez.schema.model.Column;
import org.scheez.schema.model.Index;
import org.scheez.schema.model.ObjectName;
import org.scheez.schema.model.TableName;
import org.scheez.util.DbC;

public class SchemaDaoOracle extends SchemaDaoAnsi
{
    private String defaultPassword = "scheez";

    public SchemaDaoOracle(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public void createSchema(String schemaName)
    {
        StringBuilder sb = new StringBuilder("CREATE USER ");
        sb.append(schemaName);
        sb.append(" IDENTIFIED BY ");
        sb.append(defaultPassword);
        sb.append(" QUOTA 1G ON USERS ");
        sb.append(" PASSWORD EXPIRE ");
        execute(sb.toString());
    }

    @Override
    public void dropSchema(String schemaName)
    {
        StringBuilder sb = new StringBuilder("DROP USER ");
        sb.append(schemaName);
        sb.append(" CASCADE");
        execute(sb.toString());
    }

    @Override
    public void dropColumn(TableName tableName, String columnName)
    {
        StringBuilder sb = new StringBuilder("ALTER TABLE ");
        sb.append(tableName);
        sb.append(" DROP COLUMN ");
        sb.append(columnName);
        execute(sb.toString());
    }

    @Override
    protected String getSchemaName(ObjectName objectName)
    {
       return objectName.toUpperCase().getSchemaName();
    }

    @Override
    protected String getObjectName(ObjectName objectName)
    {
        return objectName.toUpperCase().getObjectName();
    }
    
    @Override
    protected String getColumnName(String columnName)
    {
        return columnName.toUpperCase();
    }

    @Override
    protected String getIndexName(TableName tableName, Index index)
    {
        return tableName.getSchemaName() + "." + index.getName();
    }
    
    
    
    @Override
    public void renameTable(TableName oldName, TableName newName)
    {
        StringBuilder sb = new StringBuilder("ALTER TABLE ");
        sb.append(oldName);
        sb.append(" RENAME TO ");
        if(oldName.getSchemaName().equalsIgnoreCase(newName.getSchemaName()))
        {
            sb.append(newName.getObjectName());
        }
        else
        {
            sb.append(newName);
        }
        execute(sb.toString());
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
    public void alterColumn(TableName tableName, Column column)
    {
        StringBuilder sb = new StringBuilder("ALTER TABLE ");
        sb.append(tableName);
        sb.append(" MODIFY ");
        sb.append(getColumnString(column));
        execute(sb.toString());
    }

    @Override
    public ColumnType getExpectedColumnType(ColumnType columnType)
    {
        switch (columnType)
        {
            case BOOLEAN:
            case TINYINT:
            case SMALLINT:
            case INTEGER:
            case BIGINT:
                columnType = ColumnType.DECIMAL;
                break;
            case DOUBLE:
                columnType = ColumnType.FLOAT;
                break;
        // case BINARY:
        // typeStr = "BLOB";
        // break;
        }
        return columnType;
    }

    @Override
    protected String getColumnTypeString(Column column)
    {
        String typeStr = null;
        switch (column.getType())
        {
            case BOOLEAN:
                typeStr = "NUMBER(1)";
            case TINYINT:
            case SMALLINT:
            case INTEGER:
            case BIGINT:
                typeStr = "INTEGER";
                break;
            case DOUBLE:
                typeStr = "DOUBLE PRECISION";
                break;
            case BINARY:
                typeStr = "BLOB";
                break;
            default:
                typeStr = super.getColumnTypeString(column);

        }
        return typeStr;
    }

    @Override
    protected String getAutoIncrement()
    {
        return "";
    }

    public String getDefaultPasword()
    {
        return defaultPassword;
    }

    public void setDefaultPasword(String defaultPassword)
    {
        this.defaultPassword = defaultPassword;
    }

    public static class Factory extends SchemaDaoFactory
    {

        @Override
        public boolean isSupported(String databaseProduct, String databaseVersion)
        {
            DbC.throwIfNullArg(databaseProduct);
            return databaseProduct.equalsIgnoreCase("Oracle");
        }

        @Override
        public SchemaDao create(DataSource dataSource)
        {
            DbC.throwIfNullArg(dataSource);
            return new SchemaDaoOracle(dataSource);
        }

    }
}
