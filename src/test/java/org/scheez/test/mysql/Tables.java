/**
 * Tables.java
 */
package org.scheez.test.mysql;

import java.sql.Timestamp;

import org.scheez.util.BaseObject;

/**
 * This class was auto generated from the contents of a ResultSet using scheez.
 *
 * @author : Eric
 * @version : $Id$
 */
public class Tables extends BaseObject
{
    private String TABLE_CATALOG;

    private String TABLE_SCHEMA;

    private String TABLE_NAME;

    private String TABLE_TYPE;

    private String ENGINE;

    private Long VERSION;

    private String ROW_FORMAT;

    private Long TABLE_ROWS;

    private Long AVG_ROW_LENGTH;

    private Long DATA_LENGTH;

    private Long MAX_DATA_LENGTH;

    private Long INDEX_LENGTH;

    private Long DATA_FREE;

    private Long AUTO_INCREMENT;

    private Timestamp CREATE_TIME;

    private Timestamp UPDATE_TIME;

    private Timestamp CHECK_TIME;

    private String TABLE_COLLATION;

    private Long CHECKSUM;

    private String CREATE_OPTIONS;

    private String TABLE_COMMENT;

    /**
     * Setter for TABLE_CATALOG.
     *
     * @param TABLE_CATALOG  The value to set.
     */
    public void setTABLE_CATALOG(String TABLE_CATALOG)
    {
        this.TABLE_CATALOG = TABLE_CATALOG;
    }

    /**
     * Getter for TABLE_CATALOG.
     *
     * @return The value of TABLE_CATALOG.
     */
    public String getTABLE_CATALOG()
    {
        return TABLE_CATALOG;
    }

    /**
     * Setter for TABLE_SCHEMA.
     *
     * @param TABLE_SCHEMA  The value to set.
     */
    public void setTABLE_SCHEMA(String TABLE_SCHEMA)
    {
        this.TABLE_SCHEMA = TABLE_SCHEMA;
    }

    /**
     * Getter for TABLE_SCHEMA.
     *
     * @return The value of TABLE_SCHEMA.
     */
    public String getTABLE_SCHEMA()
    {
        return TABLE_SCHEMA;
    }

    /**
     * Setter for TABLE_NAME.
     *
     * @param TABLE_NAME  The value to set.
     */
    public void setTABLE_NAME(String TABLE_NAME)
    {
        this.TABLE_NAME = TABLE_NAME;
    }

    /**
     * Getter for TABLE_NAME.
     *
     * @return The value of TABLE_NAME.
     */
    public String getTABLE_NAME()
    {
        return TABLE_NAME;
    }

    /**
     * Setter for TABLE_TYPE.
     *
     * @param TABLE_TYPE  The value to set.
     */
    public void setTABLE_TYPE(String TABLE_TYPE)
    {
        this.TABLE_TYPE = TABLE_TYPE;
    }

    /**
     * Getter for TABLE_TYPE.
     *
     * @return The value of TABLE_TYPE.
     */
    public String getTABLE_TYPE()
    {
        return TABLE_TYPE;
    }

    /**
     * Setter for ENGINE.
     *
     * @param ENGINE  The value to set.
     */
    public void setENGINE(String ENGINE)
    {
        this.ENGINE = ENGINE;
    }

    /**
     * Getter for ENGINE.
     *
     * @return The value of ENGINE.
     */
    public String getENGINE()
    {
        return ENGINE;
    }

    /**
     * Setter for VERSION.
     *
     * @param VERSION  The value to set.
     */
    public void setVERSION(Long VERSION)
    {
        this.VERSION = VERSION;
    }

    /**
     * Getter for VERSION.
     *
     * @return The value of VERSION.
     */
    public Long getVERSION()
    {
        return VERSION;
    }

    /**
     * Setter for ROW_FORMAT.
     *
     * @param ROW_FORMAT  The value to set.
     */
    public void setROW_FORMAT(String ROW_FORMAT)
    {
        this.ROW_FORMAT = ROW_FORMAT;
    }

    /**
     * Getter for ROW_FORMAT.
     *
     * @return The value of ROW_FORMAT.
     */
    public String getROW_FORMAT()
    {
        return ROW_FORMAT;
    }

    /**
     * Setter for TABLE_ROWS.
     *
     * @param TABLE_ROWS  The value to set.
     */
    public void setTABLE_ROWS(Long TABLE_ROWS)
    {
        this.TABLE_ROWS = TABLE_ROWS;
    }

    /**
     * Getter for TABLE_ROWS.
     *
     * @return The value of TABLE_ROWS.
     */
    public Long getTABLE_ROWS()
    {
        return TABLE_ROWS;
    }

    /**
     * Setter for AVG_ROW_LENGTH.
     *
     * @param AVG_ROW_LENGTH  The value to set.
     */
    public void setAVG_ROW_LENGTH(Long AVG_ROW_LENGTH)
    {
        this.AVG_ROW_LENGTH = AVG_ROW_LENGTH;
    }

    /**
     * Getter for AVG_ROW_LENGTH.
     *
     * @return The value of AVG_ROW_LENGTH.
     */
    public Long getAVG_ROW_LENGTH()
    {
        return AVG_ROW_LENGTH;
    }

    /**
     * Setter for DATA_LENGTH.
     *
     * @param DATA_LENGTH  The value to set.
     */
    public void setDATA_LENGTH(Long DATA_LENGTH)
    {
        this.DATA_LENGTH = DATA_LENGTH;
    }

    /**
     * Getter for DATA_LENGTH.
     *
     * @return The value of DATA_LENGTH.
     */
    public Long getDATA_LENGTH()
    {
        return DATA_LENGTH;
    }

    /**
     * Setter for MAX_DATA_LENGTH.
     *
     * @param MAX_DATA_LENGTH  The value to set.
     */
    public void setMAX_DATA_LENGTH(Long MAX_DATA_LENGTH)
    {
        this.MAX_DATA_LENGTH = MAX_DATA_LENGTH;
    }

    /**
     * Getter for MAX_DATA_LENGTH.
     *
     * @return The value of MAX_DATA_LENGTH.
     */
    public Long getMAX_DATA_LENGTH()
    {
        return MAX_DATA_LENGTH;
    }

    /**
     * Setter for INDEX_LENGTH.
     *
     * @param INDEX_LENGTH  The value to set.
     */
    public void setINDEX_LENGTH(Long INDEX_LENGTH)
    {
        this.INDEX_LENGTH = INDEX_LENGTH;
    }

    /**
     * Getter for INDEX_LENGTH.
     *
     * @return The value of INDEX_LENGTH.
     */
    public Long getINDEX_LENGTH()
    {
        return INDEX_LENGTH;
    }

    /**
     * Setter for DATA_FREE.
     *
     * @param DATA_FREE  The value to set.
     */
    public void setDATA_FREE(Long DATA_FREE)
    {
        this.DATA_FREE = DATA_FREE;
    }

    /**
     * Getter for DATA_FREE.
     *
     * @return The value of DATA_FREE.
     */
    public Long getDATA_FREE()
    {
        return DATA_FREE;
    }

    /**
     * Setter for AUTO_INCREMENT.
     *
     * @param AUTO_INCREMENT  The value to set.
     */
    public void setAUTO_INCREMENT(Long AUTO_INCREMENT)
    {
        this.AUTO_INCREMENT = AUTO_INCREMENT;
    }

    /**
     * Getter for AUTO_INCREMENT.
     *
     * @return The value of AUTO_INCREMENT.
     */
    public Long getAUTO_INCREMENT()
    {
        return AUTO_INCREMENT;
    }

    /**
     * Setter for CREATE_TIME.
     *
     * @param CREATE_TIME  The value to set.
     */
    public void setCREATE_TIME(Timestamp CREATE_TIME)
    {
        this.CREATE_TIME = CREATE_TIME;
    }

    /**
     * Getter for CREATE_TIME.
     *
     * @return The value of CREATE_TIME.
     */
    public Timestamp getCREATE_TIME()
    {
        return CREATE_TIME;
    }

    /**
     * Setter for UPDATE_TIME.
     *
     * @param UPDATE_TIME  The value to set.
     */
    public void setUPDATE_TIME(Timestamp UPDATE_TIME)
    {
        this.UPDATE_TIME = UPDATE_TIME;
    }

    /**
     * Getter for UPDATE_TIME.
     *
     * @return The value of UPDATE_TIME.
     */
    public Timestamp getUPDATE_TIME()
    {
        return UPDATE_TIME;
    }

    /**
     * Setter for CHECK_TIME.
     *
     * @param CHECK_TIME  The value to set.
     */
    public void setCHECK_TIME(Timestamp CHECK_TIME)
    {
        this.CHECK_TIME = CHECK_TIME;
    }

    /**
     * Getter for CHECK_TIME.
     *
     * @return The value of CHECK_TIME.
     */
    public Timestamp getCHECK_TIME()
    {
        return CHECK_TIME;
    }

    /**
     * Setter for TABLE_COLLATION.
     *
     * @param TABLE_COLLATION  The value to set.
     */
    public void setTABLE_COLLATION(String TABLE_COLLATION)
    {
        this.TABLE_COLLATION = TABLE_COLLATION;
    }

    /**
     * Getter for TABLE_COLLATION.
     *
     * @return The value of TABLE_COLLATION.
     */
    public String getTABLE_COLLATION()
    {
        return TABLE_COLLATION;
    }

    /**
     * Setter for CHECKSUM.
     *
     * @param CHECKSUM  The value to set.
     */
    public void setCHECKSUM(Long CHECKSUM)
    {
        this.CHECKSUM = CHECKSUM;
    }

    /**
     * Getter for CHECKSUM.
     *
     * @return The value of CHECKSUM.
     */
    public Long getCHECKSUM()
    {
        return CHECKSUM;
    }

    /**
     * Setter for CREATE_OPTIONS.
     *
     * @param CREATE_OPTIONS  The value to set.
     */
    public void setCREATE_OPTIONS(String CREATE_OPTIONS)
    {
        this.CREATE_OPTIONS = CREATE_OPTIONS;
    }

    /**
     * Getter for CREATE_OPTIONS.
     *
     * @return The value of CREATE_OPTIONS.
     */
    public String getCREATE_OPTIONS()
    {
        return CREATE_OPTIONS;
    }

    /**
     * Setter for TABLE_COMMENT.
     *
     * @param TABLE_COMMENT  The value to set.
     */
    public void setTABLE_COMMENT(String TABLE_COMMENT)
    {
        this.TABLE_COMMENT = TABLE_COMMENT;
    }

    /**
     * Getter for TABLE_COMMENT.
     *
     * @return The value of TABLE_COMMENT.
     */
    public String getTABLE_COMMENT()
    {
        return TABLE_COMMENT;
    }

}
