package org.scheez.test.querydsl;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.dao.SchemaDaoFactory;
import org.scheez.test.ScheezTestConfiguration;
import org.scheez.test.TestDatabase;
import org.scheez.test.junit.ScheezTestDatabase;

import com.mysema.query.jpa.impl.JPAQuery;

@RunWith(ScheezTestDatabase.class)
public class QueryDSLTest 
{
    public static final String JNDI_DATASOURCE = "jndi:jdbc/QueryDSLTest/DataSource";
    
    public static final String DEFAULT_SCHEMA = "querydsl";
    
    private EntityManagerFactory entityManagerFactory;
    
    private TestDatabase testDatabase;
    
    private SchemaDao schemaDao;

    public QueryDSLTest(TestDatabase testDatabase)
    {
        this.testDatabase = testDatabase;
        this.schemaDao = SchemaDaoFactory.getSchemaDao(testDatabase.getDataSource());
    }

    @Before
    public void setUp() throws Exception
    {
        if(schemaDao.schemaExists(DEFAULT_SCHEMA))
        {
            schemaDao.dropSchema(DEFAULT_SCHEMA);
        }
        schemaDao.createSchema(DEFAULT_SCHEMA);
        
        ScheezTestConfiguration.getInstance().resetThreadLocalJndiObjects().put(JNDI_DATASOURCE, testDatabase.getDataSource());
        entityManagerFactory = Persistence.createEntityManagerFactory("org.scheez.test.querydsl");
    }

    @After
    public void tearDown() throws Exception
    {
        if(entityManagerFactory != null)
        {
            entityManagerFactory.close();
        }
    }

    @Test
    public void testQueryDSL ()
    {
        generateTestData();
       
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        QEmployee employee = QEmployee.employee;
       
        JPAQuery query = new JPAQuery(entityManager);
        List<Employee> employees = query.from(employee).where(employee.job.jobTrack.eq(JobTrack.SECURITY)).orderBy(employee.firstName.asc()).list(employee);
        assertEquals(2, employees.size());
        assertEquals("Natasha", employees.get(0).getFirstName());
        assertEquals("Worf", employees.get(1).getFirstName());
        
        assertEquals("Bridge", employees.get(0).getDepartment().getName());
        assertEquals("Picard", employees.get(1).getManager().getLastName());
        
        query = new JPAQuery(entityManager);
        List<Object[]> results = query.from(employee).groupBy(employee.job.jobTrack, employee.department.name).orderBy(
                employee.count().desc(), employee.salary.sum().desc()).list(employee.job.jobTrack,
                employee.department.name, employee.salary.sum(), employee.count());
        assertEquals(5, results.size());
        assertEquals(JobTrack.COMMAND, results.get(0)[0]);
        assertEquals(JobTrack.TECHNICAL, results.get(1)[0]);
        assertEquals(JobTrack.MEDICAL, results.get(2)[0]);
        assertEquals(JobTrack.SECURITY, results.get(3)[0]);
        assertEquals(JobTrack.MEDICAL, results.get(4)[0]);
    }
    
    public void generateTestData ()
    {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        
        String departments[] = {"Engineering", "Bridge", "SickBay", "CargoBay", "Security", "Counseling" };
        
        for (String name : departments)
        {
            Department department = new Department ();
            department.setName(name);
            entityManager.persist(department);
        }
        
        for ( JobTrack track : JobTrack.values() )
        {
            for(int grade = 1; grade <= 10; grade++)
            {
                Job job = new Job ();
                job.setJobTrack(track);
                job.setGrade(grade);
                job.setMinSalary(BigDecimal.valueOf(grade * 10000 + 25000));
                job.setMaxSalary(BigDecimal.valueOf(grade * 10000 + 75000));
                entityManager.persist(job);
            }
        }
        
        
        QDepartment department = QDepartment.department;
        
        JPAQuery query = new JPAQuery(entityManager);
        Department bridge = query.from(department).where(department.name.eq("Bridge")).uniqueResult(department);
        
        Employee captain = newEmployee (entityManager, "Jean-Luc", "Picard", null, JobTrack.COMMAND, 10, bridge);
        bridge.setManager(captain);
        
        newEmployee (entityManager, "Willam", "Riker", captain, JobTrack.COMMAND, 9, bridge);
        newEmployee (entityManager, "Data", "", captain, JobTrack.TECHNICAL, 8, bridge);
        newEmployee (entityManager, "Geordi", "La Forge", captain, JobTrack.TECHNICAL, 7, bridge);
        newEmployee (entityManager, "Worf", "", captain, JobTrack.SECURITY, 5, bridge);
        newEmployee (entityManager, "Natasha", "Yar", captain, JobTrack.SECURITY, 5, bridge);
        newEmployee (entityManager, "Deanna", "Troi", captain, JobTrack.MEDICAL, 5, bridge);
        
        query = new JPAQuery(entityManager);
        Department sickBay = query.from(department).where(department.name.eq("SickBay")).uniqueResult(department);
        
        newEmployee (entityManager, "Katherine Pulaski", "", captain, JobTrack.MEDICAL, 6, sickBay);
        newEmployee (entityManager, "Beverly", "Crusher", captain, JobTrack.MEDICAL, 6, sickBay);
        
        entityManager.getTransaction().commit();
    }

    /**
     * @param string
     * @param string2
     * @param tcpe
     * @return
     */
    private Employee newEmployee (EntityManager entityManager, String firstName, String lastName, Employee manager, JobTrack track, int grade, Department department)
    {
        JPAQuery query = new JPAQuery(entityManager);
        QJob job = QJob.job;
        
        Employee e = new Employee();
        e.setFirstName(firstName);
        e.setLastName(lastName);
        e.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@teradata.com");
        e.setPhoneNumber("(555) 555-5555");
        e.setSalary(BigDecimal.valueOf(grade * 20000));
        e.setHireDate(new Timestamp(System.currentTimeMillis()));
        e.setDepartment(department);
        e.setManager(manager);
        e.setJob(query.from(job).where(job.jobTrack.eq(track).and(job.grade.eq(grade))).uniqueResult(job));
        entityManager.persist(e);
        return e;
    }
    
    
}
