package org.scheez.schema.dao.impl;

import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.dao.SchemaDaoTest;
import org.scheez.schema.def.ColumnType;
import org.scheez.test.util.DataSourceUtil;

public class SchemaDaoTestHsqldb extends SchemaDaoTest
{

    @Override
    protected SchemaDao initSchemaDao()
    {
        return new SchemaDaoHsqldb(DataSourceUtil.getHsqldbDataSource()); 
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
