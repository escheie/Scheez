package org.scheez.test.ec2;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.scheez.test.ec2.SshResult.Param;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * @author es151000
 * @version $Id: SshSession.java 268418 2012-06-21 00:15:50Z sr186002 $
 */
public class SshSession implements Closeable
{
    private static final int TIMEOUT = 30000;
    
    private static final int DEFAULT_PORT = 22;

    protected Session session;

    private String host;

    private int port;

    private String username;

    private File identityFile;

    public SshSession(String host, String username, File identityFile)
    {
        this.host = host;
        this.port = DEFAULT_PORT;
        this.username = username;
        this.identityFile = identityFile;
    }

    /**
     * @return the host
     */
    public String getHost()
    {
        return host;
    }

    /**
     * @param host
     *            the host to set
     */
    public void setHost(String host)
    {
        this.host = host;
    }

    /**
     * @return the port
     */
    public int getPort()
    {
        return port;
    }

    /**
     * @param port
     *            the port to set
     */
    public void setPort(int port)
    {
        this.port = port;
    }

    /**
     * @return the username
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * @param username
     *            the username to set
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /**
     * @return the identityFile
     */
    public File getIdentityFile()
    {
        return identityFile;
    }

    /**
     * @param identityFile
     *            the identityFile to set
     */
    public void setIdentityFile(File identityFile)
    {
        this.identityFile = identityFile;
    }

    /**
     * Initialize session with password authentication.
     * 
     * @return
     */
    public synchronized SshResult init()
    {
        SshResult result = new SshResult(SshResult.Code.SUCCESS);
        if ((session == null) || (!session.isConnected()))
        {
            JSch jsch = new JSch();
            try
            {
                session = jsch.getSession(username, host, port);
                jsch.addIdentity(identityFile.getAbsolutePath());

                JSch.setConfig("StrictHostKeyChecking", "no");
                
                session.connect(TIMEOUT);
            }
            catch (JSchException e)
            {
                result = mapToResult(e);
            }
        }
        return result;
    }
    
    /**
     * Runs the specified command using SSH.
     */
    public SshResult runCommand(String cmd)
    {
        return runCommand(cmd, false);
    }

    /**
     * Runs the specified command using SSH.
     */
    public SshResult runCommand(String cmd, boolean skipOutput)
    {
        BufferedReader reader = null;
        SshResult result = init();
        if (result.isSuccess())
        {
            Channel channel = null;
            try
            {
                channel = session.openChannel("exec");
                ((ChannelExec) channel).setCommand(cmd);
                channel.connect(TIMEOUT);
                StringBuilder cmdOut = new StringBuilder();
                if(!skipOutput)
                {
                    reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        cmdOut.append(line).append('\n');
                    }
                }
                int totalSleepTime = 0;
                int sleepTime = 100;
                while ((!channel.isClosed()) && (totalSleepTime < TIMEOUT))
                {
                    totalSleepTime += sleepTime;
                    Thread.sleep(sleepTime);
                }
                if (channel.getExitStatus() < 0)
                {
                    result = new SshResult(SshResult.Code.FAILURE,
                            "Timed out waiting on exit status from " + " command: " + cmd
                                    + "\nCommand Out:\n" + cmdOut);
                }
                else if (channel.getExitStatus() > 0)
                {
                    result = new SshResult(SshResult.Code.NON_ZERO_EXIT_CODE, "Exit Status: "
                            + channel.getExitStatus() + "\nCommand: " + cmd + "\nCommand Out:\n"
                            + cmdOut);
                    result.setParam(Param.EXIT_CODE, channel.getExitStatus());
                }
                else
                {
                    result = new SshResult(SshResult.Code.SUCCESS, cmdOut.toString());
                }
            }
            catch (IOException e)
            {
                result = new SshResult(SshResult.Code.FAILURE, "Error while running command \"" + cmd
                        + "\"", e);
            }
            catch (JSchException e)
            {
                result = mapToResult(e);
            }
            catch (InterruptedException e)
            {
                result = new SshResult(SshResult.Code.FAILURE, "Command interrupted:  " + cmd, e);
            }
            finally
            {
                if (reader != null)
                {
                    try
                    {
                        reader.close();
                    }
                    catch (IOException e)
                    {
                        // Ignore.
                    }
                }

                if (channel != null)
                {
                    channel.disconnect();
                }
            }
        }
        else
        {
            result = new SshResult(SshResult.Code.FAILURE, "Session does not exist");
        }

        return result;
    }
    
    public SshResult createTunnel (int localPort, String host, int remotePort)
    {
        SshResult result = new SshResult(SshResult.Code.SUCCESS);
        try
        {
            session.setPortForwardingL(localPort, host, remotePort);
        }
        catch (JSchException e)
        {
            result = mapToResult(e);
        }
        return result;
    }
   

    public synchronized void close()
    {
        if (session != null)
        {
            session.disconnect();
            session = null;
        }
    }

    protected SshResult mapToResult(JSchException e)
    {
        SshResult result = null;
        if ((e.getCause() instanceof UnknownHostException)
                || (e.getMessage().startsWith("java.net.UnknownHostException")))
        {
            result = new SshResult(SshResult.Code.UNKNOWN_HOST, "The host \"" + host
                    + "\" cannot be resolved to an IP address.", e);
        }
        else if (e.getCause() instanceof IllegalArgumentException)
        {
            throw (IllegalArgumentException) e.getCause();
        }
        else if (e.getCause() instanceof SocketException)
        {
            result = new SshResult(SshResult.Code.NO_CONNECTION, "Unable to connect to " + host
                    + " on port " + port + ".", e);
        }
        else if ((e.getMessage() != null) && (e.getMessage().trim().startsWith("Auth")))
        {
            result = new SshResult(SshResult.Code.INVALID_CREDENTIALS,
                    "The specified credentials are invalid.", e);
        }
        else
        {
            result = new SshResult(SshResult.Code.FAILURE, e.getMessage(), e);
        }
        return result;
    }

}
