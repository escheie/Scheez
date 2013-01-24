package org.scheez.dao;

import java.util.List;

import org.junit.Test;
import org.scheez.map.RowMapper;
import org.scheez.test.mysql.Tables;
import org.scheez.test.util.DataSourceUtil;
import org.springframework.jdbc.core.JdbcTemplate;

public class ObjectMapperTest
{
    
    @Test
    public void test()
    {
        JdbcTemplate template = new JdbcTemplate(DataSourceUtil.getMysqlDataSource());
        List<Tables> list = template.query("select * from information_schema.tables", new RowMapper<Tables>(Tables.class));
    }
}
