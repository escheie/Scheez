/*
 * Copyright (C) 2013 by Teradata Corporation. All Rights Reserved. TERADATA CORPORATION
 * CONFIDENTIAL AND TRADE SECRET
 */
package org.scheez.test.jpa;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.dao.SchemaDaoFactory;
import org.scheez.test.TestDatabase;
import org.scheez.test.TestPersistenceUnit;

import com.mysema.query.jpa.impl.JPAQuery;

/**
 * @author es151000
 * @version $Id: $
 */
public class EnterpriseSchema extends TestPersistenceUnit
{
    public static final String SCHEMA = "enterprise";

    public static final int BRIDGE_CREW_COUNT = 7;

    public static final int TABLE_COUNT = 3;

    public static final String TABLE_DEPARTMENT = "department";

    public static final String TABLE_EMPLOYEE = "employee";

    public static final String TABLE_JOB = "job";

    public static final String COLUMN_ID = "id";

    public static final String COLUMN_DEPARTMENT_ID = "department_id";

    public static final String COLUMN_MANAGER_ID = "manager_id";

    public static final String COLUMN_JOB_ID = "job_id";

    private static EnterpriseSchema epu;

    private EnterpriseSchema()
    {
        super("org.scheez.test.jpa.enterprise", "jndi:jdbc/Enterprise/DataSource");
    }

    public synchronized static EnterpriseSchema getInstance()
    {
        if (epu == null)
        {
            epu = new EnterpriseSchema();
        }
        return epu;
    }

    public void init(TestDatabase testDatabase)
    {
        getEntityManagerFactory(testDatabase);
    }

    @Override
    protected void setUp(TestDatabase testDatabase)
    {
        SchemaDao schemaDao = SchemaDaoFactory.getSchemaDao(testDatabase.getDataSource());

        if (schemaDao.schemaExists(SCHEMA))
        {
            schemaDao.dropSchema(SCHEMA);
        }
        schemaDao.createSchema(SCHEMA);
    }

    /**
     * @inheritDoc
     */
    @Override
    protected void load(TestDatabase testDatabase, EntityManagerFactory factory)
    {
        EntityManager entityManager = factory.createEntityManager();
        entityManager.getTransaction().begin();

        String departments[] = { "Engineering", "Bridge", "SickBay", "CargoBay", "Security", "Counseling" };

        for (String name : departments)
        {
            Department department = new Department();
            department.setName(name);
            entityManager.persist(department);
        }

        for (JobTrack track : JobTrack.values())
        {
            for (int grade = 1; grade <= 10; grade++)
            {
                Job job = new Job();
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

        Employee captain = newEmployee(entityManager, "Jean-Luc", "Picard", null, JobTrack.COMMAND, 10, bridge);
        bridge.setManager(captain);

        newEmployee(entityManager, "Willam", "Riker", captain, JobTrack.COMMAND, 9, bridge);
        newEmployee(entityManager, "Data", "", captain, JobTrack.TECHNICAL, 8, bridge);
        newEmployee(entityManager, "Geordi", "La Forge", captain, JobTrack.TECHNICAL, 7, bridge);
        newEmployee(entityManager, "Worf", "", captain, JobTrack.SECURITY, 5, bridge);
        newEmployee(entityManager, "Natasha", "Yar", captain, JobTrack.SECURITY, 5, bridge);
        newEmployee(entityManager, "Deanna", "Troi", captain, JobTrack.MEDICAL, 5, bridge);

        query = new JPAQuery(entityManager);
        Department sickBay = query.from(department).where(department.name.eq("SickBay")).uniqueResult(department);

        newEmployee(entityManager, "Katherine Pulaski", "", captain, JobTrack.MEDICAL, 6, sickBay);
        newEmployee(entityManager, "Beverly", "Crusher", captain, JobTrack.MEDICAL, 6, sickBay);

        entityManager.getTransaction().commit();
    }

    /**
     * @param string
     * @param string2
     * @param tcpe
     * @return
     */
    private Employee newEmployee(EntityManager entityManager, String firstName, String lastName, Employee manager,
            JobTrack track, int grade, Department department)
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
