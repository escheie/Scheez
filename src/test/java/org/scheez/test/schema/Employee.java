package org.scheez.test.schema;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.scheez.util.BaseObject;

@Entity
public class Employee extends BaseObject
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column (nullable = false)
    private String firstName;

    @Column 
    private String lastName;

    private Character middleInitial;

    private String jobTitle;

    @Column(unique = true)
    private String email;

    private String phoneNumber;

    @Column(precision = 10, scale = 2)
    private BigDecimal salary;

    private Timestamp hireDate;

    @ManyToOne
    private Employee manager;

    @ManyToOne (optional=false)
    private Department department;

    @ManyToOne (optional=false)
    private Job job;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    /**
     * @return the middleInitial
     */
    public Character getMiddleInitial()
    {
        return middleInitial;
    }

    /**
     * @param middleInitial
     *            the middleInitial to set
     */
    public void setMiddleInitial(Character middleInitial)
    {
        this.middleInitial = middleInitial;
    }

    /**
     * @return the jobTitle
     */
    public String getJobTitle()
    {
        return jobTitle;
    }

    /**
     * @param jobTitle
     *            the jobTitle to set
     */
    public void setJobTitle(String title)
    {
        this.jobTitle = title;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    public BigDecimal getSalary()
    {
        return salary;
    }

    public void setSalary(BigDecimal salary)
    {
        this.salary = salary;
    }

    public Employee getManager()
    {
        return manager;
    }

    public void setManager(Employee manager)
    {
        this.manager = manager;
    }

    public Department getDepartment()
    {
        return department;
    }

    public void setDepartment(Department department)
    {
        this.department = department;
    }

    /**
     * @return the job
     */
    public Job getJob()
    {
        return job;
    }

    /**
     * @param job
     *            the job to set
     */
    public void setJob(Job job)
    {
        this.job = job;
    }

    /**
     * @return the hireDate
     */
    public Timestamp getHireDate()
    {
        return hireDate;
    }

    /**
     * @param hireDate
     *            the hireDate to set
     */
    public void setHireDate(Timestamp hireDate)
    {
        this.hireDate = hireDate;
    }

}
