package org.scheez.schema.reports;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.hibernate.engine.jdbc.internal.DDLFormatterImpl;
import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.dao.SchemaDaoFactory;
import org.scheez.schema.dao.SchemaDdlExecutor;
import org.scheez.schema.diff.SchemaDifference;

public class HtmlReport
{   
    public static void generate (SchemaDao schemaDao, List<SchemaDifference> schemaDifferences, File file) throws IOException
    {
        file.getParentFile().mkdirs();
        
        schemaDao = SchemaDaoFactory.getSchemaDao(schemaDao.getDataSource());

        BufferedWriter writer = null;

        try
        {
            writer = new BufferedWriter(new FileWriter(file));
            schemaDao.setSchemaDdlExecutor(new SqlExporter(writer));
            
            File srcFile = new File("lib/shjs/sh_main.js");
            FileUtils.copyFile(srcFile, new File(file.getParentFile(), "sh_main.js"));
            srcFile = new File("lib/shjs/lang/sh_sql.js");
            FileUtils.copyFile(srcFile,  new File(file.getParentFile(), "sh_sql.js"));
            srcFile = new File("lib/shjs/css/sh_typical.css");
            FileUtils.copyFile(srcFile, new File(file.getParentFile(), "sh.css"));
            
            writer.write("<html><head>");
            writer.newLine();
            writer.write("<script type=\"text/javascript\" src=\"sh_main.js\"></script>");
            writer.newLine();
            writer.write("<script type=\"text/javascript\" src=\"sh_sql.js\"></script>");
            writer.newLine();
            writer.write("<link type=\"text/css\" rel=\"stylesheet\" href=\"sh.css\">");
            writer.write("</head>");
            writer.newLine();
            writer.write("<body onload=\"sh_highlightDocument();\">");

            if(schemaDifferences.isEmpty())
            {
                writer.write("<h3>Schema up-to-date.  No differences found.</h3>");
            }
            else
            {
                writer.write("<h3>Schema differences found: " + schemaDifferences.size() + "</h3>");
            }
            writer.newLine();
            
            for (SchemaDifference schemaDifference : schemaDifferences)
            {
                exportComment(writer, schemaDifference.toString());
                schemaDifference.reconcileDifferences(schemaDao);
            }
            
            writer.write("</body></html>");
        }
        finally
        {
            if (writer != null)
            {
                writer.close();
            }
        }
    }

    private static void exportComment(BufferedWriter writer, String comment) throws IOException
    {
        writer.write("<pre class=\"sh_sql\">");
        writer.newLine();
        writer.write("-- ");
        writer.write(comment);
        writer.write("</pre>");
        writer.newLine();
    }

    private static void exportSql(BufferedWriter writer, String sql) throws IOException
    {
        writer.write("<pre class=\"sh_sql\">");
        StringTokenizer tokenizer = new StringTokenizer(new DDLFormatterImpl().format(sql), "\n");
        while(tokenizer.hasMoreTokens())
        {
            writer.write(tokenizer.nextToken().substring(4));
            writer.newLine();
        }
        writer.write("</pre>");
        writer.newLine();
    }

    public static class SqlExporter implements SchemaDdlExecutor
    {
        private BufferedWriter writer;

        public SqlExporter(BufferedWriter writer)
        {
            super();
            this.writer = writer;
        }

        @Override
        public void execute(String ddl)
        {
            if(!ddl.endsWith(";"))
            {
                ddl += ";";
            }
            try
            {
                exportSql(writer, ddl);
            }
            catch (IOException e)
            {
                throw new RuntimeException("Unable to write ddl to file.", e);
            }
        }
    }
}
