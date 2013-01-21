package org.scheez.schema.def;

public enum ColumnMetaDataKey
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
     * column name
     */
    COLUMN_NAME,
    /**
     * SQL type from java.sql.Types
     */
    DATA_TYPE,
    /**
     * Data source dependent type name, for a UDT the type name is fully
     * qualified
     */
    TYPE_NAME,
    /**
     * column size.
     */
    COLUMN_SIZE,
    /**
     * the number of fractional digits. Null is returned for data types where
     * DECIMAL_DIGITS is not applicable.
     */
    DECIMAL_DIGITS,
    /**
     * Radix (typically either 10 or 2)
     */
    NUM_PREC_RADIX,
    /**
     * is NULL allowed. columnNoNulls - might not allow NULL values
     * columnNullable - definitely allows NULL values columnNullableUnknown -
     * nullability unknown
     */
    NULLABLE,
    /**
     * comment describing column (may be null)
     */
    REMARKS,
    /**
     * default value for the column, which should be interpreted as a string
     * when the value is enclosed in single quotes (may be null)
     */
    COLUMN_DEF,
    /**
     * for char types the maximum number of bytes in the column
     */
    CHAR_OCTET_LENGTH,
    /**
     * index of column in table (starting at 1)
     */
    ORDINAL_POSITION,
    /**
     * ISO rules are used to determine the nullability for a column. 
     * YES --- if the parameter can include NULLs 
     * NO --- if the parameter cannot include NULLs 
     * empty string --- if the nullability for the parameter is unknown
     */
    IS_NULLABLE,
    /**
     * catalog of table that is the scope of a reference attribute (null if DATA_TYPE isn't REF)
     */
    SCOPE_CATALOG,
    /**
     * schema of table that is the scope of a reference attribute (null if the DATA_TYPE isn't REF)
     */
    SCOPE_SCHEMA,
    /**
     * table name that this the scope of a reference attribure (null if the DATA_TYPE isn't REF)
     */
    SCOPE_TABLE,
    /**
     * source type of a distinct type or user-generated Ref type, SQL type from java.sql.Types (null if DATA_TYPE isn't DISTINCT or user-generated REF)
     */
    SOURCE_DATA_TYPE,
    /**
     * Indicates whether this column is auto incremented
     * YES --- if the column is auto incremented
     * NO --- if the column is not auto incremented
     * empty string --- if it cannot be determined whether the column is auto incremented parameter is unknown
     */
    IS_AUTOINCREMENT;
}
