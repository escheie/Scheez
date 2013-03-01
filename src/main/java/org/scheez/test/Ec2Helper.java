package org.scheez.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.DefaultResourceLoader;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.AmazonEC2Async;
import com.amazonaws.services.ec2.AmazonEC2AsyncClient;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.DeleteKeyPairRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeKeyPairsRequest;
import com.amazonaws.services.ec2.model.DescribeKeyPairsResult;
import com.amazonaws.services.ec2.model.GroupIdentifier;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.KeyPair;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;

public class Ec2Helper
{
    public static final String STATE_PENDING = "pending";
    
    public static final String STATE_RUNNING = "running";
    
    public static final String STATE_SHUTTING_DOWN = "shutting-down";
    
    public static final String STATE_TERMINATED = "terminated";
    
    public static final String BEHAVIOR_TERMINATE = "terminate";
    
    private static final Log log = LogFactory.getLog(Ec2Helper.class);
    
    public static final String DEFAULT_CREDENTIALS_FILE = "awscredentials.properties";
    
    public static final String DEFAULT_REGION = "ec2.us-east-1.amazonaws.com";
    
    private AmazonEC2Async client;
    
    public Ec2Helper () 
    {
        this (getCredentials(DEFAULT_CREDENTIALS_FILE), DEFAULT_REGION);
    }
    
    public Ec2Helper(AWSCredentials credentials, String region)
    {
        client = new AmazonEC2AsyncClient(credentials);
        client.setEndpoint(region);
    }
    
    public KeyPair createKeyPair (String keyName, boolean overwrite)
    {
        if(overwrite)
        {
            client.deleteKeyPair(new DeleteKeyPairRequest(keyName));
        }
        CreateKeyPairResult result = client.createKeyPair(new CreateKeyPairRequest().withKeyName(keyName));
        return result.getKeyPair();
    }
    
    public File initializeKeyPair (String keyName, File keyDir) throws IOException
    {
        KeyPair keyPair = null;
        
        /// Load key on file system.
        File keyFile = new File(keyDir, keyName + ".key");
        File digestFile = new File(keyDir, keyName + ".digest");
        
        if((keyFile.exists()) && (digestFile.exists()))
        {
            String digest = FileUtils.readFileToString(digestFile);
            String material = FileUtils.readFileToString(keyFile);
            
            DescribeKeyPairsResult result = client.describeKeyPairs(new DescribeKeyPairsRequest().withKeyNames(keyName));
            if((!result.getKeyPairs().isEmpty()) && (result.getKeyPairs().get(0).getKeyFingerprint().equals(digest)))
            {
                keyPair = new KeyPair().withKeyFingerprint(digest).withKeyMaterial(material).withKeyName(keyName);
            }
        }  
        
        if(keyPair == null)
        {
            keyPair = createKeyPair(keyName, true);
            
            FileUtils.writeStringToFile(keyFile, keyPair.getKeyMaterial());
            FileUtils.writeStringToFile(digestFile, keyPair.getKeyFingerprint());
        }
        
        return keyFile;
    }
    
    public void createSecurityGroup (String name, String description)
    {
        CreateSecurityGroupRequest securityGroupRequest = new CreateSecurityGroupRequest();
        securityGroupRequest
                .withGroupName(name)
                .withDescription(description);

        try
        {
            client.createSecurityGroup(securityGroupRequest);
        }
        catch (AmazonServiceException e)
        {
            if (!e.getErrorCode().equals("InvalidGroup.Duplicate"))
            {
                throw e;
            }
        }
    }

    public void addIPPermission(String securityGroup, IpPermission ipPermission)
    {
        AuthorizeSecurityGroupIngressRequest authorizeSecurityGroupIngressRequest = new AuthorizeSecurityGroupIngressRequest();
        authorizeSecurityGroupIngressRequest.withGroupName(securityGroup).withIpPermissions(ipPermission);
        
        try
        {
            client.authorizeSecurityGroupIngress(authorizeSecurityGroupIngressRequest);
        }
        catch (AmazonServiceException e)
        {
            if (!e.getErrorCode().equals("InvalidPermission.Duplicate"))
            {
                throw e;
            }
        }
    }

    public List<Instance> getInstances()
    {
        LinkedList<Instance> instances = new LinkedList<Instance>();
        DescribeInstancesResult result = client.describeInstances();
        for (Reservation reservations : result.getReservations())
        {
            for (Instance instance : reservations.getInstances())
            {
                if (!instance.getState().getName().equals(STATE_TERMINATED))
                {
                    instances.add(instance);
                }
            }
        }
        return instances;
    }
    
    public List<Instance> getInstances(String securityGroup)
    {
        LinkedList<Instance> instances = new LinkedList<Instance>();
        DescribeInstancesResult result = client.describeInstances();
        for (Reservation reservations : result.getReservations())
        {
            for (Instance instance : reservations.getInstances())
            {
                if ((inSecurityGroup(securityGroup, instance)) && (!instance.getState().getName().equals(STATE_TERMINATED)))
                {
                    instances.add(instance);
                }
            }
        }
        return instances;
    }

    public Instance getInstance(String instanceId)
    {
        Instance retval = null;
        DescribeInstancesResult result = client.describeInstances(new DescribeInstancesRequest()
                .withInstanceIds(instanceId));
        for (Reservation reservations : result.getReservations())
        {
            for (Instance instance : reservations.getInstances())
            {
                if (!instance.getState().getName().equals(STATE_TERMINATED))
                {
                    retval = instance;
                }
            }
        }
        return retval;
    }

    private boolean inSecurityGroup (String securityGroup, Instance instance)
    {
        boolean inGroup = false;
        for (GroupIdentifier id : instance.getSecurityGroups())
        {
            if (id.getGroupName().equals(securityGroup))
            {
                inGroup = true;
                break;
            }
        }
        return inGroup;
    }

    public void terminateAll (String securityGroup)
    {
        TerminateInstancesRequest request = new TerminateInstancesRequest();
        for (Instance instance : getInstances(securityGroup))
        {
            request.withInstanceIds(instance.getInstanceId());
        }
        client.terminateInstances(request);
    }
    
    public void terminateInstance (String instanceId)
    {
        client.terminateInstances(new TerminateInstancesRequest().withInstanceIds(instanceId));
    }

    public Instance startInstance (String instanceType, String imageName, String securityGroup, String keyName, boolean waitForRunning)
    {
        RunInstancesRequest runInstancesRequest = new RunInstancesRequest();

        runInstancesRequest.withImageId(imageName).withInstanceType(instanceType).withMinCount(1).withMaxCount(1)
                .withSecurityGroups(securityGroup).withKeyName(keyName).withInstanceInitiatedShutdownBehavior(BEHAVIOR_TERMINATE);

        RunInstancesResult runInstancesResult = client.runInstances(runInstancesRequest);

        Instance instance = runInstancesResult.getReservation().getInstances().get(0);

        long start = System.currentTimeMillis();
        while (waitForRunning)
        {
            try
            {
                Thread.sleep(10000);
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            instance = getInstance(instance.getInstanceId());

            if (instance.getState().getName().equals(STATE_RUNNING))
            {
                log.info("Instance instance.getInstanceId() started after " + (System.currentTimeMillis() - start) / 1000 + " seconds.  "
                        + instance);
                break;
            }
            else
            {
                log.info("Waiting for instance " + instance.getInstanceId() + " to be started: " + instance);
            }
        }

        return instance;
    }
  
    private static AWSCredentials getCredentials (String resource) 
    {
        AWSCredentials credentials = null;
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
                credentials = new PropertiesCredentials(inputStream);
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
            throw new RuntimeException ("Unable to load credentials from resource: " + resource, e);
        }
        return credentials;
    }
}