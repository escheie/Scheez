package org.scheez.test.schema;

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
        entityManagerFactory = EnterpriseSchema.getInstance().getEntityManagerFactory(testDatabase);
    }

    @Test
    public void testQueryDSL1()
    {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        QEmployee employee = QEmployee.employee;

        JPAQuery query = new JPAQuery(entityManager);
        List<Employee> employees = query.from(employee).where(employee.job.jobTrack.eq(JobTrack.SECURITY))
                .orderBy(employee.firstName.asc()).list(employee);
        assertEquals(2, employees.size());
        assertEquals("Natasha", employees.get(0).getFirstName());
        assertEquals("Worf", employees.get(1).getFirstName());
        assertEquals("Bridge", employees.get(0).getDepartment().getName());
        assertEquals("Picard", employees.get(1).getManager().getLastName());

        entityManager.close();
    }

    @Test
    public void testQueryDSL2()
    {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        QEmployee employee = QEmployee.employee;

        JPAQuery query = new JPAQuery(entityManager);
        List<Object[]> results = query.from(employee).groupBy(employee.job.jobTrack, employee.department.name)
                .orderBy(employee.count().desc(), employee.salary.sum().desc())
                .list(employee.job.jobTrack, employee.department.name, employee.salary.sum(), employee.count());
        assertEquals(6, results.size());
        assertEquals(JobTrack.COMMAND, results.get(0)[0]);
        assertEquals(JobTrack.TECHNICAL, results.get(1)[0]);
        assertEquals(JobTrack.MEDICAL, results.get(2)[0]);
        assertEquals(JobTrack.SECURITY, results.get(3)[0]);
        assertEquals(JobTrack.MEDICAL, results.get(4)[0]);
        assertEquals(JobTrack.CIVILIAN, results.get(5)[0]);
    }

    @Test
    public void testQueryDSL3()
    {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        QEmployee employee = QEmployee.employee;

        JPAQuery query = new JPAQuery(entityManager);
        List<String> names = query.from(employee)
                .where(employee.lastName.isNull().and(employee.department.name.eq("Bridge")))
                .orderBy(employee.firstName.asc()).list(employee.firstName);
        
        assertEquals(2, names.size());
        assertEquals("Data", names.get(0)); 
        assertEquals("Worf", names.get(1));
    }

}
