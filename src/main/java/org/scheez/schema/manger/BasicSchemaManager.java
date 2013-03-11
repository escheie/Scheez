
package org.scheez.schema.manger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.scheez.reflect.PersistentClass;
import org.scheez.reflect.PersistentField;
import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.diff.MismatchedColumnLength;
import org.scheez.schema.diff.MismatchedColumnPrecision;
import org.scheez.schema.diff.MismatchedColumnType;
import org.scheez.schema.diff.MissingColumn;
import org.scheez.schema.diff.MissingIndex;
import org.scheez.schema.diff.MissingSchema;
import org.scheez.schema.diff.MissingTable;
import org.scheez.schema.diff.RenamedColumn;
import org.scheez.schema.diff.RenamedTable;
import org.scheez.schema.diff.SchemaDifference;
import org.scheez.schema.diff.UnknownColumn;
import org.scheez.schema.diff.UnknownTable;
import org.scheez.schema.mapper.DefaultSchemaMapper;
import org.scheez.schema.mapper.SchemaMapper;
import org.scheez.schema.model.Column;
import org.scheez.schema.model.Index;
import org.scheez.schema.model.Table;
import org.scheez.schema.model.TableName;
import org.scheez.util.DbC;

public class BasicSchemaManager implements SchemaManager
{
    private String schemaName;

    private SchemaDao schemaDao;

    private Iterable<Class<?>> classes;

    private SchemaMapper schemaMapper;

    public BasicSchemaManager(String schemaName, SchemaDao schemaDao, Iterable<Class<?>> classes)
    {
        this.schemaName = schemaName;
        this.schemaDao = schemaDao;
        this.classes = classes;
        this.schemaMapper = new DefaultSchemaMapper();
    }

    @Override
    public void reconcileDifferences(List<SchemaDifference> differences)
    {
        for (SchemaDifference difference : differences)
        {
            difference.reconcileDifferences(schemaDao);
        }
    }

    @Override
    public List<SchemaDifference> findDifferences()
    {
        List<SchemaDifference> diff = new LinkedList<SchemaDifference>();

        if (!schemaDao.schemaExists(schemaName))
        {
            diff.add(new MissingSchema(schemaName));
        }

        List<Table> tables = schemaDao.getTables(schemaName);

        Map<String, Table> map = new HashMap<String, Table>();
        for (Table table : tables)
        {
            map.put(table.getName().toLowerCase(), table);
        }

        for (Class<?> cls : classes)
        {
            PersistentClass persistentClass = new PersistentClass(cls);
            String expectedName = schemaMapper.mapClassToTableName(persistentClass);
            Table table = map.remove(expectedName.toLowerCase());
            if (table == null)
            {
                for (String previousName : persistentClass.getPreviousNames())
                {
                    table = map.remove(previousName.toLowerCase());
                    if (table != null)
                    {
                        break;
                    }
                }
            }
            if (table == null)
            {
                table = schemaMapper.mapClassToTable(new TableName(schemaName, expectedName), persistentClass);
                diff.add(new MissingTable(table, cls));
            }
            else
            {
                diffColumns(table, persistentClass, diff);
                diffIndexes(table, persistentClass, diff);
                if (!table.getName().equalsIgnoreCase(expectedName))
                {
                    diff.add(new RenamedTable(table, cls, expectedName));
                }
            }
        }

        for (Table table : map.values())
        {
            diff.add(new UnknownTable(table));
        }

        return diff;
    }

    /**
     * @param table
     * @param persistentClass
     * @param diff
     */
    private void diffIndexes(Table table, PersistentClass persistentClass,
            List<SchemaDifference> diff)
    {
        Table expectedTable = schemaMapper.mapClassToTable(table.getTableName(), persistentClass);
        
        for (Index index : expectedTable.getIndexes())
        {
            boolean match = false;
            for (Index existing : table.getIndexes())
            {
                if(index.getColumnNames().equals(existing.getColumnNames()))
                {
                    match = true;
                    break;
                }
            }
            if(!match)
            {
                diff.add(new MissingIndex(table, persistentClass.getType(), index));
            }
        }
    }

    private void diffColumns(Table table, PersistentClass cls, List<SchemaDifference> diff)
    {
        Map<String, Column> map = new HashMap<String, Column>();
        for (Column column : table.getColumns())
        {
            map.put(column.getName().toLowerCase(), column);
        }

        for (PersistentField field : cls.getPersistentFields())
        {
            Column expectedColumn = schemaMapper.mapFieldToColumn(null, field);

            Column existingColumn = map.remove(expectedColumn.getName().toLowerCase());
            if (existingColumn == null)
            {
                for (String previousName : field.getPreviousNames())
                {
                    existingColumn = map.remove(previousName.toLowerCase());
                    if (existingColumn != null)
                    {
                        diff.add(new RenamedColumn(table, existingColumn, expectedColumn, field));
                    }
                }
            }
            
            if (existingColumn == null)
            {
                diff.add(new MissingColumn(table, expectedColumn, field));
            }
            else
            {
                diffColumn(table, existingColumn, expectedColumn, field, diff);
            }
        }

        for (Column column : map.values())
        {
            diff.add(new UnknownColumn(table, column, cls.getType()));
        }
    }

    private void diffColumn(Table table, Column existingColumn, Column expectedColumn, PersistentField field,
            List<SchemaDifference> diff)
    {
        if (existingColumn.getType() != schemaDao.getExpectedColumnType(expectedColumn.getType()))
        {
            diff.add(new MismatchedColumnType(table, existingColumn, expectedColumn, field));
        }
        else if ((existingColumn.getType().isLengthSupported()) && (expectedColumn.getLength() != null)
                && (!expectedColumn.getLength().equals(existingColumn.getLength())))
        {
            diff.add(new MismatchedColumnLength(table, existingColumn, expectedColumn, field));
        }
        else if ((existingColumn.getType().isPrecisionSupported())
                && (((expectedColumn.getPrecision() != null) && (!expectedColumn.getPrecision().equals(
                        existingColumn.getPrecision()))) || ((expectedColumn.getScale() != null) && (!expectedColumn
                        .getScale().equals(existingColumn.getScale())))))
        {
            diff.add(new MismatchedColumnPrecision(table, existingColumn, expectedColumn, field));
        }
    }

    public SchemaMapper getSchemaMapper()
    {
        return schemaMapper;
    }

    public void setSchemaMapper(SchemaMapper schemaMapper)
    {
        DbC.throwIfNullArg(schemaMapper);
        this.schemaMapper = schemaMapper;
    }

}
