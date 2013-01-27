package org.scheez.schema.dao.impl;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public abstract class AbstractDao
{
    protected JdbcTemplate jdbcTemplate;

    public AbstractDao(DataSource dataSource)
    {
        this(new JdbcTemplate(dataSource, false));
    }

    public AbstractDao(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    public DataSource getDataSource()
    {
        return jdbcTemplate.getDataSource();
    }
}
