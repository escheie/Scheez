package org.scheez.schema.manger;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.diff.MissingColumn;
import org.scheez.schema.diff.MissingTable;
import org.scheez.schema.diff.SchemaDifference;
import org.scheez.schema.diff.UnknownColumn;
import org.scheez.schema.diff.UnknownTable;
import org.scheez.schema.mapper.ClassMapper;
import org.scheez.schema.mapper.DefaultClassMapper;
import org.scheez.schema.mapper.FieldMapper;
import org.scheez.schema.objects.Column;
import org.scheez.schema.objects.Table;

public class BasicSchemaManager implements SchemaManager
{
    private String schemaName;
    
    private SchemaDao schemaDao;
    
    private Iterable<Class<?>> classes;
    
    private ClassMapper classMapper;
    
    private FieldMapper fieldMapper;
   
    public BasicSchemaManager (String schemaName, SchemaDao schemaDao, Iterable<Class<?>> classes)
    {
        this.schemaName = schemaName;
        this.schemaDao = schemaDao;
        this.classes = classes;
        this.classMapper = new DefaultClassMapper();
    }
    
    @Override
    public List<SchemaDifference> diff()
    {
        List<SchemaDifference> diff = new LinkedList<SchemaDifference> ();
        
        List<Table> tables = schemaDao.getTables(schemaName);
        
        Map<String, Table> map = new HashMap<String, Table>();
        for(Table table : tables)
        {
            map.put(table.getName().toLowerCase(), table);
        }
        
        for (Class<?> cls : classes )
        {
            String name = classMapper.mapClass(cls);
            Table table = map.remove(name.toLowerCase());
            if(table == null)
            {
                diff.add(new MissingTable(name, cls));
            }
            else
            {
                diffTable (cls, table, diff);
            }
        }
        
        for (Table table : map.values())
        {
            diff.add(new UnknownTable(table));
        }
        
        return diff;
    }

    private void diffTable(Class<?> cls, Table table, List<SchemaDifference> diff)
    {
        diffColumns (cls, table, diff);
    }
    
    private void diffColumns (Class<?> cls, Table table, List<SchemaDifference> diff)
    {
        Map<String, Column> map = new HashMap<String, Column>();
        for(Column column : table.getColumns())
        {
            map.put(column.getName().toLowerCase(), column);
        }
        
        while(cls != null)
        {
            for ( Field field : cls.getDeclaredFields() )
            {
                String name = fieldMapper.mapField(field);
                Column column = map.remove(name.toLowerCase());
                if(column == null)
                {
                    diff.add(new MissingColumn(table, field, name));
                }
                else
                {
                    diffColumn (field, column, diff);
                }
            }
            cls = cls.getSuperclass();
        }
        
        for (Column column : map.values())
        {
            diff.add(new UnknownColumn(table, column));
        }
    }
    
    private void diffColumn (Field field, Column column, List<SchemaDifference> diff)
    {
        
    }
}
