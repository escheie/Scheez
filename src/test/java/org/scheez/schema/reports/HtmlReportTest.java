package org.scheez.schema.reports;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.dao.SchemaDaoFactory;
import org.scheez.schema.diff.SchemaDifference;
import org.scheez.schema.manger.BasicSchemaManager;
import org.scheez.schema.manger.SchemaClasses;
import org.scheez.test.TestDatabase;
import org.scheez.test.jpa.Department;
import org.scheez.test.jpa.Employee;
import org.scheez.test.jpa.Job;
import org.scheez.test.junit.ScheezTestDatabase;

@RunWith (ScheezTestDatabase.class)
public class HtmlReportTest
{
    private TestDatabase testDatabase;

    public HtmlReportTest(TestDatabase testDatabase)
    {
        super();
        this.testDatabase = testDatabase;
    }

    @Test
    public void testExport() throws IOException
    {   
        SchemaDao schemaDao = SchemaDaoFactory.getSchemaDao(testDatabase.getDataSource());
        
        SchemaClasses schemaClasses = new SchemaClasses();
        schemaClasses.include(Employee.class);
        schemaClasses.include(Department.class);
        schemaClasses.include(Job.class);
        
        BasicSchemaManager basicSchemaManager = new BasicSchemaManager("ReportTest", schemaDao, schemaClasses);
        
        List<SchemaDifference> differences = basicSchemaManager.findDifferences();
        
        HtmlReport.generate (schemaDao, differences, new File(
                "build/reports/ddl/" + testDatabase.getName() + ".html"));
        
        basicSchemaManager.resolveDifferences(differences);
    }

}
