package org.scheez.test;

import java.io.File;
import java.io.IOException;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.IpPermission;

public class Ec2TestDatabase extends SimpleTestDatabase
{ 
    private static final Log log = LogFactory.getLog(Ec2TestDatabase.class);
    
    public static final String PROPERTY_IMAGE_ID = "imageId";
    
    public static final String PROPERTY_SECURITY_GROUP = "securityGroup";
    
    public static final String PROPERTY_KEY_NAME = "keyName";
    
    public static final String PROPERTY_INSTANCE_TYPE = "instanceType";
    
    public static final String PROPERTY_INSTANCE_ID = "instanceId";
    
    public static final String PROPERTY_KEY_DIR = "keyDir";
    
    public static final String PROPERTY_INSTANCE_FILE = "instanceFile";
    
    public static final String PROPERTY_SSH_USER = "sshUser";
    
    public static final String TMP_DIR = "build/ec2";
    
    public static final String DEFAULT_SECURITY_GROUP = "scheez";
    
    public static final String DEFAULT_KEY_NAME = "scheez";
    
    public static final String DEFAULT_INSTANCE_TYPE = "m1.small";
    
    private String imageId;
    
    private String securityGroup;
    
    private String keyName;
    
    private String instanceType;
    
    private String instanceId;
    
    private String keyDir;
    
    private String instanceFile;
    
    private String sshUser;
    
    private File keyFile;
    
    private Instance instance;
    
    private static Ec2Helper ec2Helper;
    
    public void initialize (String name, TestDatabaseProperties properties) 
    {
        super.initialize (name, properties);
        loadProperties();
        
        ec2Helper = new Ec2Helper();
        
        initSecurityGroup();
        keyFile = initKeyPair();
    }
    
    /**
     * @param session
     */
    private void scheduleShutdown(SshSession session)
    {
        Result result = session.runCommand("sudo shutdown -h +55 2>&1");
        if(!result.isSuccess() && (!result.getText().contains("Another shutdown is already running")))
        {
            log.error("Scheduled shutdown failed for " + instance + ".  " + result);
        }
    }

    /**
     * @return
     */
    private Instance startNewInstance()
    {
        log.info("Starting " + name + " [imageId=\"" + imageId + "\", instanceType=\"" + instanceType + "\"]...");
        Instance instance = ec2Helper.startInstance(instanceType, imageId, securityGroup, keyName, true);
        
        instanceId = instance.getInstanceId();
        properties.setProperty(PROPERTY_INSTANCE_ID, instanceId);
        properties.save (new File(instanceFile));
        
        return instance;
    }

    /**
     * 
     */
    private File initKeyPair()
    {
        try
        {
            
            return ec2Helper.initializeKeyPair(keyName, new File(keyDir));
        }
        catch (IOException e)
        {
            throw new RuntimeException ("Unable to initialize key pair.");
        }     
    }

    /**
     * 
     */
    private void initSecurityGroup()
    {
        ec2Helper.createSecurityGroup(securityGroup, "A security group used for integration testing with different database vendor instances."); 
        
        ec2Helper.addIPPermission(securityGroup, new IpPermission().withIpProtocol("tcp").withIpRanges("0.0.0.0/0").withFromPort(1024).withToPort(10000));
        ec2Helper.addIPPermission(securityGroup, new IpPermission().withIpProtocol("tcp").withIpRanges("0.0.0.0/0").withFromPort(22).withToPort(22));
        ec2Helper.addIPPermission(securityGroup, new IpPermission().withIpProtocol("icmp").withIpRanges("0.0.0.0/0").withFromPort(-1).withToPort(-1));
    }

    /**
     * 
     */
    private void loadProperties()
    {
        instanceFile = properties.getProperty(PROPERTY_INSTANCE_FILE, new File(TMP_DIR, name + ".properties").getAbsolutePath());
        File f = new File(instanceFile);
        
        if(f.exists())
        {
            properties = TestDatabaseProperties.load("file:" + instanceFile, properties);
        }
        else
        {
            properties = new TestDatabaseProperties(properties);
        }
        
        imageId = properties.getProperty(PROPERTY_IMAGE_ID, true, true);
        instanceType = properties.getProperty(PROPERTY_INSTANCE_TYPE, DEFAULT_INSTANCE_TYPE);
        instanceId = properties.getProperty(PROPERTY_INSTANCE_ID, false, true);
        securityGroup = properties.getProperty(PROPERTY_SECURITY_GROUP, DEFAULT_SECURITY_GROUP);
        keyName = properties.getProperty(PROPERTY_KEY_NAME, DEFAULT_KEY_NAME);
        keyDir = properties.getProperty(PROPERTY_KEY_DIR, new File(TMP_DIR).getAbsolutePath());
        sshUser = properties.getProperty(PROPERTY_SSH_USER, true, true);
    }

    @Override
    public DataSource getDataSource()
    {
        checkDatabase ();     
        return super.getDataSource();
    }

    /**
     * 
     */
    private synchronized void checkDatabase ()
    {
        boolean setupRequired = (instance == null);
        if(instanceId != null)
        {
            Instance i = ec2Helper.getInstance(instanceId);
            if((i != null) && (i.getState().getName().equals(Ec2Helper.STATE_RUNNING)))
            {
                instance = i;
                log.info("Using existing " + name + " instance: " + instance);
            }
            else
            {
                instance = null;
            }
        }
       
        if(instance == null)
        {
            instance = startNewInstance ();
            setupRequired = true;
        }
        
        if(setupRequired)
        {
            SshSession session = new SshSession(instance.getPublicDnsName(), sshUser, keyFile);
            scheduleShutdown (session);
            
            url = url.replaceAll("//.*/", "//" + instance.getPublicDnsName() + "/");
            log.info("Updating DataSource URL: " + url);
        }
    }

    public static void main(String[] args)
    {
        TestDatabaseProperties properties = new TestDatabaseProperties ();
        properties.setProperty(PROPERTY_URL, "jdbc:postgresql://localhost/scheez");
        properties.setProperty(PROPERTY_USERNAME, "postgres");
        properties.setProperty(PROPERTY_PASSWORD, "bitnami");
        properties.setProperty(PROPERTY_IMAGE_ID, "ami-31319958");
        properties.setProperty(PROPERTY_SSH_USER, "bitnami");
        
        Ec2TestDatabase database = new Ec2TestDatabase ();
        database.initialize("postgresql", properties);
       
        JdbcTemplate jdbcTemplate = new JdbcTemplate (database.getDataSource());
        System.out.println(jdbcTemplate.queryForObject("SELECT not()", String.class));
    }
}
