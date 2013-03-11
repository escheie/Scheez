package org.scheez.schema.dao;

import java.util.List;

import javax.sql.DataSource;

import org.scheez.schema.model.Column;
import org.scheez.schema.model.ObjectName;
import org.scheez.schema.model.Sequence;
import org.scheez.schema.model.TableName;
import org.scheez.util.DbC;
import org.springframework.dao.DataRetrievalFailureException;

public class SchemaDaoMysql extends SchemaDaoAnsi
{
    public SchemaDaoMysql(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public void createSchema(String schemaName)
    {
        StringBuilder sb = new StringBuilder("CREATE DATABASE ");
        sb.append(schemaName);
        execute(sb.toString());
    }

    @Override
    public void dropSchema(String schemaName)
    {
        StringBuilder sb = new StringBuilder("DROP DATABASE ");
        sb.append(schemaName);
        execute(sb.toString());
    }

    @Override
    public boolean schemaExists(final String schemaName)
    {
        return catalogExists(schemaName);
    }

    @Override
    public List<String> getSchemas()
    {
        return getCatalogs();
    }

    @Override
    protected String getSchemaName(ObjectName objectName)
    {
       return null;
    }

    @Override
    protected String getCatalogName(ObjectName objectName)
    {
        return objectName.getSchemaName();
    }

    @Override
    public void alterColumn(TableName tableName, Column column)
    {
        StringBuilder sb = new StringBuilder("ALTER TABLE ");
        sb.append(tableName);
        sb.append(" MODIFY COLUMN ");
        sb.append(getColumnString(column));
        execute(sb.toString());
    }
    
    @Override
    public void renameColumn(TableName tableName, String oldName, String newName)
    {
        Column column = getColumn(tableName, oldName);
        if(column != null)
        {
            column.setName(newName);
            StringBuilder sb = new StringBuilder("ALTER TABLE ");
            sb.append(tableName);
            sb.append(" CHANGE ");
            sb.append(oldName);
            sb.append(" ");
            sb.append(getColumnString(column));
            execute(sb.toString());
        }
        else
        {
            throw new DataRetrievalFailureException("Column \"" + oldName + "\" not found in table " + tableName + ".");
        }
    }

    @Override
    public void dropIndex(TableName tableName, String indexName)
    {
        StringBuilder sb = new StringBuilder("DROP INDEX ");
        sb.append(indexName);
        sb.append(" ON ");
        sb.append(tableName);
        execute(sb.toString());
    }
    
    @Override
    protected String getAutoIncrement()
    {
        return " AUTO_INCREMENT";
    }
    
    

    @Override
    public void createSequence(Sequence sequence)
    {
      
    }

    @Override
    protected String getColumnTypeString(Column column)
    {
        String typeStr = null;
        switch (column.getType())
        {
            case TIMESTAMP:
                if(column.isNullable())
                {
                    typeStr = "TIMESTAMP NULL";
                }
                else
                {
                    typeStr = "TIMESTAMP";
                }
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
            return databaseProduct.equalsIgnoreCase("MySQL");
        }

        @Override
        public SchemaDao create(DataSource dataSource)
        {
            DbC.throwIfNullArg(dataSource);
            return new SchemaDaoMysql(dataSource);
        }

    }
}
