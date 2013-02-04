package org.scheez.schema.manger;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.scheez.schema.dao.SchemaDao;
import org.scheez.schema.dao.impl.SchemaDaoFactoryUrl;
import org.scheez.schema.diff.MissingTable;
import org.scheez.schema.diff.SchemaDifference;
import org.scheez.schema.diff.SchemaDifference.Type;
import org.scheez.test.db.TestDatabase;
import org.scheez.test.db.TestDatabaseManager;
import org.scheez.test.schema.Person;

@RunWith(Parameterized.class)
public class BasicSchemaManagerTest
{
    private static final Log log = LogFactory.getLog(BasicSchemaManagerTest.class);
    
    private static final String TEST_SCHEMA = "scheez_test";

    private SchemaDao schemaDao;

    public BasicSchemaManagerTest(TestDatabase testDatabase)
    {
        schemaDao = new SchemaDaoFactoryUrl(testDatabase.getUrl(), testDatabase.getDataSource()).getSchemaDao();
    }
   
    @Before
    public void setup ()
    { 
        if(schemaDao.schemaExists(TEST_SCHEMA))
        {
            schemaDao.dropSchema(TEST_SCHEMA);
        }
        schemaDao.createSchema(TEST_SCHEMA);
    }

    /**
     * Test method for
     * {@link org.scheez.schema.manger.BasicSchemaManager#diff()}.
     */
    @Test
    public void testMissingTable()
    {
        SchemaClasses classes = new SchemaClasses();
        classes.include(Person.class);
        
        BasicSchemaManager schemaManager = new BasicSchemaManager(TEST_SCHEMA, schemaDao, classes);
        List<SchemaDifference> diff = schemaManager.diff();
        
        assertNotNull(diff);
        assertEquals(1, diff.size());
        
        MissingTable missingTable = (MissingTable)diff.get(0);
        
        log.info(missingTable);
        assertEquals(Type.MISSING_TABLE, missingTable.getType());
        assertEquals(Person.class, missingTable.getTableClass());
        assertNull(missingTable.getTable());
        assertEquals("persons", missingTable.getTableName());
        assertNotNull(missingTable.getMessage());
    }
    
    @Parameters (name="{0}")
    public static Collection<Object[]> testDatabases ()
    {
        return TestDatabaseManager.getInstance().getDatabaseParameters();
    } 
}
