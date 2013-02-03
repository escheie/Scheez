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
import org.scheez.schema.classgen.ClassGenerator;
import org.scheez.schema.classgen.DefaultClassTemplate;
import org.scheez.schema.classgen.GeneratedClass;
import org.scheez.schema.mapper.NameMapper;
import org.scheez.schema.mapper.NameMapperUnderscoreToCamelCase;
import org.scheez.schema.mapper.RowMapper;
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
        File srcDir = new File("build/tmp/ClassGenerator/src");
        srcDir.mkdirs();
        File outputDir = new File("build/tmp/ClassGenerator/build");
        outputDir.mkdirs();

        NameMapper nameMapper = new NameMapperUnderscoreToCamelCase();

        final ClassGenerator codeGenerator = new ClassGenerator(srcDir, new DefaultClassTemplate());
        JdbcTemplate template = new JdbcTemplate(testDatabase.getDataSource());
        for (TableName tableName : testDatabase.getSystemTableNames())
        {
            String sql = "SELECT * FROM " + tableName;
            String pkgName = "org.scheez.test." + testDatabase.getName() + "."
                    + nameMapper.mapName(tableName.getSchemaName()).toLowerCase();
            GeneratedClass generatedClass = template.query(sql, codeGenerator.generateClass(pkgName, tableName));
            assertNotNull(generatedClass);

            Class<?> cls = generatedClass.compile(outputDir);
            assertNotNull(cls);

            List<? extends BaseObject> list = template.query(sql, new RowMapper(cls));
            assertNotNull(list);

            for (BaseObject obj : list)
            {
                assertTrue(obj.hasValues());
            }
        }
    }

    @Parameters(name = "{0}")
    public static Collection<Object[]> testDatabases()
    {
        return TestDatabaseManager.getInstance().getDatabaseParameters();
    }
}
