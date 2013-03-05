package org.scheez.hibernate.tutorial;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.scheez.util.BaseObject;

/**
 * @author Eric
 */
@Entity
public class Job extends BaseObject
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column (unique=true)
    private String title;

    @Column (nullable=false)
    private BigDecimal minSalary;

    @Column (nullable=false)
    private BigDecimal maxSalary;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public BigDecimal getMinSalary()
    {
        return minSalary;
    }

    public void setMinSalary(BigDecimal minSalary)
    {
        this.minSalary = minSalary;
    }

    public BigDecimal getMaxSalary()
    {
        return maxSalary;
    }

    public void setMaxSalary(BigDecimal maxSalary)
    {
        this.maxSalary = maxSalary;
    }

}
