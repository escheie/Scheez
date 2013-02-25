package org.scheez.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.scheez.util.DbC;
import org.springframework.core.io.DefaultResourceLoader;

public class TestDatabaseProperties
{
    private Properties properties;
    
    private String keyPrefix;
        
    private TestDatabaseProperties (String keyPrefix, Properties properties)
    {
        this.properties = properties;
        this.keyPrefix = keyPrefix;
    }
    
    public String getKeyPrefix ()
    {
        return keyPrefix;
    }
    
    public TestDatabaseProperties withPrefix (String prefix)
    {
        DbC.throwIfNullArg(prefix);
        return new TestDatabaseProperties(((keyPrefix == null) ? "" : keyPrefix) + prefix, properties);
    }   

    public String getProperty (String key, boolean propertyRequired, boolean valueRequired)
    {
        String k = (keyPrefix == null) ? key : keyPrefix + "." + key;
        String v = properties.getProperty(k);
        if ((propertyRequired) && (v == null))
        {
            throw new IllegalArgumentException("Missing property: " + k);
        }
        if (v != null)
        {
            v = v.trim();
            if ((valueRequired) && (v.isEmpty()))
            {
                throw new IllegalArgumentException("Missing value for property: " + k);
            }
        }
        return v;
    }
    
    public String getProperty (String key, String defaultValue)
    {
        String k = (keyPrefix == null) ? key : keyPrefix + "." + key;
        String v = properties.getProperty(k);
        if (v == null)
        {
            v = defaultValue;
        }
        else 
        {
            v = v.trim();
            if (v.isEmpty())
            {
                v = defaultValue;
            }
        }
        return v;
    }
    
    public static TestDatabaseProperties load  (String resource) 
    {  
        return new TestDatabaseProperties(resource, null);
    }
    
    public static TestDatabaseProperties load (String resource, TestDatabaseProperties defaults)
    {
        Properties properties = null;
        if(defaults != null)
        {
            properties = new Properties(defaults.properties);
        }
        try
        {
            InputStream inputStream = null;
            try
            {
                inputStream =  new DefaultResourceLoader().getResource(resource).getInputStream();
                if(inputStream == null)
                {
                    throw new RuntimeException ("Resource not found: " + resource);
                }
                properties.load(inputStream);
            }
            finally
            {
                if(inputStream != null)
                {
                    inputStream.close();
                }
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException ("Unable to load properties resource: " + resource, e);
        }
        
        return new TestDatabaseProperties((defaults == null) ? null : defaults.getKeyPrefix(), properties);
    }

}
