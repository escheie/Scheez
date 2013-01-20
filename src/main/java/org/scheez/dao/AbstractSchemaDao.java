package org.scheez.dao;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public abstract class AbstractSchemaDao implements SchemaDao
{
    protected JdbcTemplate jdbcTemplate;
    
    public AbstractSchemaDao (DataSource dataSource)
    {
        this(new JdbcTemplate(dataSource, false));
    }

    public AbstractSchemaDao (JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }
}
