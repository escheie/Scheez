package org.scheez.schema.dao.impl;

import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.dao.SchemaDaoTest;
import org.scheez.schema.def.ColumnType;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class SchemaDaoTestHsqldb extends SchemaDaoTest
{

    @Override
    protected SchemaDao initSchemaDao()
    {
        DriverManagerDataSource dataSource = new DriverManagerDataSource("jdbc:hsqldb:mem:testdb", "SA", "");
        return new SchemaDaoHsqldb(dataSource); 
    }
    
    @Override
    protected ColumnType getExpectedColumnType(ColumnType columnType)
    {
        if (columnType == ColumnType.FLOAT)
        {
            columnType = ColumnType.DOUBLE;
        }
        return columnType;
    } 
}
