package org.scheez.test.schema;

import java.sql.Date;

import javax.persistence.Column;

public class Person
{
    private Long id;

    @Column (length=1024)
    private String firstName;
    
    @Column (length=1024)
    private String lastName;
    
    @Column (name="dob")
    private Date dateOfBirth;

    private Date dateOfDeath;

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

    public Date getDateOfBirth()
    {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth)
    {
        this.dateOfBirth = dateOfBirth;
    }

    public Date getDateOfDeath()
    {
        return dateOfDeath;
    }

    public void setDateOfDeath(Date dateOfDeath)
    {
        this.dateOfDeath = dateOfDeath;
    }

}
