package org.scheez.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    
    public static final String PROPERTY_COMMAND_COUNT = "commandCount";
    
    public static final String PROPERTY_COMMAND = "command";
    
    public static final String TMP_DIR = "build/ec2";
    
    public static final String DEFAULT_SECURITY_GROUP = "scheez";
    
    public static final String DEFAULT_KEY_NAME = "scheez";
    
    public static final String DEFAULT_INSTANCE_TYPE = "m1.small";
    
    protected String imageId;
    
    protected String securityGroup;
    
    protected String keyName;
    
    protected String instanceType;
    
    protected String instanceId;
    
    protected String keyDir;
    
    protected String instanceFile;
    
    protected String sshUser;
    
    protected List<String> commands;
    
    protected File keyFile;
    
    protected Instance instance;
    
    protected static Ec2Helper ec2Helper;
    
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
    protected void scheduleShutdown(SshSession session)
    {
        Result result = session.runCommand("sudo shutdown -h +55 2>&1 &");
        if(!result.isSuccess() && (!result.getText().contains("Another shutdown is already running")))
        {
            log.error("Scheduled shutdown failed for " + instance + ".  " + result);
        }
    }

    /**
     * @return
     */
    protected Instance startNewInstance()
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
    protected File initKeyPair()
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
    protected void initSecurityGroup()
    {
        ec2Helper.createSecurityGroup(securityGroup, "A security group used for integration testing with different database vendor instances."); 
        
        ec2Helper.addIPPermission(securityGroup, new IpPermission().withIpProtocol("tcp").withIpRanges("0.0.0.0/0").withFromPort(1024).withToPort(10000));
        ec2Helper.addIPPermission(securityGroup, new IpPermission().withIpProtocol("tcp").withIpRanges("0.0.0.0/0").withFromPort(22).withToPort(22));
        ec2Helper.addIPPermission(securityGroup, new IpPermission().withIpProtocol("icmp").withIpRanges("0.0.0.0/0").withFromPort(-1).withToPort(-1));
    }

    /**
     * 
     */
    protected void loadProperties()
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
        
        int commandCount = properties.getInteger(PROPERTY_COMMAND_COUNT, 0);
        commands = new ArrayList<String> (commandCount);
        for (int index = 1; index <= commandCount; index++)
        {
            commands.add(index - 1, properties.getProperty(PROPERTY_COMMAND + "." + index, true, true));
        }
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
    protected synchronized void checkDatabase ()
    {
        boolean setupRequired = (instance == null);
        if (instanceId == null)
        {
            log.info("No previous instance for " + name + " found.");
        }
        else 
        {
            log.info("Found previous instanceId for " + name + ":  " + instanceId);
            Instance i = ec2Helper.getInstance(instanceId);
            if (i == null)
            {
                log.info("No instance with instanceId=\"" + instanceId + "\" found.");
            }
            else if (i.getState().getName().equals(Ec2Helper.STATE_RUNNING))
            {
                instance = i;
                log.info("Using existing " + name + " instance: " + instance);
            }
            else
            {
                log.info("Found instance with instanceId=\"" + instanceId + "\", but this instance is not running: " + i);
            }
        }
       
        if(instance == null)
        {
            instance = startNewInstance ();
            setupRequired = true;
        }
        
        SshSession session = getSshSession (120000, 10000);
        
        if(setupRequired)
        {
            scheduleShutdown (session);
            
            for (String command : commands)
            {
                Result result = session.runCommand(command);
                if(!result.isSuccess())
                {
                    log.error("Command failed: " + command + "\nInstance: " + instance + "\nResult: " + result);
                }
                else
                {
                    log.info("Command successful: " + command + "\nInstance: " + instance);
                }
            }
            
            url = url.replaceAll("//.*/", "//" + instance.getPublicDnsName() + "/");
            log.info("Updating DataSource URL: " + url);
        }
    }

    private SshSession getSshSession(int timeout, int increments)
    {
        SshSession session = new SshSession(instance.getPublicDnsName(), sshUser, keyFile);
        
        int time = 0;
        while(!session.init().isSuccess())
        {
            session.close();
            session = null;
            
            log.info("Waiting for SSH connection to succeed.");
            try
            {
                Thread.sleep(increments);
            }
            catch (InterruptedException e)
            {
               log.warn(e);
            }
            time += increments;
            if(time >= timeout)
            {
                log.error("Timed out waiting for SSH connection to succeed to: " + instance);
                break;
            }
            else
            {
                session = new SshSession(instance.getPublicDnsName(), sshUser, keyFile);
            }
        }
        log.info("Created SSH connection to instance: " + instance);
        
        return session;
    }

    public static void main(String[] args)
    {
        TestDatabaseProperties properties = new TestDatabaseProperties ().withPrefix("postgresql-ec2");
        properties.setProperty(PROPERTY_URL, "jdbc:postgresql://localhost/scheez");
        properties.setProperty(PROPERTY_USERNAME, "postgres");
        properties.setProperty(PROPERTY_PASSWORD, "bitnami");
        properties.setProperty(PROPERTY_IMAGE_ID, "ami-31319958");
        properties.setProperty(PROPERTY_SSH_USER, "bitnami");
        properties.setProperty(PROPERTY_COMMAND_COUNT, Integer.toString(3));
        properties.setProperty(PROPERTY_COMMAND + ".1", "sudo /opt/bitnami/postgresql/scripts/ctl.sh stop 2>&1");
        properties.setProperty(PROPERTY_COMMAND + ".2", "sudo /opt/bitnami/postgresql/scripts/ctl.sh start 2>&1");
        properties.setProperty(PROPERTY_COMMAND + ".3", "sudo -u postgres createdb scheez");
        
        Ec2TestDatabase database = new Ec2TestDatabase ();
        database.initialize("postgresql-ec2", properties);
       
        JdbcTemplate jdbcTemplate = new JdbcTemplate (database.getDataSource());
        System.out.println(jdbcTemplate.queryForObject("SELECT now()", String.class));
    }
}
