package org.scheez.schema.dao.impl;

import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.dao.SchemaDaoTest;
import org.scheez.schema.def.ColumnType;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class SchemaDaoTestMysql extends SchemaDaoTest
{

    @Override
    protected SchemaDao initSchemaDao()
    {
        DriverManagerDataSource dataSource = new DriverManagerDataSource("jdbc:mysql://localhost/scheez", "scheez", "scheez");
        return new SchemaDaoMysql (dataSource); 
    }
    
    @Override
    protected ColumnType getExpectedColumnType(ColumnType columnType)
    {
        return columnType;
    }
}
