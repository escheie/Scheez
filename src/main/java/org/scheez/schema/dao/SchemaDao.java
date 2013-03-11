/**
 *  Copyright (c) 2013, Eric Scheie
 *
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met: 
 *
 *  1. Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer. 
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution. 
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.scheez.schema.dao;

import java.util.List;

import javax.sql.DataSource;

import org.scheez.schema.def.ColumnType;
import org.scheez.schema.model.Column;
import org.scheez.schema.model.Index;
import org.scheez.schema.model.Sequence;
import org.scheez.schema.model.SequenceName;
import org.scheez.schema.model.Table;
import org.scheez.schema.model.TableName;
import org.springframework.dao.DataAccessException;

/**
 * <p>
 * A database independent interface for accessing and manipulating Database
 * Schemas.
 * </p>
 * <p>
 * Any errors that occur as a result of calling these interface methods should
 * result in a Spring {@link DataAccessException}.
 * </p>
 * <p>
 * Implementations of this interface should be thread safe.
 * </p>
 * <p>
 * To get an implementation of this interface for a specific database, use the
 * {@link SchemaDaoFactory} class.
 * </p>
 * 
 * @author Eric Scheie
 */
public interface SchemaDao
{
    /**
     * <p>
     * Creates a new database schema with the specified name. As not all
     * databases semantically support schemas, this method creates whatever the
     * equivalent is (e.g. MySQL = DATABASE, Oracle = USER).
     * </p>
     * 
     * @param schemaName
     *            the non-null name of the Schema to create.
     * @throws DataAccessException
     *             if any errors occur.
     */
    void createSchema(String schemaName);

    /**
     * <p>
     * Drops a database schema with the specified name. As not all databases
     * semantically support schemas, this method drops whatever the equivalent
     * is (e.g. MySQL = DATABASE, Oracle = USER).
     * </p>
     * 
     * @param schemaName
     *            the non-null name of the Schema to drop.
     * @throws DataAccessException
     *             if any errors occur.
     */
    void dropSchema(String schemaName);

    /**
     * <p>
     * Checks if a database schema with the specified name exists. As not all
     * databases semantically support schemas, this method checks for the
     * existence of whatever the equivalent is (e.g. MySQL = DATABASE, Oracle =
     * USER).
     * </p>
     * 
     * @param schemaName
     *            the non-null name of the Schema to check.
     * @return true if the schema exists, else false.
     * @throws DataAccessException
     *             if any errors occur.
     */
    boolean schemaExists(String schemaName);

    /**
     * <p>
     * Gets the list of schema's on the database. As not all databases
     * semantically support schemas, this method gets a list of whatever the
     * equivalent is (e.g. MySQL = DATABASE, Oracle = USER).
     * </p>
     * 
     * @return a non-null list of schema names.
     * @throws DataAccessException
     *             if any errors occur.
     */
    List<String> getSchemas();

    /**
     * Creates the specified table.
     * 
     * @param table
     *            the table to create.
     * @throw DataAccessException if an error occurs while creating the table.
     */
    void createTable(Table table);

    /**
     * Renames the specified table.
     * 
     * @param oldName
     *            the existing table name.
     * @param newName
     *            the desired table name.
     * @throw DataAccessException if an error occurs while renaming the table.
     */
    void renameTable(TableName oldName, TableName newName);

    /**
     * Drops the specified table.
     * 
     * @param tableName
     *            the name of the table to drop.
     * @throw DataAccessException if an error occurs while dropping the table.
     */
    void dropTable(TableName tableName);

    /**
     * Gets the table with the specified name.
     * 
     * @param tableName
     *            the name of the table to get.
     * @return the table with the specified name, else null if the table does
     *         not exist.
     */
    Table getTable(TableName tableName);

    /**
     * Gets a list of tables in the specified schema.
     * 
     * @param schemaName
     *            the schema name.
     * @return a non-null list of tables in the specified schema.
     */
    List<Table> getTables(String schemaName);

    /**
     * Adds a column to the specified table.
     * 
     * @param tableName
     *            the table to add the column to.
     * @param column
     *            the column to add.
     * @throw DataAccessException if an error occurs while adding the column.
     */
    void addColumn(TableName tableName, Column column);

    /**
     * Renames a column.
     * 
     * @param tableName
     *            the name of the table that contains the column to be renamed.
     * @param oldName
     *            the existing name of the column.
     * @param newName
     *            the desired name of the column.
     * @throw DataAccessException if an error occurs while renaming the column.
     */
    void renameColumn(TableName tableName, String oldName, String newName);

    /**
     * Drops a column.
     * 
     * @param tableName
     *            the name of the table that contains the column to be dropped.
     * @param columnName
     *            the name of the column to drop.
     * @throw DataAccessException if an error occurs while dropping the column.
     */
    void dropColumn(TableName tableName, String columnName);

    /**
     * Gets the column in the specified table with the specified name.
     * 
     * @param tableName
     *            the name of the table the column belongs to.
     * @param columnName
     *            the name of the column.
     * @return the column if it exists, else null.
     */
    Column getColumn(TableName tableName, String columnName);

    /**
     * Alters the specified column.
     * 
     * @param tableName
     *            the name of the table the column belongs to.
     * @param column
     *            the column to alter.
     * @throw DataAccessException if an error occurs while altering the column.
     */
    void alterColumn(TableName tableName, Column column);

    /**
     * Gets the type returned by the database if the specified type is used as a
     * column type.
     * 
     * @param columnType
     *            the columnType.
     * @return the columnType that is actually used by the database.
     */
    ColumnType getExpectedColumnType(ColumnType columnType);

    /**
     * Adds an index to a table.
     * 
     * @param tableName
     *            the table name.
     * @param index
     *            the index to add.
     * @throw DataAccessException if an error occurs while adding the index.
     */
    void addIndex(TableName tableName, Index index);

    /**
     * Drops an index from a table.
     * 
     * @param tableName
     *            the table name.
     * @param indexName
     *            the name of the index to drop.
     * @throw DataAccessException if an error occurs while dropping the index.
     */
    void dropIndex(TableName tableName, String indexName);

    /**
     * Gets the index with the specified name on the specified table.
     * 
     * @param tableName
     *            the name of the table.
     * @param indexName
     *            the name of the index to get.
     * @return the index if found, else null.
     */
    Index getIndex(TableName tableName, String indexName);

    /**
     * Gets the sequence with the specified name.
     * 
     * @param sequenceName
     *            the sequence name
     * @return the sequence with the specified name, ese null if the sequence
     *         does not exit.
     */
    Sequence getSequence(SequenceName sequenceName);

    /**
     * Creates a sequence.
     * 
     * @param sequence
     *            the sequence to create.
     * @throw DataAccessException if an error occurs while creating the
     *        sequence.
     */
    void createSequence(Sequence sequence);

    /**
     * Drops a sequence.
     * 
     * @param sequenceName
     *            the name of the sequence to drop.
     * @throw DataAccessException if an error occurs while dropping the
     *        sequence.
     */
    void dropSequence(SequenceName sequenceName);

    /**
     * Sets the SchemaDdlExecutor to use when executing DDL statements.
     * 
     * @param executor
     *            the SchemaDdlExecutor to use to execute DDL statements.
     */
    void setSchemaDdlExecutor(SchemaDdlExecutor executor);

    /**
     * @return the SchemaDdlExecutor to use when executing DDL statements.
     *         Returns null if no SchemaDdlExecutor has been set.
     */
    SchemaDdlExecutor getSchemaDdlExecutor();

    /**
     * @return the DataSource in use by this DAO.
     */
    DataSource getDataSource();
}
