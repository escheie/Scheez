package org.scheez.schema.classgen;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.scheez.schema.classgen.ClassGenerator;
import org.scheez.schema.classgen.DefaultClassTemplate;
import org.scheez.schema.classgen.GeneratedClass;
import org.scheez.schema.classgen.JpaClassTemplate;
import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.dao.SchemaDaoFactory;
import org.scheez.schema.mapper.ObjectMapper;
import org.scheez.schema.model.Table;
import org.scheez.test.TestDatabase;
import org.scheez.test.jpa.EnterpriseSchema;
import org.scheez.test.junit.ScheezTestDatabase;
import org.scheez.util.BaseObject;
import org.springframework.jdbc.core.JdbcTemplate;

@RunWith(ScheezTestDatabase.class)
public class ClassGeneratorTest
{
    private TestDatabase testDatabase;
    
    private SchemaDao schemaDao;
    
    private EnterpriseSchema schema;

    public ClassGeneratorTest (TestDatabase testDatabase)
    {
        this.testDatabase = testDatabase;
        schema = EnterpriseSchema.getInstance();
        schema.init(testDatabase);
        schemaDao = SchemaDaoFactory.getSchemaDao(testDatabase.getDataSource());
    }
    
    @Test
    public void testClassGeneratorFromTableDefinition() throws Exception
    { 
        File srcDir = new File("build/generated/java");
        srcDir.mkdirs();
        File outputDir = new File("build/classes/generated");
        outputDir.mkdirs();

        final ClassGenerator codeGenerator = new ClassGenerator(srcDir, new JpaClassTemplate());
        
        JdbcTemplate template = new JdbcTemplate(testDatabase.getDataSource());
        
        for (Table table : schemaDao.getTables(schema.getSchemaName()))
        {
            String pkgName = "org.scheez.schema.classgen.test." + testDatabase.getName();
            
            GeneratedClass generatedClass = codeGenerator.generateClass(pkgName, table);
            assertNotNull(generatedClass);

            Class<?> cls = generatedClass.compile(outputDir);
            assertNotNull(cls);

            @SuppressWarnings({ "unchecked", "rawtypes" })
            List<? extends BaseObject> list = template.query("SELECT * FROM " + table.getTableName(), new ObjectMapper(cls));
            assertNotNull(list);

            for (BaseObject obj : list)
            {
                assertTrue(obj.hasValues());
            }
        }
    } 

    @Test
    public void testGeneratorClassFromQueryResults() throws Exception
    { 
        File srcDir = new File("build/generated/java");
        srcDir.mkdirs();
        File outputDir = new File("build/classes/generated");
        outputDir.mkdirs();

        final ClassGenerator codeGenerator = new ClassGenerator(srcDir, new DefaultClassTemplate());
        JdbcTemplate template = new JdbcTemplate(testDatabase.getDataSource());
        
        String query = "SELECT e1.*, d1.name as department_name FROM enterprise.employee e1 " +
        		"INNER JOIN enterprise.department d1 ON e1.department_id = d1.id where d1.name = 'Bridge'";
            
        String clsName = "org.scheez.schema.classgen.test." + testDatabase.getName() + ".EnterpriseBridgeCrew";
                    
        GeneratedClass generatedClass = template.query(query, codeGenerator.generateClass(clsName));
        assertNotNull(generatedClass);

        Class<?> cls = generatedClass.compile(outputDir);
        assertNotNull(cls);

        @SuppressWarnings({ "unchecked", "rawtypes" })
        List<? extends BaseObject> list = template.query(query, new ObjectMapper(cls));
        assertNotNull(list);
        
        assertEquals(EnterpriseSchema.BRIDGE_CREW_COUNT, list.size());
        for (BaseObject obj : list)
        {
            assertTrue(obj.hasValues());
        }
    } 
    
//    @Test
//    public void testClassGenerator() throws Exception
//    { 
//        String query = "SELECT e1.*, d1.name as department_name FROM enterprise.employee e1 " +
//                "INNER JOIN enterprise.department d1 ON e1.department_id = d1.id where d1.name = 'Bridge'";
//                    
//        JdbcTemplate template = new JdbcTemplate(testDatabase.getDataSource());
//        List<EnterpriseBridgeCrew> list = template.query(query, new ObjectMapper<EnterpriseBridgeCrew>(EnterpriseBridgeCrew.class));
//        assertNotNull(list);
//        
//        assertEquals(EnterpriseSchema.getInstance().getBridgeCrewCount(), list.size());
//        for (BaseObject obj : list)
//        {
//            assertTrue(obj.hasValues());
//            System.out.println(obj);
//        }
//    } 
}
