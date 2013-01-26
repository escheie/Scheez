package org.scheez.test.db;

import java.util.List;

import javax.sql.DataSource;

import org.scheez.schema.def.ColumnType;
import org.scheez.schema.objects.TableName;

public interface TestDatabase
{
    String getName ();
    
    DataSource getDataSource();
    
    ColumnType getExpectedColumnType(ColumnType columnType);
    
    List<TableName> getSystemTableNames ();
}
