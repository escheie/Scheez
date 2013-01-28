package org.scheez.classgen;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.scheez.map.RowMapper;
import org.scheez.schema.objects.TableName;
import org.scheez.test.db.TestDatabase;
import org.scheez.test.db.TestDatabaseManager;
import org.scheez.util.BaseObject;
import org.springframework.jdbc.core.JdbcTemplate;

@RunWith(Parameterized.class)
public class ClassGeneratorTest
{
    private TestDatabase testDatabase;

    public ClassGeneratorTest(TestDatabase testDatabase)
    {
        this.testDatabase = testDatabase;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testClassGenerator() throws Exception
    {
        File srcDir = new File("build/tmp/ClassGenerator");
        srcDir.mkdirs();
        
        URLClassLoader cl = new URLClassLoader(new URL[] { srcDir.toURI().toURL() });
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        
        final ClassGenerator codeGenerator = new ClassGenerator(srcDir, new DefaultClassTemplate());
        JdbcTemplate template = new JdbcTemplate(testDatabase.getDataSource());
        for (TableName tableName : testDatabase.getSystemTableNames())
        {
            String sql = "SELECT * FROM " + tableName;
            String clsName = "org.scheez.test." + testDatabase.getName() + "." + tableName;
            File clsFile = template.query(sql, codeGenerator.generateClass(clsName));
            assertNotNull(clsFile);
            
            CompilationTask task = compiler.getTask(null, fileManager, null, null, null, fileManager.getJavaFileObjects(clsFile));
            assertTrue(task.call());
           
            Class<?> cls = cl.loadClass(clsName);
            assertNotNull(cls);

            List<? extends BaseObject> list = template.query(sql, new RowMapper(cls));
            assertNotNull(list);
            
            for (BaseObject obj : list)
            {
                assertFalse(obj.allNulls());
            }
        }
    }
    
    @Parameters (name="{0}")
    public static Collection<Object[]> testDatabases ()
    {
        return TestDatabaseManager.getInstance().getDatabaseParameters();
    }
}
