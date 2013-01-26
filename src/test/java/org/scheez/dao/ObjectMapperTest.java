package org.scheez.dao;

import org.junit.Test;
import org.scheez.test.db.DataSourceUtil;
import org.springframework.jdbc.core.JdbcTemplate;

public class ObjectMapperTest
{
    
    @Test
    public void test()
    {
        JdbcTemplate template = new JdbcTemplate(DataSourceUtil.getMysqlDataSource());
        //List<Tables> list = template.query("select * from information_schema.tables", new RowMapper<Tables>(Tables.class));
    }
}
