package org.scheez.test;

public interface TestDatabaseFactory 
{    
    TestDatabase getTestDatabase (String name, TestDatabaseProperties properties);
}
