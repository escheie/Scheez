package org.scheez.test.jpa;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.scheez.test.TestDatabase;
import org.scheez.test.junit.ScheezTestDatabase;

import com.mysema.query.jpa.impl.JPAQuery;

@RunWith(ScheezTestDatabase.class)
public class QueryDSLTest 
{
    private EntityManagerFactory entityManagerFactory;

    public QueryDSLTest(TestDatabase testDatabase)
    {
        entityManagerFactory = EnterprisePersistenceUnit.getInstance().getEntityManagerFactory(testDatabase);
    }
    
    @Test
    public void testQueryDSL1 ()
    {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        QEmployee employee = QEmployee.employee;
       
        JPAQuery query = new JPAQuery(entityManager);
        List<Employee> employees = query.from(employee).where(employee.job.jobTrack.eq(JobTrack.SECURITY)).orderBy(employee.firstName.asc()).list(employee);
        assertEquals(2, employees.size());
        assertEquals("Natasha", employees.get(0).getFirstName());
        assertEquals("Worf", employees.get(1).getFirstName());
        assertEquals("Bridge", employees.get(0).getDepartment().getName());
        assertEquals("Picard", employees.get(1).getManager().getLastName());
        
        entityManager.close();
    }
    
    @Test
    public void testQueryDSL2 ()
    {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        QEmployee employee = QEmployee.employee;
        
        JPAQuery query = new JPAQuery(entityManager);
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
    
}