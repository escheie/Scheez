package org.scheez.test.ec2;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scheez.test.SimpleTestDatabase;
import org.scheez.test.TestConfiguration;
import org.scheez.test.TestDatabase;
import org.scheez.test.TestDatabaseProperties;
import org.springframework.jdbc.core.JdbcTemplate;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.IpPermission;

public final class Ec2TestDatabase extends SimpleTestDatabase
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
    
    public static final String PROPERTY_SSH_PORT = "sshPort";
    
    public static final String PROPERTY_LOCAL_PORT = "localPort";
    
    public static final String PROPERTY_DATABASE_PORT = "databasePort";

    public static final String PROPERTY_SCHEDULE_SHUTDOWN = "scheduleShutdown";

    public static final String PROPERTY_SCHEDULE_SHUTDOWN_COMMAND = "scheduleShutdownCommand";

    public static final String PROPERTY_COMMAND = "command";

    public static final String PROPERTY_STAGED = "staged";

    public static final String PROPERTY_TEST_SQL = "testSql";

    public static final String TMP_DIR = "build/ec2";

    public static final String DEFAULT_SECURITY_GROUP = "scheez";

    public static final String DEFAULT_KEY_NAME = "scheez";

    public static final String DEFAULT_INSTANCE_TYPE = "m1.small";

    public static final String DEFAULT_SCHEDULE_SHUTDOWN_COMMAND = "echo \"sudo shutdown -h now\" | at now + 55 minutes";
    
    public static final int DEFAULT_SSH_PORT = 22;

    private static final int MAX_RETRY = 10;
    
    private static final int RETRY_WAIT = 10000;

    private static final int MIN_PORT_RANGE = 5000;

    private static final int MAX_PORT_RANGE = 20000;

    private static final String LOOPBACK_ADDRESS = "127.0.0.1";

    private String imageId;

    private String securityGroup;

    private String keyName;

    private String instanceType;

    private String instanceId;

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

    private String testSql;

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
        testSql = properties.getProperty(PROPERTY_TEST_SQL, "SELECT CURRENT_TIMESTAMP");

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
        log.info(name + " - Starting new EC2 instance (imageId=\"" + imageId + "\", instanceType=\"" + instanceType
                + "\")...");
        Instance instance = ec2Helper.startInstance(instanceType, imageId, securityGroup, keyName);

        instanceId = instance.getInstanceId();
        staged = false;
        if(sshSession != null)
        {
            sshSession.close();
            sshSession = null;
        }
        
        properties.setProperty(PROPERTY_INSTANCE_ID, instanceId);
        properties.setProperty(PROPERTY_STAGED, Boolean.toString(false));
        properties.save(new File(instanceFile));
        
        return waitForRunningInstance (instance);
    }

    /**
     * 
     */
    private Instance waitForRunningInstance (Instance instance)
    {
        long start = System.currentTimeMillis();
        while (instance.getState().getName().equals(Ec2Helper.STATE_PENDING))
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
            instance = ec2Helper.getInstance(instance.getInstanceId());
        }
        log.info(name + " - Instance transitioned from pending state in " + (System.currentTimeMillis() - start) / 1000 + " seconds.");
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
                instance = waitForRunningInstance(i);
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
            Result result = session.runCommand(scheduleShutdownCommand, true);
            if (!result.isSuccess())
            {
                log.error(name + " - Scheduling shutdown failed. Command: " + scheduleShutdownCommand + "\nResult: "
                        + result);
            }
        }
        else
        {
            log.info(name + " - Skipping scheduled shutdown.");
        }
    }

    /**
     * 
     */
    public DataSource getDataSource()
    {
        initialize (false);
        
        int retryCount = 0;
        RuntimeException ex = null;
        while (retryCount++ <= MAX_RETRY)
        {
            if(ex != null)
            {
                log.warn(name + " - Unable to connect to database: " + ex.getMessage());
                log.info(name + " - Checking EC2 instance status after short delay.");
                try
                {
                    Thread.sleep(RETRY_WAIT);
                }
                catch (InterruptedException e)
                {
                    log.warn(e);
                }
                initialize(true);
            }
            
            try
            {
                DataSource dataSource = super.getDataSource();
                JdbcTemplate jdbcTemplate = new JdbcTemplate(super.getDataSource());
                long time = System.currentTimeMillis();
                String value = jdbcTemplate.queryForObject(testSql, String.class);
                log.info (name + " - Verified database connection.  Test Sql: " + testSql + ",  Result: " + value + ",  Duration: " + (System.currentTimeMillis() - time)/1000f + "s");
                return dataSource;
            }
            catch (RuntimeException e)
            {
                ex = e;
            }
        }
        throw ex;
    }
    
    private synchronized void initialize (boolean force)
    {
        if((!initialized) || (force))
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
                log.error(name + " - Timed out waiting for SSH connection to succeed: " + instance);
                break;
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
        for (int port = MIN_PORT_RANGE; port <= MAX_PORT_RANGE; port++)
        {
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
