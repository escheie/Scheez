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
    
    void renameTable (TableName oldName, TableName newName);

    void dropTable(TableName tableName);

    Table getTable(TableName tableName);

    List<Table> getTables(String schemaName);

    void addColumn(TableName tableName, Column column);
    
    void renameColumn (TableName tableName, String oldName, String newName);

    void dropColumn(TableName tableName, String columnName);

    Column getColumn(TableName tableName, String columnName);

    void alterColumn (TableName tableName, Column column);

    ColumnType getExpectedColumnType(ColumnType columnType);

    void addIndex(TableName tableName, Index index);

    void dropIndex(TableName tableName, String indexName);

    Index getIndex(TableName tableName, String indexName);
    
    Sequence getSequence (SequenceName sequenceName);
    
    void createSequence (Sequence sequence);
    
    void dropSequence (SequenceName sequenceName);
    
    void setSchemaDdlExecutor (SchemaDdlExecutor executor);
    
    SchemaDdlExecutor getSchemaDdlExecutor();
    
    DataSource getDataSource();
}
