package org.scheez.test.ec2;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.scheez.test.DefaultTestDatabase;
import org.scheez.test.ScheezTestConfiguration;
import org.scheez.test.TestDatabase;
import org.scheez.test.TestDatabaseProperties;
import org.scheez.util.DbC;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.KeyPair;
import com.amazonaws.services.ec2.model.SpotInstanceRequest;

public final class Ec2TestDatabase extends DefaultTestDatabase
{
    public static final String PROPERTY_IMAGE_ID = "imageId";

    public static final String PROPERTY_SECURITY_GROUP = "securityGroup";

    public static final String PROPERTY_KEY_NAME = "keyName";

    public static final String PROPERTY_INSTANCE_TYPE = "instanceType";

    public static final String PROPERTY_INSTANCE_ID = "instanceId";

    public static final String PROPERTY_SPOT_INSTANCE_REQUEST_ID = "spotInstanceRequestId";

    public static final String PROPERTY_SPOT_PRICE = "spotPrice";

    public static final String PROPERTY_HOURS_UNTIL_SHUTDOWN = "hoursUntilShutdown";

    public static final String PROPERTY_KEY_FILE = "keyFile";

    public static final String PROPERTY_KEY_DIGEST = "keyDigest";

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

    public static final String DEFAULT_INSTANCE_TYPE = "m1.small";

    public static final String DEFAULT_SPOT_PRICE = "0.06";

    public static final int DEFAULT_HOURS_UNTIL_SHUTDOWN = 1;

    public static final String DEFAULT_SCHEDULE_SHUTDOWN_COMMAND = "echo \"sudo shutdown -h now\" | at now + %d minutes";

    public static final int DEFAULT_SSH_PORT = 22;

    private static final int MIN_PORT_RANGE = 5000;

    private static final int MAX_PORT_RANGE = 20000;

    public static final int MAX_RETRY = 12;

    public static final int RETRY_WAIT = 10000;

    private static final String LOOPBACK_ADDRESS = "127.0.0.1";

    private static final long MILLIS_IN_MIN = 60 * 1000;

    private static final int MINS_IN_HOUR = 60;

    private static final long SHUTDOWN_DURATION = 5;

    private String imageId;

    private String securityGroup;

    private String keyName;

    private String spotPrice;

    private int hoursUntilShutdown;

    private String instanceType;

    private String instanceId;

    private String spotInstanceRequestId;

    private File instanceFile;

    private Integer localPort;

    private Integer databasePort;

    private String sshUser;

    private Integer sshPort;

    private List<String> commands;

    private boolean scheduleShutdown;

    private String scheduleShutdownCommand;

    private boolean staged;

    private File keyFile;

    private File keyDigest;

    private SshSession sshSession;

    private Ec2Helper ec2Helper;

    private boolean initialized;
    
    private long startTime;

    private TestDatabaseProperties instanceProperties;

    /**
     * @param name
     */
    public Ec2TestDatabase(String name)
    {
        super(name);
        ec2Helper = new Ec2Helper();
        commands = new ArrayList<String>();
        
        instanceFile = new File(TMP_DIR, name + ".properties");
        
        instanceType = DEFAULT_INSTANCE_TYPE;

        securityGroup = DEFAULT_SECURITY_GROUP;
        try
        {
            keyName = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e)
        {
            log.warn(name + " - Unable to determine local host name.");
        }
        keyFile = new File(TMP_DIR, "ec2.key");
        keyDigest = new File(TMP_DIR, "ec2.key.digest");

        sshPort = DEFAULT_SSH_PORT;

        scheduleShutdown = true;
        scheduleShutdownCommand = DEFAULT_SCHEDULE_SHUTDOWN_COMMAND;

        spotPrice = DEFAULT_SPOT_PRICE;
        hoursUntilShutdown = DEFAULT_HOURS_UNTIL_SHUTDOWN;
    }

    protected void initializeFromProperties(TestDatabaseProperties properties)
    {
        super.initializeFromProperties(properties);

        setInstanceFile(new File(properties.getProperty(PROPERTY_INSTANCE_FILE, instanceFile.getAbsolutePath())));

        setImageId(properties.getProperty(PROPERTY_IMAGE_ID, true, true));
        setInstanceType(properties.getProperty(PROPERTY_INSTANCE_TYPE, instanceType));

        setSecurityGroup(properties.getProperty(PROPERTY_SECURITY_GROUP, securityGroup));
        setKeyName(properties.getProperty(PROPERTY_KEY_NAME, keyName));
        setKeyFile(new File(properties.getProperty(PROPERTY_KEY_FILE, keyFile.getAbsolutePath())));
        setKeyDigest(new File(properties.getProperty(PROPERTY_KEY_DIGEST,
                keyDigest.getAbsolutePath())));

        setSshUser(properties.getProperty(PROPERTY_SSH_USER, true, true));
        setSshPort(properties.getInteger(PROPERTY_SSH_PORT, sshPort));

        setLocalPort(properties.getInteger(PROPERTY_LOCAL_PORT, false));
        setDatabasePort(properties.getInteger(PROPERTY_DATABASE_PORT, true));

        setScheduleShutdown(properties.getBoolean(PROPERTY_SCHEDULE_SHUTDOWN, scheduleShutdown));
        setScheduleShutdownCommand(properties.getProperty(PROPERTY_SCHEDULE_SHUTDOWN_COMMAND,
                scheduleShutdownCommand));
        
        setSpotPrice(properties.getProperty(PROPERTY_SPOT_PRICE, spotPrice));
        setHoursUntilShutdown(properties.getInteger(PROPERTY_HOURS_UNTIL_SHUTDOWN, hoursUntilShutdown));

        TestDatabaseProperties commandProperties = properties.withPrefix(PROPERTY_COMMAND);
        int missingCommandCount = 0;
        int commandIndex = 0;
        while (missingCommandCount < 5)
        {
            String command = commandProperties.getProperty(Integer.toString(commandIndex++), false,
                    true);
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
     * @return the imageId
     */
    public String getImageId()
    {
        return imageId;
    }

    /**
     * @param imageId
     *            the imageId to set
     */
    public void setImageId(String imageId)
    {
        DbC.throwIfNullArg("imageId", imageId);
        this.imageId = imageId;
    }

    /**
     * @return the securityGroup
     */
    public String getSecurityGroup()
    {
        return securityGroup;
    }

    /**
     * @param securityGroup
     *            the securityGroup to set
     */
    public void setSecurityGroup(String securityGroup)
    {
        DbC.throwIfNullArg("securityGroup", securityGroup);
        this.securityGroup = securityGroup;
    }

    /**
     * @return the keyName
     */
    public String getKeyName()
    {
        return keyName;
    }

    /**
     * @param keyName
     *            the keyName to set
     */
    public void setKeyName(String keyName)
    {
        DbC.throwIfNullArg("keyName", keyName);
        this.keyName = keyName;
    }

    /**
     * @return the instanceType
     */
    public String getInstanceType()
    {
        return instanceType;
    }

    /**
     * @param instanceType
     *            the instanceType to set
     */
    public void setInstanceType(String instanceType)
    {
        DbC.throwIfNullArg("instanceType", instanceType);
        this.instanceType = instanceType;
    }

    /**
     * @return the instanceId
     */
    public String getInstanceId()
    {
        return instanceId;
    }

    /**
     * @param instanceId
     *            the instanceId to set
     */
    public void setInstanceId(String instanceId)
    {
        DbC.throwIfNullArg("instaceId", instanceId);
        this.instanceId = instanceId;
    }

    /**
     * @return the spotInstanceRequestId
     */
    public String getSpotInstanceRequestId()
    {
        return spotInstanceRequestId;
    }

    /**
     * @param spotInstanceRequestId
     *            the spotInstanceRequestId to set
     */
    public void setSpotInstanceRequestId(String spotInstanceRequestId)
    {
        DbC.throwIfNullArg("spotInstanceRequestId", spotInstanceRequestId);
        this.spotInstanceRequestId = spotInstanceRequestId;
    }

    /**
     * @return the instanceFile
     */
    public File getInstanceFile()
    {
        return instanceFile;
    }

    /**
     * @param instanceFile
     *            the instanceFile to set
     */
    public void setInstanceFile(File instanceFile)
    {
        DbC.throwIfNullArg("instanceFile", instanceFile);
        this.instanceFile = instanceFile;
    }

    /**
     * @return the localPort
     */
    public Integer getLocalPort()
    {
        return localPort;
    }

    /**
     * @param localPort
     *            the localPort to set
     */
    public void setLocalPort(Integer localPort)
    {
        this.localPort = localPort;
    }

    /**
     * @return the databasePort
     */
    public Integer getDatabasePort()
    {
        return databasePort;
    }

    /**
     * @param databasePort
     *            the databasePort to set
     */
    public void setDatabasePort(Integer databasePort)
    {
        DbC.throwIfNullArg("databasePort", databasePort);
        this.databasePort = databasePort;
    }

    /**
     * @return the sshUser
     */
    public String getSshUser()
    {
        return sshUser;
    }

    /**
     * @param sshUser
     *            the sshUser to set
     */
    public void setSshUser(String sshUser)
    {
        DbC.throwIfNullArg("sshUser", sshUser);
        this.sshUser = sshUser;
    }

    /**
     * @return the sshPort
     */
    public Integer getSshPort()
    {
        return sshPort;
    }

    /**
     * @param sshPort
     *            the sshPort to set
     */
    public void setSshPort(Integer sshPort)
    {
        DbC.throwIfNullArg("sshPort", sshPort);
        this.sshPort = sshPort;
    }

    /**
     * @return the scheduleShutdown
     */
    public boolean isScheduleShutdown()
    {
        return scheduleShutdown;
    }

    /**
     * @param scheduleShutdown
     *            the scheduleShutdown to set
     */
    public void setScheduleShutdown(boolean scheduleShutdown)
    {
        DbC.throwIfNullArg("scheduleShutdown", scheduleShutdown);
        this.scheduleShutdown = scheduleShutdown;
    }

    /**
     * @return the scheduleShutdownCommand
     */
    public String getScheduleShutdownCommand()
    {
        return scheduleShutdownCommand;
    }

    /**
     * @param scheduleShutdownCommand
     *            the scheduleShutdownCommand to set
     */
    public void setScheduleShutdownCommand(String scheduleShutdownCommand)
    {
        DbC.throwIfNullArg("scheduleShutdownCommand", scheduleShutdownCommand);
        this.scheduleShutdownCommand = scheduleShutdownCommand;
    }

    /**
     * @return the staged
     */
    public boolean isStaged()
    {
        return staged;
    }

    /**
     * @param staged
     *            the staged to set
     */
    public void setStaged(boolean staged)
    {
        this.staged = staged;
    }

    /**
     * @return the keyFile
     */
    public File getKeyFile()
    {
        return keyFile;
    }

    /**
     * @param keyFile
     *            the keyFile to set
     */
    public void setKeyFile(File keyFile)
    {
        DbC.throwIfNullArg("keyFile", keyFile);
        this.keyFile = keyFile;
    }

    /**
     * @return the keyDigest
     */
    public File getKeyDigest()
    {
        return keyDigest;
    }

    /**
     * @param keyDigest
     *            the keyDigest to set
     */
    public void setKeyDigest(File keyDigest)
    {
        DbC.throwIfNullArg("keyDigest", keyDigest);
        this.keyDigest = keyDigest;
    }

    /**
     * @return the spotPrice
     */
    public String getSpotPrice()
    {
        return spotPrice;
    }

    /**
     * @param spotPrice
     *            the spotPrice to set
     */
    public void setSpotPrice(String spotPrice)
    {
        DbC.throwIfNullArg("spotPrice", spotPrice);
        this.spotPrice = spotPrice;
    }

    /**
     * @return the hoursUntilShutdown
     */
    public int getHoursUntilShutdown()
    {
        return hoursUntilShutdown;
    }

    /**
     * @param hoursUntilShutdown
     *            the hoursUntilShutdown to set
     */
    public void setHoursUntilShutdown(int hoursUntilShutdown)
    {
        this.hoursUntilShutdown = hoursUntilShutdown;
    }

    /**
     * @return the commands
     */
    public List<String> getCommands()
    {
        return commands;
    }

    /**
     * 
     */
    private void initSecurityGroup()
    {
        ec2Helper
                .createSecurityGroup(securityGroup,
                        "A security group used for integration testing with different database vendor instances.");

        ec2Helper.addIPPermission(securityGroup, new IpPermission().withIpProtocol("tcp")
                .withIpRanges("0.0.0.0/0")
                .withFromPort(sshPort).withToPort(sshPort));
        ec2Helper.addIPPermission(securityGroup, new IpPermission().withIpProtocol("icmp")
                .withIpRanges("0.0.0.0/0")
                .withFromPort(-1).withToPort(-1));
    }

    /**
     * 
     */
    private void initKeyPair()
    {
        try
        {
            KeyPair keyPair = null;

            if ((keyFile.exists()) && (keyDigest.exists()))
            {
                String digest = FileUtils.readFileToString(keyDigest);
                String material = FileUtils.readFileToString(keyFile);

                String fingerprint = ec2Helper.getKeyFingerprint(keyName);
                if ((fingerprint != null) && (fingerprint.equals(digest)))
                {
                    keyPair = new KeyPair().withKeyFingerprint(digest).withKeyMaterial(material)
                            .withKeyName(keyName);
                }
                else
                {
                    log.warn(name + " - Existing key digest (" + keyDigest.getAbsolutePath()
                            + ") does not match the current EC2 key with keyName: " + keyName);
                }
            }

            if (keyPair == null)
            {
                keyPair = ec2Helper.createKeyPair(keyName, true);

                FileUtils.writeStringToFile(keyFile, keyPair.getKeyMaterial());
                FileUtils.writeStringToFile(keyDigest, keyPair.getKeyFingerprint());
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("Unable to initialize key pair for " + name + ". KeyName: "
                    + keyName
                    + "  KeyFile: " + keyFile + ", KeyDigest: " + keyDigest, e);
        }
    }

    /**
     * @return
     */
    private Instance startNewInstance()
    {
        if(imageId == null)
        {
            throw new RuntimeException ("Unable to start a new " + name + " instance. The imageId is null.");
        }
        
        initSecurityGroup();
        initKeyPair();

        if (spotInstanceRequestId != null)
        {
            ec2Helper.cancelSpotInstanceRequest(spotInstanceRequestId);
        }

        log.info(name + " - Requesting new EC2 spot instance (imageId=\"" + imageId
                + "\", instanceType=\"" + instanceType
                + "\", spotPrice=\"" + spotPrice
                + "\")...");
        SpotInstanceRequest request = ec2Helper.startSpotInstance(spotPrice, instanceType,
                imageId, securityGroup, keyName);

        staged = false;
        instanceId = null;
        spotInstanceRequestId = request.getSpotInstanceRequestId();

        instanceProperties.setProperty(PROPERTY_SPOT_INSTANCE_REQUEST_ID, spotInstanceRequestId);
        instanceProperties.remove(PROPERTY_INSTANCE_ID);
        instanceProperties.setProperty(PROPERTY_STAGED, Boolean.toString(false));
        instanceProperties.save(instanceFile);

        instanceId = waitForSpotRequest(request.getSpotInstanceRequestId());

        Instance instance = waitForRunningInstance(instanceId);
        
        instanceProperties.setProperty(PROPERTY_INSTANCE_ID, instanceId);
        instanceProperties.save(instanceFile);
        
        return instance;
    }

    /**
     * 
     */
    private String waitForSpotRequest(String requestId)
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
        if (spotInstanceRequest.getState().equals(Ec2Helper.REQUEST_STATE_ACTIVE))
        {
            log.info(name + " - Spot Request fullfilled in " + (System.currentTimeMillis() - start)
                    / 1000 + " seconds. " + spotInstanceRequest);
            instanceId = spotInstanceRequest.getInstanceId();
            startTime = System.currentTimeMillis();
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
    private Instance waitForRunningInstance(String instanceId)
    {
        long start = System.currentTimeMillis();
        Instance instance = ec2Helper.getInstance(instanceId);
        while ((instance != null)
                && (instance.getState().getName().equals(Ec2Helper.STATE_PENDING)))
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
        if (instance == null)
        {
            log.info(name + " - Instance is not running with InstanceId: " + instanceId);
        }
        if (instance.getState().getName().equals(Ec2Helper.STATE_RUNNING))
        {
            log.info(name + " - Instance transitioned into running state after "
                    + (System.currentTimeMillis() - start) / 1000 + " seconds.");
        }
        else if (instance.getState().getName().equals(Ec2Helper.STATE_PENDING))
        {
            log.error(name + " - Instance is not running after waiting "
                    + (System.currentTimeMillis() - start) / 1000 + " seconds.");
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
                if (instanceId != null)
                {
                    instance = waitForRunningInstance(instanceId);
                    
                    instanceProperties.setProperty(PROPERTY_INSTANCE_ID, instanceId);
                    instanceProperties.save(instanceFile);
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
    private void scheduleShutdown(SshSession session)
    {
        if (scheduleShutdown)
        {
            log.info(name + " - Scheduling shutdown in " + hoursUntilShutdown + " hours minus the time it took to start and the time it will take to stop.");
            
            long mins =  (hoursUntilShutdown * MINS_IN_HOUR) - (((System.currentTimeMillis() - startTime)/MILLIS_IN_MIN) + SHUTDOWN_DURATION);
            
            String cmd = String.format(scheduleShutdownCommand, mins);
            
            SshResult result = session.runCommand(cmd);
            if (!result.isSuccess())
            {
                log.error(name + " - Scheduling shutdown failed. Command: "
                        + cmd + "\nResult: "
                        + result);
            }
            else
            {
                log.info(name + " - Shutdown scheduled successfully: " + cmd);
            }
        }
        else
        {
            log.info(name + " - Skipping scheduled shutdown.");
        }
    }

    protected synchronized DataSource initializeDataSource(boolean reinitialize)
    {
        if ((!initialized) || (reinitialize))
        {
            if(instanceProperties == null)
            {
                loadInstanceProperties ();
            }
            
            if(sshSession != null)
            {
               sshSession.close();
               sshSession = null;
            }
            
            Instance instance = findExistingInstance();

            if (instance == null)
            {
                instance = startNewInstance();
            }
            
            sshSession = getSshSession(instance);
            setupSshTunnel(instance);

            if (!staged)
            {
               stageDatabase ();
            }

            initialized = true;
        }
        return super.initializeDataSource(reinitialize);
    }

    /**
     * 
     */
    private void loadInstanceProperties()
    {
        if (instanceFile.exists())
        {
            log.info(name + " - Loading EC2 instance properties from " + instanceFile.getAbsolutePath()
                    + ".");
            instanceProperties = TestDatabaseProperties.load("file:"
                    + instanceFile.getAbsolutePath());
        }
        else
        {
            log.info(name + " - No existing instance file found: " + instanceFile.getAbsolutePath());
            instanceProperties = new TestDatabaseProperties();
        }
        instanceId = instanceProperties.getProperty(PROPERTY_INSTANCE_ID, instanceId);
        spotInstanceRequestId = instanceProperties.getProperty(PROPERTY_SPOT_INSTANCE_REQUEST_ID,
                spotInstanceRequestId);
        staged = instanceProperties.getBoolean(PROPERTY_STAGED, staged);
    }

    /**
     * 
     */
    private void stageDatabase ()
    {
        scheduleShutdown(sshSession);

        for (String command : commands)
        {
            log.info(name + " - Running Command: " + command);
            SshResult result = sshSession.runCommand(command);
            if (!result.isSuccess())
            {
                log.error(name + " - Command failed: " + command + "\nResult: " + result);
                throw new RuntimeException("Command failed: " + command + "\nResult: "
                        + result);
            }
        }

        staged = true;
        instanceProperties.setProperty(PROPERTY_STAGED, Boolean.toString(true));
        instanceProperties.save(instanceFile);
        
    }

    @Override
    public void close()
    {
        super.close();
        if (sshSession != null)
        {
            sshSession.close();
        }
    }

    private SshSession getSshSession(Instance instance)
    {
        if(sshUser == null)
        {
            throw new RuntimeException ("Unable to create ssh session to " + name + ". The sshUser is null.");
        }
        
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
                throw new RuntimeException(name
                        + " - Timed out waiting for SSH connection to succeed: " + instance);
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
     * 
     */
    private void setupSshTunnel(Instance instance)
    {
        if (databasePort == null)
        {
            throw new RuntimeException ("Unable to create SSH Tunnel to " + name + ". The databasePort is null.");
        }
        
        if (localPort == null)
        {
            localPort = getUnusedPort();
        }

        log.info(name + " - Creating SSH tunnel from localhost:" + localPort + " to "
                + instance.getPublicDnsName() + ":" + databasePort);
        SshResult result = sshSession.createTunnel(localPort, LOOPBACK_ADDRESS, databasePort);

        if (!result.isSuccess())
        {
            log.error(name + " - Unable to create ssh tunnel.  Result: " + result);

            sshSession.close();
            sshSession = null;

            throw new RuntimeException("Unable to create SSH tunnel from localhost:"
                    + localPort +
                    " to " + instance.getPublicDnsName() + ":" + databasePort
                    + ".  Result: " + result);
        }

        url = url.replaceAll("(.*//)([^/]*)/?(.*)", "$1" + LOOPBACK_ADDRESS + ":"
                + localPort + "/$3");
        log.info(name + " - Updating DataSource URL: " + url);
        
    }

    /**
     * @return
     */
    private Integer getUnusedPort()
    {
        log.info(name + " - Searching for unused local port...");
        Integer retval = null;
        Random random = new Random();
        while (true)
        {
            int port = random.nextInt(MAX_PORT_RANGE - MIN_PORT_RANGE) + MIN_PORT_RANGE;
            Socket s = null;
            try
            {
                s = new Socket(LOOPBACK_ADDRESS, port);
            }
            catch (IOException e)
            {
                log.info(name + " - Found unused local port: " + port);
                retval = port;
                break;
            }
            finally
            {
                if (s != null)
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
        ScheezTestConfiguration testConfiguration = ScheezTestConfiguration.getInstance();
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
