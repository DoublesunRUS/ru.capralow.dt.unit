/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.junit.model;

public interface ITestElement
{
    public double getElapsedTimeInSeconds();

    public FailureTrace getFailureTrace();

    public ITestElementContainer getParentContainer();

    public ProgressState getProgressState();

    public Result getTestResult(boolean var1);

    public ITestRunSession getTestRunSession();

    public static final class FailureTrace
    {
        private final String fActual;
        private final String fExpected;
        private final String fTrace;

        public FailureTrace(String trace, String expected, String actual)
        {
            this.fActual = actual;
            this.fExpected = expected;
            this.fTrace = trace;
        }

        public String getActual()
        {
            return this.fActual;
        }

        public String getExpected()
        {
            return this.fExpected;
        }

        public String getTrace()
        {
            return this.fTrace;
        }
    }

    public static final class ProgressState
    {
        public static final ProgressState NOT_STARTED = new ProgressState("Not Started");
        public static final ProgressState RUNNING = new ProgressState("Running");
        public static final ProgressState STOPPED = new ProgressState("Stopped");
        public static final ProgressState COMPLETED = new ProgressState("Completed");
        private String fName;

        private ProgressState(String name)
        {
            this.fName = name;
        }

        @Override
        public String toString()
        {
            return this.fName;
        }
    }

    public static final class Result
    {
        public static final Result UNDEFINED = new Result("Undefined");
        public static final Result OK = new Result("OK");
        public static final Result ERROR = new Result("Error");
        public static final Result FAILURE = new Result("Failure");
        public static final Result IGNORED = new Result("Ignored");
        private String fName;

        private Result(String name)
        {
            this.fName = name;
        }

        @Override
        public String toString()
        {
            return this.fName;
        }
    }

}
