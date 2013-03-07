package org.scheez.test.junit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;
import org.junit.runners.model.Statement;
import org.scheez.test.ScheezTestConfiguration;
import org.scheez.test.TestDatabase;

public class ScheezTestDatabase extends Suite
{
    private final ArrayList<Runner> runners = new ArrayList<Runner>();

    public ScheezTestDatabase(Class<?> cls) throws InitializationError
    {
        super(cls, Collections.<Runner> emptyList());
        setScheduler(new ThreadedScheduler());
        for (TestDatabase testDatabase : ScheezTestConfiguration.getInstance().getTestDatabases())
        {
            runners.add(new ScheezClassRunner(cls, testDatabase));
        }
    }

    @Override
    protected List<Runner> getChildren()
    {
        return runners;
    }

    public void filter(Filter filter) throws NoTestsRemainException
    {
        super.filter(new ScheezFilter(filter));
    }

    private static class ScheezClassRunner extends BlockJUnit4ClassRunner
    {
        private TestDatabase testDatabase;

        public ScheezClassRunner(Class<?> cls, TestDatabase testDatabase) throws InitializationError
        {
            super(cls);
            this.testDatabase = testDatabase;
        }

        @Override
        protected Object createTest() throws Exception
        {
            return getTestClass().getOnlyConstructor().newInstance(testDatabase);
        }

        @Override
        protected void validateConstructor(List<Throwable> errors)
        {
            boolean gripe = false;
            if (getTestClass().getJavaClass().getConstructors().length != 1)
            {
                gripe = true;
            }
            else
            {
                Class<?> params[] = getTestClass().getOnlyConstructor().getParameterTypes();
                if ((params.length != 1) || (!params[0].equals(TestDatabase.class)))
                {
                    gripe = true;
                }
            }
            if (gripe)
            {
                errors.add(new Exception(
                        "Test class should have exactly one public constructor that takes a single Test Database argument."));
            }
        }

        @Override
        protected String getName()
        {
            return String.format("[%s]", testDatabase.getName());
        }

        @Override
        protected String testName(FrameworkMethod method)
        {
            //return String.format("[%s] %s", testDatabase.getName(), method.getName());
            return String.format("%s [%s]", method.getName(), testDatabase.getName());
        }

        @Override
        protected Statement classBlock(RunNotifier notifier)
        {
            return childrenInvoker(notifier);
        }
    }

    private static class ScheezFilter extends Filter
    {
        private Filter original;

        public ScheezFilter(Filter original)
        {
            super();
            this.original = original;
        }

        @Override
        public boolean shouldRun(Description description)
        {
            return original.shouldRun(copy(description));
        }

        private Description copy (Description description)
        {
            Description d = null;
            if(description.isTest())
            {
                d = Description.createTestDescription(description.getClassName(), description.getMethodName().replaceAll(" \\[.*\\]", ""));
            }
            else
            {
                d = description.childlessCopy();
            }
            
            for (Description child : description.getChildren())
            {
                d.addChild(copy(child));
            }
            
            return d;
        }

        @Override
        public String describe()
        {
            return original.describe();
        }
    }

    private static class ThreadedScheduler implements RunnerScheduler
    {
        private ExecutorService executor;

        public ThreadedScheduler()
        {
            executor = Executors.newCachedThreadPool();
        }

        @Override
        public void finished()
        {
            executor.shutdown();
            try
            {
                executor.awaitTermination(365, TimeUnit.DAYS);
            }
            catch (InterruptedException exc)
            {
                throw new RuntimeException(exc);
            }
        }

        @Override
        public void schedule(Runnable childStatement)
        {
            executor.submit(childStatement);
        }
    }
}
