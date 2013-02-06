package org.scheez.schema.manger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.scheez.reflect.PersistentField;
import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.diff.MismatchedColumnLength;
import org.scheez.schema.diff.MismatchedColumnPrecision;
import org.scheez.schema.diff.MismatchedColumnType;
import org.scheez.schema.diff.MissingColumn;
import org.scheez.schema.diff.MissingTable;
import org.scheez.schema.diff.SchemaDifference;
import org.scheez.schema.diff.UnknownColumn;
import org.scheez.schema.diff.UnknownTable;
import org.scheez.schema.mapper.DefaultSchemaMapper;
import org.scheez.schema.mapper.SchemaMapper;
import org.scheez.schema.parts.Column;
import org.scheez.schema.parts.Table;
import org.scheez.schema.parts.TableName;
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
    public void resolveDifferences(List<SchemaDifference> differences)
    {
        for (SchemaDifference difference : differences)
        {
            difference.resolveDifference(schemaDao);
        }
    }

    @Override
    public List<SchemaDifference> findDifferences()
    {
        List<SchemaDifference> diff = new LinkedList<SchemaDifference>();

        List<Table> tables = schemaDao.getTables(schemaName);

        Map<String, Table> map = new HashMap<String, Table>();
        for (Table table : tables)
        {
            map.put(table.getName().toLowerCase(), table);
        }

        for (Class<?> cls : classes)
        {
            String name = schemaMapper.mapClassToTableName(cls);
            Table table = map.remove(name.toLowerCase());
            if (table == null)
            {
                table = schemaMapper.mapClassToTable(new TableName(schemaName, name), cls);
                diff.add(new MissingTable(table, cls));
            }
            else
            {
                diffTable(table, cls, diff);
            }
        }

        for (Table table : map.values())
        {
            diff.add(new UnknownTable(table));
        }

        return diff;
    }

    private void diffTable(Table table, Class<?> cls, List<SchemaDifference> diff)
    {
        diffColumns(table, cls, diff);
    }

    private void diffColumns(Table table, Class<?> cls, List<SchemaDifference> diff)
    {
        Map<String, Column> map = new HashMap<String, Column>();
        for (Column column : table.getColumns())
        {
            map.put(column.getName().toLowerCase(), column);
        }

        for (PersistentField field : schemaMapper.getPersistentFields(cls))
        {
            Column expectedColumn = schemaMapper.mapFieldToColumn(field);

            Column existingColumn = map.remove(expectedColumn.getName().toLowerCase());
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
            diff.add(new UnknownColumn(table, column, cls));
        }
    }

    private void diffColumn(Table table, Column existingColumn, Column expectedColumn, PersistentField field,
            List<SchemaDifference> diff)
    {
        if (existingColumn.getType() != expectedColumn.getType())
        {
            diff.add(new MismatchedColumnType(table, existingColumn, expectedColumn, field));
        }
        else if ((existingColumn.getType().isLengthSupported()) && (expectedColumn.getLength() != null)
                && (!expectedColumn.getLength().equals(existingColumn.getLength())))
        {
            diff.add(new MismatchedColumnLength(table, existingColumn, expectedColumn, field));
        }
        else if ((existingColumn.getType().isPrecisionSupported()) && (((expectedColumn.getPrecision() != null)
                && (!expectedColumn.getPrecision().equals(existingColumn.getPrecision()))) || ((expectedColumn.getScale() != null)
                        && (!expectedColumn.getScale().equals(existingColumn.getScale())))))
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
