package org.scheez.classgen;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Collection;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

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
    public void testClassGenerator()
    {
        File srcDir = new File("build/tmp/ClassGenerator");
        srcDir.mkdirs();
        
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        
        final ClassGenerator codeGenerator = new ClassGenerator(srcDir, new DefaultClassTemplate());
        JdbcTemplate template = new JdbcTemplate(testDatabase.getDataSource());
        for (TableName tableName : testDatabase.getSystemTableNames())
        {
            String clsName = "org.scheez.test." + testDatabase.getName() + "." + tableName;
            File clsFile = template.query("SELECT * FROM " + tableName, codeGenerator.generateClass(clsName));
            assertNotNull(clsFile);
            
            CompilationTask task = compiler.getTask(null, fileManager, null, null, null, fileManager.getJavaFileObjects(clsFile));
            assertTrue(task.call());
        }
    }
    
    @Parameters (name="{0}")
    public static Collection<Object[]> testDatabases ()
    {
        return TestDatabaseManager.getInstance().getDatabaseParameters();
    }
}
