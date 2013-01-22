package org.scheez.schema.dao.impl;

import javax.sql.DataSource;

import org.junit.Test;
import org.scheez.codegen.CodeGenerator;
import org.scheez.codegen.DefaultCodeTemplate;
import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.dao.SchemaDaoTest;
import org.scheez.schema.def.ColumnType;
import org.scheez.test.util.DataSourceUtil;
import org.springframework.jdbc.core.JdbcTemplate;

public class SchemaDaoTestPostgresql extends SchemaDaoTest
{
    
    private DataSource dataSource;

    @Override
    protected SchemaDao initSchemaDao()
    {
        return new SchemaDaoPostgresql (DataSourceUtil.getPostgresqlDataSource()); 
    }
    
    @Override
    protected ColumnType getExpectedColumnType(ColumnType columnType)
    {
        if(columnType == ColumnType.TINYINT)
        {
            columnType = ColumnType.SMALLINT;
        }
        else if (columnType == ColumnType.FLOAT)
        {
            columnType = ColumnType.DOUBLE;
        }
        return columnType;
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testCodeGeneration ()
    {
        JdbcTemplate template = new JdbcTemplate (dataSource);
        CodeGenerator codeGenerator = new CodeGenerator("org.scheez.test.GenTest", null, new DefaultCodeTemplate());
        
        template.query("SELECT * FROM pg_class LIMIT 1", codeGenerator);
        
    }
}
