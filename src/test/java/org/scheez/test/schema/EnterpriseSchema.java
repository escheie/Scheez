/*
 * Copyright (C) 2013 by Teradata Corporation. All Rights Reserved. TERADATA CORPORATION
 * CONFIDENTIAL AND TRADE SECRET
 */
package org.scheez.test.schema;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.dao.SchemaDaoFactory;
import org.scheez.schema.dao.SchemaDaoTeradata;
import org.scheez.schema.diff.SchemaDifference;
import org.scheez.schema.manger.BasicSchemaManager;
import org.scheez.schema.manger.SchemaClasses;
import org.scheez.schema.mapper.HibernateNamingStrategy;
import org.scheez.schema.model.Sequence;
import org.scheez.schema.model.SequenceName;
import org.scheez.schema.reports.HtmlReport;
import org.scheez.test.TestDatabase;
import org.scheez.test.TestPersistenceUnit;
import org.scheez.util.BaseObject;

import com.mysema.query.jpa.impl.JPAQuery;

/**
 * @author es151000
 * @version $Id: $
 */
public class EnterpriseSchema extends TestPersistenceUnit
{
    public static final String DEFAULT_SCHEMA = "enterprise";

    public static final int BRIDGE_CREW_COUNT = 7;

    public static final int TABLE_COUNT = 3;

    public static final String TABLE_DEPARTMENT = "departments";

    public static final String TABLE_EMPLOYEE = "employees";

    public static final String TABLE_JOB = "jobs";

    public static final String COLUMN_ID = "id";

    public static final String COLUMN_DEPARTMENT_ID = "department_id";

    public static final String COLUMN_MANAGER_ID = "manager_id";

    public static final String COLUMN_JOB_ID = "job_id";

    private static Map<Context, EnterpriseSchema> schemas = new HashMap<Context, EnterpriseSchema>();

    private Context context;

    private EnterpriseSchema(Context context)
    {
        super("org.scheez.test.schema.enterprise", "jndi:jdbc/Enterprise/DataSource");
        this.context = context;
        getProperties().put("hibernate.default_schema", context.getSchemaName());
        getProperties().put("hibernate.show_sql", "true");
        getProperties().put("hibernate.ejb.naming_strategy", HibernateNamingStrategy.class.getName());
        if(context.isHbm2ddl())
        {
            getProperties().put("hibernate.hbm2ddl.auto", "create");
        }
    }

    public synchronized static EnterpriseSchema getInstance()
    {
        return getInstance(DEFAULT_SCHEMA, false);
    }

    public synchronized static EnterpriseSchema getInstance(String schemaName, boolean hbm2ddl)
    {
        Context context = new Context (schemaName, hbm2ddl);
        
        EnterpriseSchema schema = schemas.get(context);
        if (schema == null)
        {
            schema = new EnterpriseSchema(context);
            schemas.put(context, schema);
        }
        return schema;
    }
    
    public String getSchemaName ()
    {
        return context.getSchemaName();
    }

    public void init (TestDatabase testDatabase)
    {
        getEntityManagerFactory(testDatabase);
    }

    @Override
    protected void setUp(TestDatabase testDatabase)
    {
        SchemaDao schemaDao = SchemaDaoFactory.getSchemaDao(testDatabase.getDataSource());

        if (schemaDao.schemaExists(context.getSchemaName()))
        {
            schemaDao.dropSchema(context.getSchemaName());
        }
        schemaDao.createSchema(context.getSchemaName());
        
        if (schemaDao instanceof SchemaDaoTeradata)
        {
            getProperties().setProperty("hibernate.dialect", "org.hibernate.dialect.TeradataDialect");
        }
        
        if (!context.isHbm2ddl())
        {
            SchemaClasses schemaClasses = new SchemaClasses();
            schemaClasses.include(Employee.class);
            schemaClasses.include(Department.class);
            schemaClasses.include(Job.class);
            
            BasicSchemaManager basicSchemaManager = new BasicSchemaManager(context.getSchemaName(), schemaDao, schemaClasses);
            
            List<SchemaDifference> differences = basicSchemaManager.findDifferences();            
            
            try
            {
                HtmlReport.generate (schemaDao, differences, new File(
                        "build/reports/ddl/enterprise/" + testDatabase.getName() + ".html"));
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            basicSchemaManager.resolveDifferences(differences);
            
            schemaDao.createSequence(new Sequence(new SequenceName(context.getSchemaName(), "hibernate_sequence")));
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    protected void load(TestDatabase testDatabase, EntityManagerFactory factory)
    {
        EntityManager entityManager = factory.createEntityManager();
        entityManager.getTransaction().begin();

        String departments[] = { "Engineering", "Bridge", "SickBay", "CargoBay", "Security",
                "Counseling", "TenForward" };

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
        Department bridge = query.from(department).where(department.name.eq("Bridge"))
                .uniqueResult(department);

        Employee captain = newEmployee(entityManager, "Jean-Luc", "Picard", null, JobTrack.COMMAND,
                10, bridge);
        bridge.setManager(captain);

        newEmployee(entityManager, "Willam", "Riker", captain, JobTrack.COMMAND, 9, bridge);
        newEmployee(entityManager, "Data", null, captain, JobTrack.TECHNICAL, 8, bridge);
        newEmployee(entityManager, "Geordi", "La Forge", captain, JobTrack.TECHNICAL, 7, bridge);
        newEmployee(entityManager, "Worf", null, captain, JobTrack.SECURITY, 5, bridge);
        newEmployee(entityManager, "Natasha", "Yar", captain, JobTrack.SECURITY, 5, bridge);
        newEmployee(entityManager, "Deanna", "Troi", captain, JobTrack.MEDICAL, 5, bridge);

        query = new JPAQuery(entityManager);
        Department sickBay = query.from(department).where(department.name.eq("SickBay"))
                .uniqueResult(department);

        newEmployee(entityManager, "Katherine", "Pulaski", captain, JobTrack.MEDICAL, 6, sickBay);
        newEmployee(entityManager, "Beverly", "Crusher", captain, JobTrack.MEDICAL, 6, sickBay);
        
        query = new JPAQuery(entityManager);
        Department tenForward = query.from(department).where(department.name.eq("TenForward")).uniqueResult(department);
        
        newEmployee(entityManager, "Guinan", null, captain, JobTrack.CIVILIAN, 3, tenForward);

        entityManager.getTransaction().commit();
    }

    /**
     * @param string
     * @param string2
     * @param tcpe
     * @return
     */
    private Employee newEmployee(EntityManager entityManager, String firstName, String lastName,
            Employee manager,
            JobTrack track, int grade, Department department)
    {
        JPAQuery query = new JPAQuery(entityManager);
        QJob job = QJob.job;

        Employee e = new Employee();
        e.setFirstName(firstName);
        e.setLastName(lastName);
        e.setEmail(firstName.toLowerCase() + ((lastName != null) ? "." + lastName.toLowerCase() : "") + "@teradata.com");
        e.setPhoneNumber("(555) 555-5555");
        e.setSalary(BigDecimal.valueOf(grade * 20000));
        e.setHireDate(new Timestamp(System.currentTimeMillis()));
        e.setDepartment(department);
        e.setManager(manager);
        e.setJob(query.from(job).where(job.jobTrack.eq(track).and(job.grade.eq(grade)))
                .uniqueResult(job));
        entityManager.persist(e);
        return e;
    }

    private static class Context extends BaseObject
    {
        private String schemaName;

        private boolean hbm2ddl;

        public Context(String schemaName, boolean hbm2ddl)
        {
            super();
            this.schemaName = schemaName;
            this.hbm2ddl = hbm2ddl;
        }

        /**
         * @return the schemaName
         */
        public String getSchemaName()
        {
            return schemaName;
        }

        /**
         * @return the hbm2ddl
         */
        public boolean isHbm2ddl()
        {
            return hbm2ddl;
        }

    }
}
