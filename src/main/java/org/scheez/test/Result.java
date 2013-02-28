package org.scheez.test;

import java.util.HashMap;
import java.util.Map;


public class Result
{
    public static enum Code
    {
        SUCCESS,

        FAILURE,

        UNKNOWN_HOST,

        NO_CONNECTION,

        INVALID_CREDENTIALS,

        INVALID_DESTINATION,

        FILE_NOT_FOUND,
        
        NON_ZERO_EXIT_CODE(Param.EXIT_CODE);
        
        Param param[];

        private Code()
        {
            param = new Param[0];
        }

        private Code(Param... param)
        {
            this.param = param;
        }

        public Param[] getSupportedParams()
        {
            return param;
        }
    }

    public static enum Param
    {   
        EXIT_CODE;
    }

    private Code code;

    private String text;

    private Exception exception;

    private Map<Param, Object> params;
    
    public Result(Code code)
    {
        this(code, code.name(), null);
    }

    public Result(Code code, String text)
    {
        this(code, text, null);
    }

    public Result(Code code, String text, Exception exception)
    {
        this.code = code;
        this.text = text;
        this.exception = exception;
        params = new HashMap<Param, Object>();
    }

    /**
     * @return the code
     */
    public Code getCode()
    {
        return code;
    }

    /**
     * @return the text
     */
    public String getText()
    {
        return text;
    }

    /**
     * @return the exception
     */
    public Exception getException()
    {
        return exception;
    }

    /**
     * Sets a result paramater.
     * 
     * @param param
     *            the parameter to add.
     * @param object
     *            the result parameter.
     */
    public void setParam(Param param, Object object)
    {
        params.put(param, object);
    }

    /**
     * Gets a result parameter.
     * 
     * @param param
     *            the result parameter to get.
     * 
     * @return the specified result parameter else null if it does not exist.
     */
    public Object getParam(Param param)
    {
        return params.get(param);
    }

    /**
     * @return true if the result is success, else false.
     */
    public boolean isSuccess()
    {
        return code == Code.SUCCESS;
    }

    /**
     * @inheritDoc
     */
    public String toString()
    {
        return code.toString() + ": " + text
                + ((exception != null) ? ("\n" + exception.toString()) : "");
    }
}
