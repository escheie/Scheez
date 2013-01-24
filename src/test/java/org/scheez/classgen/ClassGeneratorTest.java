package org.scheez.classgen;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;
import org.scheez.classgen.ClassGenerator;
import org.scheez.classgen.DefaultClassTemplate;
import org.scheez.test.util.DataSourceUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

public class ClassGeneratorTest
{
    @Test
    public void test()
    {
        final ClassGenerator codeGenerator = new ClassGenerator(new File("src/test/java"), new DefaultClassTemplate());
        JdbcTemplate template = new JdbcTemplate(DataSourceUtil.getMysqlDataSource());
        template.query("select * from information_schema.tables", new ResultSetExtractor<Object>()
        {

            @Override
            public Object extractData(ResultSet rs) throws SQLException,
                    DataAccessException
            {
              
                codeGenerator.generateClass("org.scheez.test.mysql.Tables", rs);  
                
                return null;
            }
            
        });
    }
}
