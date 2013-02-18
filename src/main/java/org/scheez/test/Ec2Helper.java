package org.scheez.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
import com.amazonaws.services.ec2.model.GroupIdentifier;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.KeyPair;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.jcraft.jsch.JSch;

public class Ec2Helper
{
    private static final Log log = LogFactory.getLog(Ec2Helper.class);

    private AmazonEC2Async client;

    private String namespace;
    
    private KeyPair keyPair;

    public static Ec2Helper getInstance()
    {
        try
        {
            Ec2Helper ec2Helper = new Ec2Helper(getCredentials("awscredentials.properties"),
                    "ec2.us-east-1.amazonaws.com", "Scheez");
            ec2Helper.init();
            return ec2Helper;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Unable to load Amazone EC2 credentials.", e);
        }
    }

    public Ec2Helper(AWSCredentials credentials, String region, String securityGroup)
    {
        client = new AmazonEC2AsyncClient(credentials);
        client.setEndpoint(region);
        this.namespace = securityGroup;
    }

    private void init()
    {
        CreateSecurityGroupRequest securityGroupRequest = new CreateSecurityGroupRequest();
        securityGroupRequest
                .withGroupName(namespace)
                .withDescription(
                        "A security group used for integration testing the Scheez library against different database vendor instances.");

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
        
        addIPPermission(new IpPermission().withIpProtocol("tcp").withIpRanges("0.0.0.0/0").withFromPort(1024).withToPort(10000));
        addIPPermission(new IpPermission().withIpProtocol("tcp").withIpRanges("0.0.0.0/0").withFromPort(22).withToPort(22));
        addIPPermission(new IpPermission().withIpProtocol("icmp").withIpRanges("0.0.0.0/0").withFromPort(-1).withToPort(-1));
        
        client.deleteKeyPair(new DeleteKeyPairRequest(namespace));
        CreateKeyPairResult result = client.createKeyPair(new CreateKeyPairRequest().withKeyName(namespace));
        keyPair = result.getKeyPair();
        log.info(keyPair.getKeyMaterial());
    }

    private void addIPPermission(IpPermission ipPermission)
    {
        AuthorizeSecurityGroupIngressRequest authorizeSecurityGroupIngressRequest = new AuthorizeSecurityGroupIngressRequest();
        authorizeSecurityGroupIngressRequest.withGroupName(namespace).withIpPermissions(ipPermission);
        
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
                if ((inSecurityGroup(instance)) && (!instance.getState().getName().equals("terminated")))
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
                if ((inSecurityGroup(instance)) && (!instance.getState().getName().equals("terminated")))
                {
                    retval = instance;
                }
            }
        }
        return retval;
    }
    
    public void connect (Instance instance) throws Exception
    {
        JSch jsch = new JSch();
        jsch.getSession("root", instance.getPublicIpAddress());
    }  

    private boolean inSecurityGroup(Instance instance)
    {
        boolean inGroup = false;
        for (GroupIdentifier id : instance.getSecurityGroups())
        {
            if (id.getGroupName().equals(namespace))
            {
                inGroup = true;
                break;
            }
        }
        return inGroup;
    }

    public void terminateAll()
    {
        TerminateInstancesRequest request = new TerminateInstancesRequest();
        for (Instance instance : getInstances())
        {
            request.withInstanceIds(instance.getInstanceId());
        }
        client.terminateInstances(request);
    }
    
    public void terminateInstance (String instanceId)
    {
        client.terminateInstances(new TerminateInstancesRequest().withInstanceIds(instanceId));
    }

    public Instance startInstance(String imageName, boolean waitForRunning)
    {
        RunInstancesRequest runInstancesRequest = new RunInstancesRequest();

        runInstancesRequest.withImageId(imageName).withInstanceType("m1.small").withMinCount(1).withMaxCount(1)
                .withSecurityGroups(namespace).withKeyName(namespace);

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

            if (instance.getState().getName().equals("running"))
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
    
    public KeyPair getKeyPair ()
    {
        return keyPair;
    }
  
    private static AWSCredentials getCredentials(String resourceName) throws IOException
    {
        AWSCredentials credentials = null;
        InputStream inputStream = null;
        try
        {
            inputStream = Ec2Helper.class.getClassLoader().getResourceAsStream(resourceName);
            if (inputStream == null)
            {
                throw new RuntimeException("Resource not found: " + resourceName);
            }
            credentials = new PropertiesCredentials(inputStream);
        }
        finally
        {
            if (inputStream != null)
            {
                inputStream.close();
            }
        }
        return credentials;
    }

    public static void main(String[] args)
    {
        Ec2Helper ec2Helper = Ec2Helper.getInstance();

        List<Instance> instances = ec2Helper.getInstances();
        System.out.println("Listing existing images...");
        for (Instance instance : instances)
        {
            System.out.println(instance);
        }

//        if (instances.size() == 0)
//        {
//            System.out.println("Running new image...");
//            System.out.println(ec2Helper.startInstance("ami-a7c12fce", true));
//        }
//   
//        ec2Helper.terminateAll();
    }
}
