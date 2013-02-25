package org.scheez.test;

import java.io.File;
import java.io.IOException;

import javax.sql.DataSource;

import com.amazonaws.services.ec2.model.IpPermission;

public class Ec2TestDatabase extends SimpleTestDatabase
{ 
    public static final String TMP_DIR = "build/ec2";
    
    public static final String DEFAULT_KEY_DIR = TMP_DIR + "/keys";
    
    public static final String DEFAULT_NAMESPACE = "Scheez";
    
    public static final String DEFAULT_INSTANCE_TYPE = "m1.small";
    
    private String imageId;
    
    private String namespace;
    
    private String instanceType;
    
    private String instanceId;
    
    private static Ec2Helper ec2Helper;
    
    protected Ec2TestDatabase (String name, TestDatabaseProperties properties)
    {
        super(name, properties);
        ec2Helper = new Ec2Helper();
        namespace = DEFAULT_NAMESPACE;
    }
    
    public static TestDatabase getInstance (String name, TestDatabaseProperties properties)
    {
        SimpleTestDatabase testDatabase = new SimpleTestDatabase(name,  properties);
        testDatabase.init();
        return testDatabase;
    }
    
    protected void init () 
    {
        super.init ();
        
        imageId = properties.getProperty("imageId", true, true);
        instanceType = properties.getProperty("instanceType", DEFAULT_INSTANCE_TYPE);
        
        initializeNameSpace(namespace, ec2Helper);
    }
    
    public static synchronized void initializeNameSpace (String namespace, Ec2Helper ec2Helper)
    {
        ec2Helper.createSecurityGroup(namespace, "A security group used for integration testing with different database vendor instances."); 
        
        ec2Helper.addIPPermission(namespace, new IpPermission().withIpProtocol("tcp").withIpRanges("0.0.0.0/0").withFromPort(1024).withToPort(10000));
        ec2Helper.addIPPermission(namespace, new IpPermission().withIpProtocol("tcp").withIpRanges("0.0.0.0/0").withFromPort(22).withToPort(22));
        ec2Helper.addIPPermission(namespace, new IpPermission().withIpProtocol("icmp").withIpRanges("0.0.0.0/0").withFromPort(-1).withToPort(-1));
        
        try
        {
            ec2Helper.initializeKeyPair(namespace, new File(DEFAULT_KEY_DIR));
        }
        catch (IOException e)
        {
            throw new RuntimeException ("Unable to initialize key pair.");
        }     
    }
    
    


    @Override
    public DataSource getDataSource()
    {
        // TODO Auto-generated method stub
        return super.getDataSource();
    }

    @Override
    public void start (boolean wait)
    {
       
    }

    @Override
    public void terminate()
    {
       
    }

    @Override
    public boolean isOnline()
    {
        return false;
    }
}
