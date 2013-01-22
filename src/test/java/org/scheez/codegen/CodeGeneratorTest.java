package org.scheez.codegen;

import org.junit.Test;
import org.scheez.test.util.DataSourceUtil;
import org.springframework.jdbc.core.JdbcTemplate;

public class CodeGeneratorTest
{
    @Test
    public void test()
    {
        CodeGenerator codeGenerator = new CodeGenerator("org.scheez.test.TestGen", null, new DefaultCodeTemplate());
        JdbcTemplate template = new JdbcTemplate(DataSourceUtil.getPostgresqlDataSource());
        template.query("select * from pg_class", codeGenerator);
    }
}
