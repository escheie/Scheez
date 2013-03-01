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

    public static final String PROPERTY_SCHEDULE_SHUTDOWN = "scheduleShutdown";

    public static final String PROPERTY_SCHEDULE_SHUTDOWN_COMMAND = "scheduleShutdownCommand";

    public static final String PROPERTY_COMMAND = "command";

    public static final String PROPERTY_STAGED = "staged";

    public static final String PROPERTY_TEST_SQL = "testSql";

    public static final String TMP_DIR = "build/ec2";

    public static final String DEFAULT_SECURITY_GROUP = "scheez";

    public static final String DEFAULT_KEY_NAME = "scheez";

    public static final String DEFAULT_INSTANCE_TYPE = "m1.small";

    private static final String DEFAULT_SCHEDULE_SHUTDOWN_COMMAND = "sudo shutdown -h +55 < /dev/null &> /dev/null &";

    protected String imageId;

    protected String securityGroup;

    protected String keyName;

    protected String instanceType;

    protected String instanceId;

    protected String keyDir;

    protected String instanceFile;

    protected String sshUser;

    protected List<String> commands;

    protected boolean scheduleShutdown;

    protected String scheduleShutdownCommand;

    protected boolean staged;

    protected String testSql;

    protected File keyFile;

    protected Instance instance;

    protected SshSession sshSession;

    protected Ec2Helper ec2Helper;

    public void initialize(String name, TestDatabaseProperties properties)
    {
        super.initialize(name, properties);
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
     * @return
     */
    protected Instance startNewInstance()
    {
        staged = false;

        log.info(name + " - Starting new EC2 instance (imageId=\"" + imageId + "\", instanceType=\"" + instanceType
                + "\")...");
        Instance instance = ec2Helper.startInstance(instanceType, imageId, securityGroup, keyName, true);

        instanceId = instance.getInstanceId();
        properties.setProperty(PROPERTY_INSTANCE_ID, instanceId);
        properties.setProperty(PROPERTY_STAGED, Boolean.toString(false));
        properties.save(new File(instanceFile));

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
            throw new RuntimeException("Unable to initialize key pair for " + name + ". KeyName: " + keyName
                    + "  KeyDir: " + keyDir, e);
        }
    }

    /**
     * 
     */
    protected void initSecurityGroup()
    {
        ec2Helper.createSecurityGroup(securityGroup,
                "A security group used for integration testing with different database vendor instances.");

        ec2Helper.addIPPermission(securityGroup, new IpPermission().withIpProtocol("tcp").withIpRanges("0.0.0.0/0")
                .withFromPort(0).withToPort((int) Math.pow(2, 16) - 1));
        ec2Helper.addIPPermission(securityGroup, new IpPermission().withIpProtocol("icmp").withIpRanges("0.0.0.0/0")
                .withFromPort(-1).withToPort(-1));
    }

    /**
     * 
     */
    protected void loadProperties()
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
            properties = new TestDatabaseProperties(properties);
        }

        imageId = properties.getProperty(PROPERTY_IMAGE_ID, true, true);
        instanceType = properties.getProperty(PROPERTY_INSTANCE_TYPE, DEFAULT_INSTANCE_TYPE);
        instanceId = properties.getProperty(PROPERTY_INSTANCE_ID, false, true);
        securityGroup = properties.getProperty(PROPERTY_SECURITY_GROUP, DEFAULT_SECURITY_GROUP);
        keyName = properties.getProperty(PROPERTY_KEY_NAME, DEFAULT_KEY_NAME);
        keyDir = properties.getProperty(PROPERTY_KEY_DIR, new File(TMP_DIR).getAbsolutePath());
        sshUser = properties.getProperty(PROPERTY_SSH_USER, true, true);

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
    public DataSource getDataSource()
    {
        if (instanceId == null)
        {
            log.info(name + " - No previous Ec2 instanceId found.");
        }
        else
        {
            log.info(name + " - Checking previous Ec2 instanceId: " + instanceId);
            Instance i = ec2Helper.getInstance(instanceId);
            if (i == null)
            {
                log.info(name + " - No instance found with instanceId: " + instanceId);
            }
            else if (i.getState().getName().equals(Ec2Helper.STATE_RUNNING))
            {
                instance = i;
                log.info(name + " - Using existing instance: " + instance);
            }
            else
            {
                log.info(name + " - Instance is not running with instanceId: " + i);
            }
        }

        if (instance == null)
        {
            if(sshSession != null)
            {
                sshSession.close();
                sshSession = null;
            }
            instance = startNewInstance();
        }
        
        boolean newSession = false;
        if(sshSession == null)
        {
            newSession = true;
            sshSession = getSshSession(120000, 10000);
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
                }
            }

            staged = true;
            properties.setProperty(PROPERTY_STAGED, Boolean.toString(true));
            properties.save(new File(instanceFile));
        }

        if(newSession)
        {
            url = url.replaceAll("//.*/", "//" + instance.getPublicDnsName() + "/");
            log.info(name + " - Updating DataSource URL: " + url);
        }

        DataSource dataSource = super.getDataSource();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(super.getDataSource());
        try
        {
            String value = jdbcTemplate.queryForObject(testSql, String.class);
            if(newSession)
            {
                throw new RuntimeException ("Testing ssh tunnel.");
            }
            else
            {
                log.info (name + " - Verified database connection.  Test Sql: " + testSql + ",  Result: " + value);
            }
            
        }
        catch (RuntimeException e)
        {
            if(newSession)
            {
                log.warn(name + " - Unable to connect to database: " + e.getMessage());  
                log.info(name + " - Creating SSH tunnel to see if its a firewall issue.");
    
                Result result = sshSession.createTunnel(5433, 5432);
                
                if(!result.isSuccess())
                {
                    log.error(name + " - Unable to create ssh tunnel.  Result: " + result);
                    throw new RuntimeException ("Unable to create data source.", e);
                }
    
                url = url.replaceAll("//.*/", "//127.0.0.1:5433/");
                log.info(name + " - Updating DataSource URL: " + url);
               
                dataSource = super.getDataSource();
                jdbcTemplate = new JdbcTemplate(super.getDataSource());
                String value = jdbcTemplate.queryForObject(testSql, String.class);
                log.info(name + " - Verified database connection.  Test Sql: " + testSql + ",  Result: " + value);
            }
            else
            {
                throw e;
            }
        }

        return dataSource;
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

    private SshSession getSshSession(int timeout, int increments)
    {
        SshSession session = new SshSession(instance.getPublicDnsName(), sshUser, keyFile);

        int time = 0;
        while (!session.init().isSuccess())
        {
            session.close();
            session = null;

            log.info(name + " - Waiting for SSH connection to succeed: " + instance);
            try
            {
                Thread.sleep(increments);
            }
            catch (InterruptedException e)
            {
                log.warn(e);
            }
            time += increments;
            if (time >= timeout)
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

    public static void main(String[] args)
    {
        startPostgresqlEc2();
    }

    private static void startPostgresqlEc2()
    {
        TestDatabaseProperties properties = new TestDatabaseProperties().withPrefix("postgresql-ec2");
        properties.setProperty(PROPERTY_URL, "jdbc:postgresql://localhost/scheez");
        properties.setProperty(PROPERTY_USERNAME, "postgres");
        properties.setProperty(PROPERTY_PASSWORD, "bitnami");
        properties.setProperty(PROPERTY_IMAGE_ID, "ami-31319958");
        properties.setProperty(PROPERTY_SSH_USER, "bitnami");
        properties.setProperty(PROPERTY_COMMAND + ".1", "sudo -u postgres sed -i 's/127.0.0.1/*/g' /opt/bitnami/postgresql/data/postgresql.conf 2>&1");
        properties.setProperty(PROPERTY_COMMAND + ".2", "echo -e \"local all all trust\\nhost all all 127.0.0.1/32 trust\\nhost all all ::1/128 trust\\nhost all all 0.0.0.0/0 md5\\nhost all all ::0/0 md5\" | sudo -u postgres tee /opt/bitnami/postgresql/data/pg_hba.conf 2>&1");
        properties.setProperty(PROPERTY_COMMAND + ".3", "sudo -u postgres /opt/bitnami/postgresql/bin/pg_ctl -w restart 2>&1");
        properties.setProperty(PROPERTY_COMMAND + ".4", "sudo -u postgres createdb scheez < /dev/null 2>&1");

        Ec2TestDatabase database = new Ec2TestDatabase();
        database.initialize("postgresql-ec2", properties);

        database.getDataSource();
        
        database.close();
    }
}