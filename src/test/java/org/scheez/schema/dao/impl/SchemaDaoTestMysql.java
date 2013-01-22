package org.scheez.schema.dao.impl;

import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.dao.SchemaDaoTest;
import org.scheez.schema.def.ColumnType;
import org.scheez.test.util.DataSourceUtil;

public class SchemaDaoTestMysql extends SchemaDaoTest
{

    @Override
    protected SchemaDao initSchemaDao()
    {
        return new SchemaDaoMysql (DataSourceUtil.getMysqlDataSource()); 
    }
    
    @Override
    protected ColumnType getExpectedColumnType(ColumnType columnType)
    {
        return columnType;
    }
}
