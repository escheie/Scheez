package org.scheez.classgen;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.scheez.schema.classgen.ClassGenerator;
import org.scheez.schema.classgen.DefaultClassTemplate;
import org.scheez.schema.classgen.GeneratedClass;
import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.dao.SchemaDaoFactory;
import org.scheez.schema.mapper.DefaultNameMapper;
import org.scheez.schema.mapper.NameMapper;
import org.scheez.schema.mapper.ObjectMapper;
import org.scheez.schema.model.Table;
import org.scheez.test.TestDatabase;
import org.scheez.test.jpa.EnterprisePersistenceUnit;
import org.scheez.test.junit.ScheezTestDatabase;
import org.scheez.util.BaseObject;
import org.springframework.jdbc.core.JdbcTemplate;

@RunWith(ScheezTestDatabase.class)
public class ClassGeneratorTest
{
    private TestDatabase testDatabase;
    
    private SchemaDao schemaDao;

    public ClassGeneratorTest (TestDatabase testDatabase)
    {
        this.testDatabase = testDatabase;
        EnterprisePersistenceUnit.getInstance().getEntityManagerFactory(testDatabase);
        schemaDao = SchemaDaoFactory.getSchemaDao(testDatabase.getDataSource());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testClassGenerator() throws Exception
    { 
        File srcDir = new File("build/generated/java");
        srcDir.mkdirs();
        File outputDir = new File("build/classes/generated");
        outputDir.mkdirs();

        NameMapper nameMapper = new DefaultNameMapper();

        final ClassGenerator codeGenerator = new ClassGenerator(srcDir, new DefaultClassTemplate());
        JdbcTemplate template = new JdbcTemplate(testDatabase.getDataSource());
        for (Table table : schemaDao.getTables(EnterprisePersistenceUnit.SCHEMA))
        {
            String sql = "SELECT * FROM " + table.getTableName();
            String pkgName = "org.scheez.classgen.test." + testDatabase.getName() + "."
                    + nameMapper.mapDatabaseNameToJavaName(table.getSchemaName()).toLowerCase();
            GeneratedClass generatedClass = template.query(sql, codeGenerator.generateClass(pkgName, table.getTableName()));
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
