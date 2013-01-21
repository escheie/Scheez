package org.scheez.schema.def;

public enum TableMetaDataKey
{
    /**
     * table catalog (may be null)
     */
    TABLE_CAT,
    /**
     * table schema (may be null)
     */
    TABLE_SCHEM,
    /**
     * table name
     */
    TABLE_NAME,
    /**
     * table type. Typical types are "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM".
     */
    TABLE_TYPE,
    /**
     * explanatory comment on the table
     */
    REMARKS,
    /**
     * the types catalog (may be null)
     */
    TYPE_CAT,
    /**
     *  the types schema (may be null)
     */
    TYPE_SCHEM,
    /**
     *  type name (may be null)
     */
    TYPE_NAME,
    /**
     *  name of the designated "identifier" column of a typed table (may be null)
     */
    SELF_REFERENCING_COL_NAME,
    /**
     * specifies how values in SELF_REFERENCING_COL_NAME are created. Values are "SYSTEM", "USER", "DERIVED". (may be null)
     */
    REF_GENERATION;  
}
