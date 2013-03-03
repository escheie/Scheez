package org.scheez.test.ec2;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.sql.DataSource;

import org.scheez.test.DefaultTestDatabase;
import org.scheez.test.TestConfiguration;
import org.scheez.test.TestDatabase;
import org.scheez.test.TestDatabaseProperties;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.SpotInstanceRequest;

public final class Ec2TestDatabase extends DefaultTestDatabase
{
    public static final String PROPERTY_IMAGE_ID = "imageId";

    public static final String PROPERTY_SECURITY_GROUP = "securityGroup";

    public static final String PROPERTY_KEY_NAME = "keyName";

    public static final String PROPERTY_INSTANCE_TYPE = "instanceType";

    public static final String PROPERTY_INSTANCE_ID = "instanceId";
    
    public static final String PROPERTY_SPOT_INSTANCE_REQUEST_ID = "spotInstanceRequestId";

    public static final String PROPERTY_KEY_DIR = "keyDir";

    public static final String PROPERTY_INSTANCE_FILE = "instanceFile";

    public static final String PROPERTY_SSH_USER = "sshUser";
    
    public static final String PROPERTY_SSH_PORT = "sshPort";
    
    public static final String PROPERTY_LOCAL_PORT = "localPort";
    
    public static final String PROPERTY_DATABASE_PORT = "databasePort";

    public static final String PROPERTY_SCHEDULE_SHUTDOWN = "scheduleShutdown";

    public static final String PROPERTY_SCHEDULE_SHUTDOWN_COMMAND = "scheduleShutdownCommand";

    public static final String PROPERTY_COMMAND = "command";

    public static final String PROPERTY_STAGED = "staged";

    public static final String TMP_DIR = "build/ec2";

    public static final String DEFAULT_SECURITY_GROUP = "scheez";

    public static final String DEFAULT_KEY_NAME = "scheez";

    public static final String DEFAULT_INSTANCE_TYPE = "m1.small";
    
    public static final String DEFAULT_PRICE = "0.06";

    public static final String DEFAULT_SCHEDULE_SHUTDOWN_COMMAND = "echo \"sudo shutdown -h now\" | at now + 55 minutes";
    
    public static final int DEFAULT_SSH_PORT = 22;

    private static final int MIN_PORT_RANGE = 5000;

    private static final int MAX_PORT_RANGE = 20000;
    
    public static final int MAX_RETRY= 12;
    
    public static final int RETRY_WAIT = 10000;

    private static final String LOOPBACK_ADDRESS = "127.0.0.1";

    private String imageId;

    private String securityGroup;

    private String keyName;

    private String instanceType;

    private String instanceId;
    
    private String spotInstanceRequestId;

    private String keyDir;

    private String instanceFile;
    
    private Integer localPort;
    
    private Integer databasePort;

    private String sshUser;
    
    private Integer sshPort;

    private List<String> commands;

    private boolean scheduleShutdown;

    private String scheduleShutdownCommand;

    private boolean staged;

    private File keyFile;

    private SshSession sshSession;

    private Ec2Helper ec2Helper;
    
    private boolean initialized;

    public void initialize(String name, TestDatabaseProperties properties)
    {
        super.initialize(name, properties);
        loadProperties();
        ec2Helper = new Ec2Helper();
        
        initSecurityGroup();
        keyFile = initKeyPair();
    }
    
    /**
     * 
     */
    private void loadProperties()
    {
        instanceFile = properties.getProperty(PROPERTY_INSTANCE_FILE,
                new File(TMP_DIR, name + ".properties").getAbsolutePath());
        File f = new File(instanceFile);

        if (f.exists())
        {
            log.info(name + " - Loading instance properties from " + instanceFile + ".");
            properties = TestDatabaseProperties.load("file:" + instanceFile, properties);
        }
        else
        {
            log.info(name + " - No instance file found: " + instanceFile);
            properties = new TestDatabaseProperties(properties);
        }

        imageId = properties.getProperty(PROPERTY_IMAGE_ID, true, true);
        instanceType = properties.getProperty(PROPERTY_INSTANCE_TYPE, DEFAULT_INSTANCE_TYPE);
        instanceId = properties.getProperty(PROPERTY_INSTANCE_ID, false, true);
        spotInstanceRequestId = properties.getProperty(PROPERTY_SPOT_INSTANCE_REQUEST_ID, false, true);
        securityGroup = properties.getProperty(PROPERTY_SECURITY_GROUP, DEFAULT_SECURITY_GROUP);
        keyName = properties.getProperty(PROPERTY_KEY_NAME, DEFAULT_KEY_NAME);
        keyDir = properties.getProperty(PROPERTY_KEY_DIR, new File(TMP_DIR).getAbsolutePath());
        
        sshUser = properties.getProperty(PROPERTY_SSH_USER, true, true);
        sshPort = properties.getInteger(PROPERTY_SSH_PORT, DEFAULT_SSH_PORT);
        
        localPort = properties.getInteger(PROPERTY_LOCAL_PORT, false);
        databasePort = properties.getInteger(PROPERTY_DATABASE_PORT, true);

        scheduleShutdown = properties.getBoolean(PROPERTY_SCHEDULE_SHUTDOWN, true);
        scheduleShutdownCommand = properties.getProperty(PROPERTY_SCHEDULE_SHUTDOWN_COMMAND,
                DEFAULT_SCHEDULE_SHUTDOWN_COMMAND);

        staged = properties.getBoolean(PROPERTY_STAGED, false);
       

        commands = new ArrayList<String>();
        TestDatabaseProperties commandProperties = properties.withPrefix(PROPERTY_COMMAND);
        int missingCommandCount = 0;
        int commandIndex = 0;
        while (missingCommandCount < 5)
        {
            String command = commandProperties.getProperty(Integer.toString(commandIndex++), false, true);
            if (command == null)
            {
                missingCommandCount++;
            }
            else
            {
                missingCommandCount = 0;
                commands.add(command);
            }
        }
    }
    

    /**
     * 
     */
    private void initSecurityGroup()
    {
        ec2Helper.createSecurityGroup(securityGroup,
                "A security group used for integration testing with different database vendor instances.");

        ec2Helper.addIPPermission(securityGroup, new IpPermission().withIpProtocol("tcp").withIpRanges("0.0.0.0/0")
                .withFromPort(sshPort).withToPort(sshPort));
        ec2Helper.addIPPermission(securityGroup, new IpPermission().withIpProtocol("icmp").withIpRanges("0.0.0.0/0")
                .withFromPort(-1).withToPort(-1));
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
            throw new RuntimeException("Unable to initialize key pair for " + name + ". KeyName: " + keyName
                    + "  KeyDir: " + keyDir, e);
        }
    }
    
    /**
     * @return
     */
    private Instance startNewInstance()
    {
        if(spotInstanceRequestId != null)
        {
            ec2Helper.cancelSpotInstanceRequest(spotInstanceRequestId);
        }
        
        if(sshSession != null)
        {
            sshSession.close();
            sshSession = null;
        }
        
        log.info(name + " - Starting new EC2 instance (imageId=\"" + imageId + "\", instanceType=\"" + instanceType
                + "\")...");
        SpotInstanceRequest request = ec2Helper.startSpotInstance(DEFAULT_PRICE, instanceType, imageId, securityGroup, keyName);
        
        staged = false;
        instanceId = null;
        spotInstanceRequestId = request.getSpotInstanceRequestId();
        
        properties.setProperty(PROPERTY_SPOT_INSTANCE_REQUEST_ID, spotInstanceRequestId);
        properties.remove(PROPERTY_INSTANCE_ID);
        properties.setProperty(PROPERTY_STAGED, Boolean.toString(false));
        properties.save(new File(instanceFile));
       
        instanceId = waitForSpotRequest(request.getSpotInstanceRequestId());
        properties.setProperty(PROPERTY_INSTANCE_ID, instanceId);
        properties.save(new File(instanceFile));
        
        return waitForRunningInstance (instanceId);
    }
    
    /**
     * 
     */
    private String waitForSpotRequest (String requestId)
    {
        long start = System.currentTimeMillis();
        String instanceId = null;
        SpotInstanceRequest spotInstanceRequest = ec2Helper.getSpotInstanceRequest(requestId);
        while (spotInstanceRequest.getState().equals(Ec2Helper.REQUEST_STATE_OPEN))
        {
            log.info(name + " - Waiting for spot request to be fulfilled: " + spotInstanceRequest);
            try
            {
                Thread.sleep(RETRY_WAIT);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            spotInstanceRequest = ec2Helper.getSpotInstanceRequest(requestId);
        }
        if(spotInstanceRequest.getState().equals(Ec2Helper.REQUEST_STATE_ACTIVE))
        {
            log.info(name + " - Spot Request fullfilled in " + (System.currentTimeMillis() - start) / 1000 + " seconds. " + spotInstanceRequest);
            instanceId = spotInstanceRequest.getInstanceId();
        }
        else
        {
            log.error(name + " - Spot Request was not fullfilled. " + spotInstanceRequest);
        }
        return instanceId;     
    }

    /**
     * 
     */
    private Instance waitForRunningInstance (String instanceId)
    {
        long start = System.currentTimeMillis();
        Instance instance = ec2Helper.getInstance(instanceId);
        while ((instance != null) && (instance.getState().getName().equals(Ec2Helper.STATE_PENDING)))
        {
            log.info(name + " - Waiting for instance to transition to running state: " + instance);
            try
            {
                Thread.sleep(RETRY_WAIT);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            instance = ec2Helper.getInstance(instanceId);
        }
        if(instance == null)
        {
            log.info(name + " - Instance is not running with InstanceId: " + instanceId);
        }
        if(instance.getState().getName().equals(Ec2Helper.STATE_RUNNING))
        {
            log.info(name + " - Instance transitioned into running state after " + (System.currentTimeMillis() - start) / 1000 + " seconds.");
        }
        else if (instance.getState().getName().equals(Ec2Helper.STATE_PENDING))
        {
            log.error(name + " - Instance is not running after waiting " + (System.currentTimeMillis() - start) / 1000 + " seconds.");
        }
        else
        {
            log.error(name + " - Instance is not in a pending or running state.  " + instance);
        }
        return instance;        
    }
    
    /**
     * @return
     */
    private Instance findExistingInstance()
    {
        Instance instance = null;
        if (instanceId == null)
        {
            log.info(name + " - No previous EC2 instanceId found.");
            if (spotInstanceRequestId != null)
            {
                log.info(name + " - Checking spot instance request status.");
                instanceId = waitForSpotRequest(spotInstanceRequestId);
                if(instanceId != null)
                {
                    instance = waitForRunningInstance(instanceId);
                }
            }
        }
        else
        {
            log.info(name + " - Checking previous EC2 instanceId: " + instanceId);
            Instance i = ec2Helper.getInstance(instanceId);
            if (i == null)
            {
                log.info(name + " - No instance found with instanceId: " + instanceId);
            }
            else if (i.getState().getName().equals(Ec2Helper.STATE_PENDING))
            {
                log.info(name + " - Using existing instance: " + i);
                instance = waitForRunningInstance(i.getInstanceId());
            }
            else if (i.getState().getName().equals(Ec2Helper.STATE_RUNNING))
            {
                log.info(name + " - Using existing instance: " + i);
                instance = i;
            }
            else
            {
                log.info(name + " - Instance is not running with instanceId: " + i);
            }
        }
        return instance;
    }

    /**
     * @param session
     */
    private void scheduleShutdown (SshSession session)
    {
        if (scheduleShutdown)
        {
            log.info(name + " - Scheduling shutdown: " + scheduleShutdownCommand);
            Result result = session.runCommand(scheduleShutdownCommand);
            if (!result.isSuccess())
            {
                log.error(name + " - Scheduling shutdown failed. Command: " + scheduleShutdownCommand + "\nResult: "
                        + result);
            }
            else
            {
                log.info(name + " - Shutdown scheduled successfully.");
            }
        }
        else
        {
            log.info(name + " - Skipping scheduled shutdown.");
        }
    }
    
    protected synchronized DataSource initializeDataSource (boolean reinitialize)
    {
        if((!initialized) || (reinitialize))
        {
            Instance instance = findExistingInstance();
         
            if (instance == null)
            {
                instance = startNewInstance();
            }
            
            if(sshSession == null)
            {
                sshSession = getSshSession(instance);
                
                if(localPort == null)
                {
                    localPort = getUnusedPort();
                }
                
                log.info(name + " - Creating SSH tunnel from localhost:" + localPort + " to " + instance.getPublicDnsName() + ":" + databasePort);
                Result result = sshSession.createTunnel(localPort, LOOPBACK_ADDRESS, databasePort);
                
                if(!result.isSuccess())
                {
                    log.error(name + " - Unable to create ssh tunnel.  Result: " + result);
                    
                    sshSession.close();
                    sshSession = null;
                    
                    throw new RuntimeException ("Unable to create SSH tunnel from localhost:" + localPort + 
                            " to " + instance.getPublicDnsName() + ":" + databasePort + ".  Result: " + result);
                }
                
                url = url.replaceAll("(.*//)([^/]*)/?(.*)", "$1" + LOOPBACK_ADDRESS + ":" + localPort + "/$3");
                log.info(name + " - Updating DataSource URL: " + url);
            }
            
            if (!staged)
            {
                scheduleShutdown(sshSession);

                for (String command : commands)
                {
                    log.info(name + " - Running Command: " + command);
                    Result result = sshSession.runCommand(command);
                    if (!result.isSuccess())
                    {
                        log.error(name + " - Command failed: " + command + "\nResult: " + result);
                        throw new RuntimeException ("Command failed: " + command + "\nResult: " + result);
                    }
                }

                staged = true;
                properties.setProperty(PROPERTY_STAGED, Boolean.toString(true));
                properties.save(new File(instanceFile));
            }
            
            initialized = true;
        }
        return super.initializeDataSource(reinitialize);
    }

    @Override
    public void close() 
    {
        super.close();
        if(sshSession != null)
        {
            sshSession.close();
        }
    }

    private SshSession getSshSession(Instance instance)
    {
        SshSession session = new SshSession(instance.getPublicDnsName(), sshUser, keyFile);

        int retryCount = 0;
        while (!session.init().isSuccess())
        {
            session.close();
            session = null;

            log.info(name + " - Waiting for SSH connection to succeed: " + instance);
            try
            {
                Thread.sleep(RETRY_WAIT);
            }
            catch (InterruptedException e)
            {
                log.warn(e);
            }
            if (++retryCount >= MAX_RETRY)
            {
                throw new RuntimeException(name + " - Timed out waiting for SSH connection to succeed: " + instance);
            }
            else
            {
                session = new SshSession(instance.getPublicDnsName(), sshUser, keyFile);
            }
        }
  
        
        log.info(name + " - Created SSH connection to hostname: " + instance.getPublicDnsName());

        return session;
    }
    
    /**
     * @return
     */
    private Integer getUnusedPort()
    {
        log.info(name + " - Searching for unused local port...");
        Integer retval = null;
        Random random = new Random();
        while(true)
        {
            int port = random.nextInt(MAX_PORT_RANGE - MIN_PORT_RANGE) + MIN_PORT_RANGE;
            Socket s = null;
            try
            {
                s = new Socket (LOOPBACK_ADDRESS, port);
            }
            catch (IOException e)
            {
                log.info(name + " - Found unused local port: " + port);
                retval = port;
                break;
            }
            finally
            {
                if(s != null)
                {
                    try
                    {
                        s.close();
                    }
                    catch (IOException e)
                    {
                       log.warn(e);
                    }
                }
            }
        }
        return retval;
    }

    public static void main(String[] args)
    {
         TestConfiguration testConfiguration = TestConfiguration.getInstance();
         for (TestDatabase testDatabase : testConfiguration.getTestDatabases())
         {
             try
             {
                 testDatabase.getDataSource();
             }
             finally
             {
                 testDatabase.close();
             }
         }
    }
}
