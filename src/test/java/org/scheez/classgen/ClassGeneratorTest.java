package org.scheez.classgen;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.scheez.schema.classgen.ClassGenerator;
import org.scheez.schema.classgen.DefaultClassTemplate;
import org.scheez.schema.classgen.GeneratedClass;
import org.scheez.schema.mapper.DefaultNameMapper;
import org.scheez.schema.mapper.NameMapper;
import org.scheez.schema.mapper.ObjectMapper;
import org.scheez.schema.parts.TableName;
import org.scheez.test.DatabaseIntegrationTest;
import org.scheez.test.SimpleTestDatabase;
import org.scheez.util.BaseObject;
import org.springframework.jdbc.core.JdbcTemplate;

public class ClassGeneratorTest extends DatabaseIntegrationTest
{
    private SimpleTestDatabase testDatabase;

    public ClassGeneratorTest(SimpleTestDatabase testDatabase)
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

        NameMapper nameMapper = new DefaultNameMapper();

        final ClassGenerator codeGenerator = new ClassGenerator(srcDir, new DefaultClassTemplate());
        JdbcTemplate template = new JdbcTemplate(testDatabase.getDataSource());
        for (TableName tableName : testDatabase.getSystemTableNames())
        {
            String sql = "SELECT * FROM " + tableName;
            String pkgName = "org.scheez.test." + testDatabase.getName() + "."
                    + nameMapper.mapDatabaseNameToJavaName(tableName.getSchemaName()).toLowerCase();
            GeneratedClass generatedClass = template.query(sql, codeGenerator.generateClass(pkgName, tableName));
            assertNotNull(generatedClass);

            Class<?> cls = generatedClass.compile(outputDir);
            assertNotNull(cls);

            List<? extends BaseObject> list = template.query(sql, new ObjectMapper(cls));
            assertNotNull(list);

            for (BaseObject obj : list)
            {
                assertTrue(obj.hasValues());
            }
        }
    } 
}
