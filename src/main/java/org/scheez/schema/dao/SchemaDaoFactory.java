package org.scheez.schema.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scheez.schema.dao.impl.SchemaDaoHsqldb;
import org.scheez.schema.dao.impl.SchemaDaoMysql;
import org.scheez.schema.dao.impl.SchemaDaoPostgresql;
import org.scheez.util.DbC;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class SchemaDaoFactory
{
    private static final Log log = LogFactory.getLog(SchemaDaoFactory.class);
    
    private static LinkedList<SchemaDaoFactory> schemaDaoFactories = new LinkedList<SchemaDaoFactory>();
    
    static {
        register(new SchemaDaoMysql.Factory());
        register(new SchemaDaoHsqldb.Factory());
        register(new SchemaDaoPostgresql.Factory());
    }

    public static void register(SchemaDaoFactory factory)
    {
        schemaDaoFactories.addFirst(factory);
    }

    public static void unregister(SchemaDaoFactory factory)
    {
        schemaDaoFactories.remove(factory);
    }

    public static SchemaDao getSchemaDao (final DataSource dataSource)
    {
        DbC.throwIfNullArg(dataSource);
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.execute(new ConnectionCallback<SchemaDao>()
        {
            @Override
            public SchemaDao doInConnection(Connection con) throws SQLException, DataAccessException
            {
                String productName = con.getMetaData().getDatabaseProductName();
                String productVersion = con.getMetaData().getDatabaseProductVersion();
                
                log.info("Loading SchemaDao for " + productName + ":" + productVersion + "." );

                SchemaDao schemaDao = null;
                for (SchemaDaoFactory factory : schemaDaoFactories)
                {
                    if(factory.isSupported(productName, productVersion))
                    {
                        schemaDao = factory.create (jdbcTemplate);
                        break;
                    }
                }
                
                if(schemaDao == null)
                {
                    throw new UnsupportedOperationException("No implementation found that supports " + productName + ":" + productVersion + ".");
                }
                
                return schemaDao;
            }
        });
    }
    
    public abstract boolean isSupported (String databaseProduct, String databaseVersion);
    
    public abstract SchemaDao create (JdbcTemplate jdbcTemplate);
}
