package org.scheez.codegen;

import static org.junit.Assert.*;

import org.junit.Test;

public class CodeGeneratorTest
{
    @Test
    public void test()
    {
        CodeGenerator codeGenerator = new CodeGenerator("org.scheez.test.TestGen", null, new DefaultCodeTemplate());
    }
}
