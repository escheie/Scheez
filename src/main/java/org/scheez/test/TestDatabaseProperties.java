package org.scheez.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Properties;

import org.scheez.util.DbC;
import org.springframework.core.io.DefaultResourceLoader;

public class TestDatabaseProperties
{
    private Properties properties;
    
    private String keyPrefix;
    
    public TestDatabaseProperties ()
    {
        properties = new Properties();
    }
    
    public TestDatabaseProperties (TestDatabaseProperties defaults)
    {
        keyPrefix = defaults.keyPrefix;
        properties = new Properties(defaults.properties);
    }
        
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
        return new TestDatabaseProperties(((keyPrefix == null) ? "" : keyPrefix + ".") + prefix, properties);
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
        String value = getProperty(key, false, false);
        if ((value == null) || (value.isEmpty()))
        {
            value = defaultValue;
        }
        return value;
    }
    
    public Integer getInteger(String key, boolean propertyRequired)
    {
        String value = getProperty(key, propertyRequired, propertyRequired);
        Integer retval = null;
        if (value != null)
        {
           retval = Integer.parseInt(value);
        }
        return retval;
    }
    
    public Integer getInteger(String key, Integer defaultValue)
    {
        Integer value = getInteger(key, false);
        if (value == null)
        {
           value = defaultValue;
        }
        return value;
    }
    
    public Boolean getBoolean(String key, Boolean defaultValue)
    {
        String value = getProperty(key, false, false);
        Boolean retval = defaultValue;
        if (value != null)
        {
           retval = Boolean.parseBoolean(value);
        }
        return retval;
    }
    
    public void setProperty (String key, String value)
    {
        String k = (keyPrefix == null) ? key : keyPrefix + "." + key;
        properties.setProperty(k, value);
    }
    
    public void save (File file)
    {
        Writer out = null;
        try
        {
            properties.store(out = new BufferedWriter(new FileWriter(file)), null);
        }
        catch (IOException e)
        {
            
        }
        finally
        {
            if(out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static TestDatabaseProperties load  (String resource) 
    {  
        return load (resource, null);
    }
    
    public static TestDatabaseProperties load (String resource, TestDatabaseProperties defaults)
    {
        Properties properties = null;
        if(defaults != null)
        {
            properties = new Properties(defaults.properties);
        }
        else
        {
            properties = new Properties();
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
