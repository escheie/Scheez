package org.scheez.schema.dao.impl;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.sql.DataSource;

import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.dao.SchemaDaoFactory;
import org.scheez.util.DbC;

public class SchemaDaoFactoryUrl implements SchemaDaoFactory
{ 
    private String url;
    
    private DataSource dataSource;
    
    private Map<String, Class<? extends SchemaDao>> schemaDaoClasses;
    
    public SchemaDaoFactoryUrl (String url, DataSource dataSource)
    {
        DbC.throwIfNullArg(url, dataSource);
        this.url = url;
        this.dataSource = dataSource;
    }

    @Override
    public SchemaDao getSchemaDao()
    {
        initMap ();
        
        SchemaDao schemaDao = null;
        for (Entry<String, Class<? extends SchemaDao>> entry : schemaDaoClasses.entrySet())
        {
            if (url.startsWith(entry.getKey()))
            {
                try
                {
                    schemaDao = entry.getValue().getConstructor(DataSource.class).newInstance(dataSource);
                }
                catch (Exception e)
                {
                    throw new RuntimeException ("Unable to create SchamDAO for URL: " + url, e);
                }
                break;
            }    
        }
        
        if(schemaDao == null)
        {
            throw new IllegalArgumentException("No SchemaDAO found for URL: " + url);
        }
        
        return schemaDao;
    }
    
    public Set<String> getSupportedDatabases ()
    {
        initMap();
        
        return Collections.unmodifiableSet(schemaDaoClasses.keySet());
    }
    
    protected Map<String, Class<? extends SchemaDao>> registerSchemaDaoClasses()
    {
        TreeMap<String, Class<? extends SchemaDao>> map = new TreeMap<String, Class<? extends SchemaDao>>();
        map.put("jdbc:mysql", SchemaDaoMysql.class);
        map.put("jdbc:postgresql", SchemaDaoPostgresql.class);
        map.put("jdbc:hsqldb", SchemaDaoHsqldb.class);
        return map;
    }
    
    private synchronized void initMap ()
    {     
        if(schemaDaoClasses == null)
        {
            Map<String, Class<? extends SchemaDao>> map = registerSchemaDaoClasses();
            if((map != null) && (!map.isEmpty()))
            {
                schemaDaoClasses = map;
            }
            else
            {
                throw new IllegalStateException("The registerSchemaDaoClasses() method returned a null or empty list.");
            }
        }
    }
}
