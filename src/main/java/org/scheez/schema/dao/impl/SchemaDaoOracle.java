package org.scheez.schema.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.dao.SchemaDaoFactory;
import org.scheez.schema.parts.TableName;
import org.scheez.util.DbC;
import org.springframework.jdbc.core.JdbcTemplate;

public class SchemaDaoOracle extends SchemaDaoAnsi
{
    private String defaultPassword = "scheez";

    public SchemaDaoOracle(DataSource dataSource)
    {
        super(dataSource);
    }

    public SchemaDaoOracle(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
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
        jdbcTemplate.execute(sb.toString());
    }

    @Override
    public void dropSchema(String schemaName)
    {
        StringBuilder sb = new StringBuilder("DROP USER ");
        sb.append(schemaName);
        sb.append(" CASCADE");
        jdbcTemplate.execute(sb.toString());
    }
    
    @Override
    protected String getSchemaName(TableName tableName)
    {
       return tableName.toUpperCase().getSchemaName();
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
        public SchemaDao create(JdbcTemplate jdbcTemplate)
        {
            DbC.throwIfNullArg(jdbcTemplate);
            return new SchemaDaoOracle(jdbcTemplate);
        }

    }
}
