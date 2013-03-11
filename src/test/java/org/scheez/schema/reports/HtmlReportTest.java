package org.scheez.schema.reports;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.dao.SchemaDaoFactory;
import org.scheez.schema.diff.SchemaDifference;
import org.scheez.schema.manger.BasicSchemaManager;
import org.scheez.schema.manger.SchemaClasses;
import org.scheez.test.TestDatabase;
import org.scheez.test.junit.ScheezTestDatabase;
import org.scheez.test.schema.Department;
import org.scheez.test.schema.Employee;
import org.scheez.test.schema.Job;

@RunWith (ScheezTestDatabase.class)
public class HtmlReportTest
{
    private static final String TEST_SCHEMA = "ReportTest";
    
    private TestDatabase testDatabase;
    
    private SchemaDao schemaDao;

    public HtmlReportTest(TestDatabase testDatabase)
    {
        super();
        this.testDatabase = testDatabase;
        
        schemaDao = SchemaDaoFactory.getSchemaDao(testDatabase.getDataSource());
    }
    
    @Before
    public void setUp ()
    {
        if(schemaDao.schemaExists(TEST_SCHEMA))
        {
            schemaDao.dropSchema(TEST_SCHEMA);
            assertFalse(schemaDao.schemaExists(TEST_SCHEMA));
        }
        schemaDao.createSchema(TEST_SCHEMA);
        assertTrue(schemaDao.schemaExists(TEST_SCHEMA));
    }

    @Test
    public void testExport() throws IOException
    {      
        SchemaClasses schemaClasses = new SchemaClasses();
        schemaClasses.include(Employee.class);
        schemaClasses.include(Department.class);
        schemaClasses.include(Job.class);
        
        BasicSchemaManager basicSchemaManager = new BasicSchemaManager(TEST_SCHEMA, schemaDao, schemaClasses);
        
        List<SchemaDifference> differences = basicSchemaManager.findDifferences();
        
        HtmlReport.generate (schemaDao, differences, new File(
                "build/reports/ddl/" + testDatabase.getName() + ".html"));
        
        basicSchemaManager.resolveDifferences(differences);
    }

}
