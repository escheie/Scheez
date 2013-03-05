package org.scheez.test.ec2;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.scheez.test.ec2.SshResult.Code;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

public class SftpSession
{
    private SshSession sshSession;

    private ChannelSftp channel;

    public SftpSession(SshSession sshSession)
    {
        this.sshSession = sshSession;
    }

    /**
     * @inheritDoc
     */
    public synchronized SshResult init()
    {
        SshResult result = new SshResult(Code.SUCCESS);
        if ((channel == null) || (!channel.isConnected()))
        {
            result = sshSession.init();
            if (result.isSuccess())
            {
                try
                {
                    channel = (ChannelSftp) sshSession.session.openChannel("sftp");
                    channel.connect();
                }
                catch (Exception e)
                {
                    result = new SshResult(Code.FAILURE, "Unable to open sftp channel.", e);
                }
            }
        }
        return result;
    }

    private void initChannel() throws SftpException
    {
        SshResult result = init();
        if (!result.isSuccess())
        {
            throw new SftpException(0, result.toString());
        }
    }

    /**
     * Checks whether a given directory path exists or not.
     * 
     * @param session
     * @param path
     * @return
     */
    public boolean dirExists(String path) throws SftpException
    {
        boolean result = false;
        SftpATTRS attributes = getAttributes(path);

        if ((attributes != null) && (attributes.isDir()))
        {
            result = true;
        }

        return result;
    }

    /**
     * Checks whether a given file path exists or not.
     * 
     * @param session
     * @param path
     * @return
     */
    public boolean fileExists(String path) throws SftpException
    {
        boolean result = false;
        SftpATTRS attributes = getAttributes(path);

        if ((attributes != null) && (!attributes.isDir()) && (!attributes.isLink()))
        {
            result = true;
        }

        return result;
    }

    /**
     * Returns file system attributes for a given path.
     * 
     * @param session
     * @param path
     * @return
     */
    public SftpATTRS getAttributes(String path) throws SftpException
    {
        initChannel();
        return channel.stat(path);
    }

    /**
     * Creates directory under a given parent directory. Also sets the default access rights and
     * ownership for the newly created directory.
     * 
     * @param session
     * @param parentPath
     * @param path
     * @throws Exception
     */
    public void mkdir(String parentPath, String path) throws SftpException
    {
        initChannel();
        SftpATTRS attributes = channel.stat(parentPath);
        int uid = attributes.getUId();
        int gid = attributes.getGId();

        channel.mkdir(path);
        channel.chmod(0700, path);
        channel.chown(uid, path);
        channel.chgrp(gid, path);
    }

    /**
     * 
     * @param session
     * @param parentPath
     * @param path
     * @throws Exception
     */
    public void changeAttributesForKeyFile(String parentPath, String path)
            throws Exception
    {
        initChannel();
        SftpATTRS attributes = channel.stat(parentPath);
        int uid = attributes.getUId();
        int gid = attributes.getGId();

        channel.chmod(0700, path);
        channel.chown(uid, path);
        channel.chgrp(gid, path);
    }

    /**
     * Copies the file specified by the local file path to the file specified by the remote file
     * path.
     * 
     * @param localFilePath
     * @param remoteFilePath
     * @return
     */
    public SshResult put(String localFilePath, String remoteFilePath)
    {
        SshResult result = init();
        InputStream input = null;
        try
        {
            input = new BufferedInputStream(new FileInputStream(localFilePath));
            channel.put(input, remoteFilePath);
        }
        catch (FileNotFoundException e)
        {
            result = new SshResult(SshResult.Code.FAILURE, e.getMessage(), e);
        }
        catch (SftpException e)
        {
            result = new SshResult(SshResult.Code.FAILURE, e.getMessage(), e);
        }
        finally
        {
            if (input != null)
            {
                try
                {
                    input.close();
                }
                catch (IOException e)
                {

                }
            }
        }
        return result;
    }

    /**
     * Copies the file specified by the remote file path to the file specified by the local file
     * path.
     * 
     * @param localFilePath
     * @param remoteFilePath
     * @return
     */
    public SshResult get(String remoteFilePath, String localFilePath)
    {
        SshResult result = init();
        OutputStream output = null;
        try
        {
            output = new BufferedOutputStream(new FileOutputStream(localFilePath));
            channel.get(remoteFilePath, output);
        }
        catch (FileNotFoundException e)
        {
            result = new SshResult(SshResult.Code.FAILURE, e.getMessage(), e);
        }
        catch (SftpException e)
        {
            result = new SshResult(SshResult.Code.FAILURE, e.getMessage(), e);
        }
        finally
        {
            if (output != null)
            {
                try
                {
                    output.close();
                }
                catch (IOException e)
                {

                }
            }
        }
        return result;
    }

    /**
     * @inheritDoc
     */
    public synchronized void close()
    {
        if (channel != null)
        {
            channel.disconnect();
            channel = null;
        }
    }
}
