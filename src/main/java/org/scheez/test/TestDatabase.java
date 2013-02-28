package org.scheez.test;

import javax.sql.DataSource;

public interface TestDatabase
{
    String getName ();
    
    DataSource getDataSource();
    
    void initialize (String name, TestDatabaseProperties properties);
    
}
