package org.scheez.hibernate.tutorial;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HibernateTutorialTest 
{
    private EntityManagerFactory entityManagerFactory;

    @Before
    public void setUp() throws Exception
    {
        entityManagerFactory = Persistence.createEntityManagerFactory("org.scheez.hibernate.tutorial");
    }

    @After
    public void tearDown() throws Exception
    {
        entityManagerFactory.close();
    }

    @Test
    public void testHibernateTutorial ()
    {

        // create a couple of events...
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
          
        Employee manager = new Employee ();
        manager.setFirstName("Blaise");
        manager.setLastName("McEvoy");
        manager.setEmail("blaise.mcevoy@teradata.com");
        manager.setPhoneNumber("(555) 555-5555");
        
        Department department = new Department ();
        department.setName("Viewpoint");
        department.setManager(manager);
        
        manager.setDepartment(department);
        
        Employee me = new Employee ();
        me.setFirstName("Eric");
        me.setLastName("Scheie");
        me.setEmail("eric.scheie@teradata.com");
        me.setPhoneNumber("(555) 555-5555");
        me.setManager(manager);
        me.setDepartment(department);
        
        Job job = new Job ();
        job.setTitle("Software Engineer");
        job.setMinSalary(BigDecimal.valueOf(1));
        job.setMaxSalary(BigDecimal.valueOf(1000000));
        
        JobHistory jobHistory = new JobHistory ();
        jobHistory.setEmployee(me);
        jobHistory.setDepartment(department);
        jobHistory.setJob(job);
        jobHistory.setStartDate(new Timestamp(System.currentTimeMillis()));
        
        ArrayList<JobHistory> jobHistoryList = new ArrayList<JobHistory>();
        jobHistoryList.add(jobHistory);
        me.setJobhistory(jobHistoryList);
        
        entityManager.persist(me);
        
        //entityManager.persist(new Event("Our very first event!", new Date()));
        //entityManager.persist(new Event("A follow up event", new Date()));
        entityManager.getTransaction().commit();
        
        // now lets pull events from the database and list them
        entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        @SuppressWarnings("unchecked")
        List<Employee> result = entityManager.createQuery("from Employee").getResultList();
        for (Employee employee : result)
        {
            System.out.println(employee);
        }
        entityManager.getTransaction().commit();
        entityManager.close();
    }
}
