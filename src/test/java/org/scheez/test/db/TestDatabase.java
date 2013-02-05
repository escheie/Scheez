package org.scheez.test.db;

import java.util.List;

import javax.sql.DataSource;

import org.scheez.schema.def.ColumnType;
import org.scheez.schema.parts.TableName;

public interface TestDatabase
{
    String getName ();
    
    String getUrl ();
    
    DataSource getDataSource();
    
    ColumnType getExpectedColumnType(ColumnType columnType);
    
    List<TableName> getSystemTableNames ();
}
