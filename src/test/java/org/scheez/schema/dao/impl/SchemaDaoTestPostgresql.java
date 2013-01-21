package org.scheez.schema.dao.impl;

import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.dao.SchemaDaoTest;
import org.scheez.schema.def.ColumnType;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class SchemaDaoTestPostgresql extends SchemaDaoTest
{

    @Override
    protected SchemaDao initSchemaDao()
    {
        DriverManagerDataSource dataSource = new DriverManagerDataSource("jdbc:postgresql://localhost/scheez", "postgres", "dbc");
        return new SchemaDaoPostgresql (dataSource); 
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
}
