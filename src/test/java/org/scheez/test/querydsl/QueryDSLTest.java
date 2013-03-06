package org.scheez.test.querydsl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.scheez.test.ScheezTestConfiguration;

import com.mysema.query.jpa.impl.JPAQuery;

public class QueryDSLTest 
{
    private EntityManagerFactory entityManagerFactory;

    @Before
    public void setUp() throws Exception
    {
        ScheezTestConfiguration.getInstance();
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
        
        JPAQuery query = new JPAQuery(entityManager);
        QEmployee employee = QEmployee.employee;
       
        List<Employee> employees = query.from(employee).where(employee.lastName.startsWith("Sch")).list(employee);
        
        System.out.println(employees);
    }
    
    private void generateTestData ()
    {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        
        String departments[] = {"TCPE", "DBS", "GSC", "TAC", "HR", "ITS", "TAE", "PM", "CS", "PS", "Sales/Americas", "Sales/EMEA", "Sales/AJP", "BusOps", 
                "Legal", "Finance", "LT"};
        
        for (String name : departments)
        {
            Department department = new Department ();
            department.setName(name);
            entityManager.persist(department);
        }
        
        for ( JobTrack track : JobTrack.values() )
        {
            for(int grade = 0; grade < 10; grade++)
            {
                Job job = new Job ();
                job.setJobTrack(track);
                job.setGrade(grade);
                job.setMinSalary(BigDecimal.valueOf(grade * 10000 + 25000));
                job.setMaxSalary(BigDecimal.valueOf(grade * 10000 + 75000));
                entityManager.persist(job);
            }
        }
        
        JPAQuery query = new JPAQuery(entityManager);
        QDepartment department = QDepartment.department;
        
        Department tcpe = query.from(department).where(department.name.eq("TCPE")).uniqueResult(department);
        
        Employee e = newEmployee (entityManager, "Carson", "Schmidt", null, getJob(query, JobTrack.MANAGEMENT, 9), tcpe);
        tcpe.setManager(e);
        
        e = newEmployee (entityManager, "Keith", "Sweetnam", e, getJob(query, JobTrack.MANAGEMENT, 8), tcpe);
        e = newEmployee (entityManager, "Blaise", "McEvoy", e, getJob(query, JobTrack.MANAGEMENT, 7), tcpe);
        e = newEmployee (entityManager, "Eric", "Scheie", e, getJob(query, JobTrack.TECHNICAL, 5), tcpe);
        
        entityManager.getTransaction().commit();
    }

    /**
     * @param query
     * @param management
     * @param i
     * @return
     */
    private Job getJob(JPAQuery query, JobTrack track, int grade)
    {
        QJob job = QJob.job;
        return query.from(job).where(job.jobTrack.eq(track).and(job.grade.eq(grade))).uniqueResult(job);
    }

    /**
     * @param string
     * @param string2
     * @param tcpe
     * @return
     */
    private Employee newEmployee (EntityManager entityManager, String firstName, String lastName, Employee manager, Job job, Department department)
    {
        Employee e = new Employee();
        e.setFirstName(firstName);
        e.setLastName(lastName);
        e.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@teradata.com");
        e.setPhoneNumber("(555) 555-5555");
        e.setHireDate(new Timestamp(System.currentTimeMillis()));
        e.setDepartment(department);
        e.setManager(manager);
        e.setJob(job);
        entityManager.persist(e);
        return e;
    }
    
    
}
