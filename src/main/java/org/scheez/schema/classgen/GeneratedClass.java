package org.scheez.schema.classgen;

import java.io.File;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class GeneratedClass
{
    private File srcDir;

    private File file;

    private String fullyQualifiedClassName;

    public GeneratedClass(File file, File srcDir, String fullyQualifiedClassName)
    {
        super();
        this.file = file;
        this.srcDir = srcDir;
        this.fullyQualifiedClassName = fullyQualifiedClassName;
    }

    public File getFile()
    {
        return file;
    }

    public String getFullyQualifiedClassName()
    {
        return fullyQualifiedClassName;
    }

    public Class<?> compile (File outputDir) throws Exception
    {
        URLClassLoader cl = new URLClassLoader(new URL[] { outputDir.toURI().toURL() });
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        StringWriter writer = new StringWriter();
        ArrayList<String> options = new ArrayList<String>();
        options.add("-d");
        options.add(outputDir.getAbsolutePath());
        CompilationTask task = compiler.getTask(writer, fileManager, null, options, null,
                fileManager.getJavaFileObjects(file));
        if(!task.call())
        {
            throw new RuntimeException (writer.toString());
        }
        return cl.loadClass(fullyQualifiedClassName);
    }

}
