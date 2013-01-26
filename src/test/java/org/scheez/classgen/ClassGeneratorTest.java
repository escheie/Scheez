package org.scheez.classgen;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.scheez.schema.objects.TableName;
import org.scheez.test.db.TestDatabase;
import org.scheez.test.db.TestDatabaseManager;
import org.springframework.jdbc.core.JdbcTemplate;

@RunWith(Parameterized.class)
public class ClassGeneratorTest
{
    private TestDatabase testDatabase;

    public ClassGeneratorTest(TestDatabase testDatabase)
    {
        this.testDatabase = testDatabase;
    }

    @Test
    public void test()
    {
        File srcDir = new File("build/generated/java");
        srcDir.mkdirs();
        
        final ClassGenerator codeGenerator = new ClassGenerator(srcDir, new DefaultClassTemplate());
        JdbcTemplate template = new JdbcTemplate(testDatabase.getDataSource());
        for (TableName tableName : testDatabase.getSystemTableNames())
        {
            String clsName = "org.scheez.test." + testDatabase.getName() + "." + tableName;
            File clsFile = template.query("SELECT * FROM " + tableName, codeGenerator.generateClass(clsName));
            assertNotNull(clsFile);
        }
    }
    
    @Parameters (name="{0}")
    public static Collection<Object[]> profiles ()
    {
        return TestDatabaseManager.getInstance().getDatabaseParameters();
    }
}
