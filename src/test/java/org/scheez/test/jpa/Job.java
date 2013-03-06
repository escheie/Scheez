package org.scheez.test.jpa;

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

    @Column (nullable=false)
    private JobTrack jobTrack;

    @Column (nullable=false)
    private Integer grade;

    @Column(nullable = false)
    private BigDecimal minSalary;

    @Column(nullable = false)
    private BigDecimal maxSalary;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    /**
     * @return the jobTrack
     */
    public JobTrack getJobTrack()
    {
        return jobTrack;
    }

    /**
     * @param jobTrack
     *            the jobTrack to set
     */
    public void setJobTrack(JobTrack jobTrack)
    {
        this.jobTrack = jobTrack;
    }

    /**
     * @return the grade
     */
    public Integer getGrade()
    {
        return grade;
    }

    /**
     * @param grade
     *            the grade to set
     */
    public void setGrade(Integer grade)
    {
        this.grade = grade;
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
