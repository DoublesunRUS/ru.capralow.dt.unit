/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.model;

import org.eclipse.core.runtime.Assert;

import ru.capralow.dt.unit.junit.model.ITestElement;
import ru.capralow.dt.unit.junit.model.ITestElementContainer;
import ru.capralow.dt.unit.junit.model.ITestRunSession;

public abstract class TestElement
    implements ITestElement
{
    public static String extractRawClassName(String testNameString)
    {
        if (testNameString.startsWith("[") && testNameString.endsWith("]")) //$NON-NLS-1$//$NON-NLS-2$
        {
            // a group of parameterized tests, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=102512
            return testNameString;
        }
        int index = testNameString.lastIndexOf('(');
        if (index < 0)
            return testNameString;
        int end = testNameString.lastIndexOf(')');
        testNameString = testNameString.substring(index + 1, end > index ? end : testNameString.length());
        return testNameString;
    }

    private static String extractClassName(String testNameString)
    {
        testNameString = extractRawClassName(testNameString);
        testNameString = testNameString.replace('$', '.'); // see bug 178503
        return testNameString;
    }

    private final TestSuiteElement fParent;
    private final String fId;

    private String fTestName;

    /**
     * The display name of the test element, can be <code>null</code>. In that case, use
     * {@link TestElement#fTestName fTestName}.
     */
    private String fDisplayName;

    /**
     * The array of method parameter types (as given by
     * org.junit.platform.engine.support.descriptor.MethodSource.getMethodParameterTypes()) if
     * applicable, otherwise <code>null</code>.
     */
    private String[] fParameterTypes;

    /**
     * The unique ID of the test element which can be <code>null</code> as it is applicable to JUnit 5
     * and above.
     */
    private String fUniqueId;
    private Status fStatus;
    private String fTrace;
    private String fExpected;

    private String fActual;

    private boolean fAssumptionFailed;

    /**
     * Running time in seconds. Contents depend on the current {@link #getProgressState()}:
     * <ul>
     * <li>{@link ru.capralow.dt.unit.junit.model.ITestElement.ProgressState#NOT_STARTED}: {@link Double#NaN}</li>
     * <li>{@link ru.capralow.dt.unit.junit.model.ITestElement.ProgressState#RUNNING}: negated start time</li>
     * <li>{@link ru.capralow.dt.unit.junit.model.ITestElement.ProgressState#STOPPED}: elapsed time</li>
     * <li>{@link ru.capralow.dt.unit.junit.model.ITestElement.ProgressState#COMPLETED}: elapsed time</li>
     * </ul>
     */
    /* default */ double fTime = Double.NaN;

    /**
     * @param parent the parent, can be <code>null</code>
     * @param id the test id
     * @param testName the test name
     * @param displayName the test display name, can be <code>null</code>
     * @param parameterTypes the array of method parameter types (as given by
     *            org.junit.platform.engine.support.descriptor.MethodSource.getMethodParameterTypes())
     *            if applicable, otherwise <code>null</code>
     * @param uniqueId the unique ID of the test element, can be <code>null</code> as it is applicable
     *            to JUnit 5 and above
     */
    protected TestElement(TestSuiteElement parent, String id, String testName, String displayName,
        String[] parameterTypes, String uniqueId)
    {
        Assert.isNotNull(id);
        Assert.isNotNull(testName);
        fParent = parent;
        fId = id;
        fTestName = testName;
        fDisplayName = displayName;
        fParameterTypes = parameterTypes;
        fUniqueId = uniqueId;
        fStatus = Status.NOT_RUN;
        if (parent != null)
            parent.addChild(this);
    }

    public String getActual()
    {
        return fActual;
    }

    /**
     * @return return the class name
     * @see ru.capralow.dt.unit.internal.junit.runner.ITestIdentifier#getName()
     * @see ru.capralow.dt.unit.internal.junit.runner.jdt.internal.junit.runner.MessageIds#TEST_IDENTIFIER_MESSAGE_FORMAT
     */
    public String getClassName()
    {
        return extractClassName(getTestName());
    }

    /**
     * Returns the display name of the test. Can be <code>null</code>. In that case, use
     * {@link TestElement#getTestName() getTestName()}.
     *
     * @return the test display name, can be <code>null</code>
     */
    public String getDisplayName()
    {
        return fDisplayName;
    }

    @Override
    public double getElapsedTimeInSeconds()
    {
        if (Double.isNaN(fTime) || fTime < 0.0d)
        {
            return Double.NaN;
        }

        return fTime;
    }

    public String getExpected()
    {
        return fExpected;
    }

    @Override
    public FailureTrace getFailureTrace()
    {
        var testResult = getTestResult(false);
        if (testResult == Result.ERROR || testResult == Result.FAILURE
            || (testResult == Result.IGNORED && fTrace != null))
        {
            return new FailureTrace(fTrace, fExpected, fActual);
        }
        return null;
    }

    public String getId()
    {
        return fId;
    }

    /**
     * @return the array of method parameter types (as given by
     *         org.junit.platform.engine.support.descriptor.MethodSource.getMethodParameterTypes()) if
     *         applicable, otherwise <code>null</code>
     */
    public String[] getParameterTypes()
    {
        return fParameterTypes;
    }

    /**
     * @return the parent suite, or <code>null</code> for the root
     */
    public TestSuiteElement getParent()
    {
        return fParent;
    }

    @Override
    public ITestElementContainer getParentContainer()
    {
        if (fParent instanceof TestRoot)
        {
            return getTestRunSession();
        }
        return fParent;
    }

    @Override
    public ProgressState getProgressState()
    {
        return getStatus().convertToProgressState();
    }

    public TestRoot getRoot()
    {
        return getParent().getRoot();
    }

    public Status getStatus()
    {
        return fStatus;
    }

    public String getTestName()
    {
        return fTestName;
    }

    @Override
    public Result getTestResult(boolean includeChildren)
    {
        if (fAssumptionFailed)
        {
            return Result.IGNORED;
        }
        return getStatus().convertToResult();
    }

    @Override
    public ITestRunSession getTestRunSession()
    {
        return getRoot().getTestRunSession();
    }

    public String getTrace()
    {
        return fTrace;
    }

    /**
     * Returns the unique ID of the test element. Can be <code>null</code> as it is applicable to JUnit
     * 5 and above.
     *
     * @return the unique ID of the test, can be <code>null</code>
     */
    public String getUniqueId()
    {
        return fUniqueId;
    }

    public boolean isAssumptionFailure()
    {
        return fAssumptionFailed;
    }

    public boolean isComparisonFailure()
    {
        return fExpected != null && fActual != null;
    }

    public void setAssumptionFailed(boolean assumptionFailed)
    {
        fAssumptionFailed = assumptionFailed;
    }

    public void setElapsedTimeInSeconds(double time)
    {
        fTime = time;
    }

    public void setName(String name)
    {
        fTestName = name;
    }

    public void setStatus(Status status)
    {
        if (status == Status.RUNNING)
        {
            fTime = -System.currentTimeMillis() / 1000d;
        }
        else if (status.convertToProgressState() == ProgressState.COMPLETED)
        {
            if (fTime < 0)
            { // assert ! Double.isNaN(fTime)
                double endTime = System.currentTimeMillis() / 1000.0d;
                fTime = endTime + fTime;
            }
        }

        fStatus = status;
        TestSuiteElement parent = getParent();
        if (parent != null)
            parent.childChangedStatus(this, status);
    }

    public void setStatus(Status status, String trace, String expected, String actual)
    {
        if (trace != null && fTrace != null)
        {
            //don't overwrite first trace if same test run logs multiple errors
            fTrace = fTrace + trace;
        }
        else
        {
            fTrace = trace;
            fExpected = expected;
            fActual = actual;
        }
        setStatus(status);
    }

    @Override
    public String toString()
    {
        return getProgressState() + " - " + getTestResult(true); //$NON-NLS-1$
    }

    public static final class Status
    {
        public static final Status RUNNING_ERROR = new Status("RUNNING_ERROR", 5); //$NON-NLS-1$
        public static final Status RUNNING_FAILURE = new Status("RUNNING_FAILURE", 6); //$NON-NLS-1$
        public static final Status RUNNING = new Status("RUNNING", 3); //$NON-NLS-1$

        public static final Status ERROR = new Status("ERROR", /*1*/ITestRunListener2.STATUS_ERROR); //$NON-NLS-1$
        public static final Status FAILURE = new Status("FAILURE", /*2*/ITestRunListener2.STATUS_FAILURE); //$NON-NLS-1$
        public static final Status OK = new Status("OK", /*0*/ITestRunListener2.STATUS_OK); //$NON-NLS-1$
        public static final Status NOT_RUN = new Status("NOT_RUN", 4); //$NON-NLS-1$

        private static final Status[] OLD_CODE = { OK, ERROR, FAILURE };

        public static Status combineStatus(Status one, Status two)
        {
            Status progress = combineProgress(one, two);
            Status error = combineError(one, two);
            return combineProgressAndErrorStatus(progress, error);
        }

        /**
         * @param oldStatus one of {@link ITestRunListener2}'s STATUS_* constants
         * @return the Status
         */
        public static Status convert(int oldStatus)
        {
            return OLD_CODE[oldStatus];
        }

        private static Status combineError(Status one, Status two)
        {
            if (one.isError() || two.isError())
                return ERROR;
            else if (one.isFailure() || two.isFailure())
                return FAILURE;
            else
                return OK;
        }

        private static Status combineProgress(Status one, Status two)
        {
            if (one.isNotRun() && two.isNotRun())
                return NOT_RUN;
            else if ((one.isDone() && two.isDone()) || (!one.isRunning() && !two.isRunning()))
            { // One done, one not-run -> a parent failed and its children are not run
                return OK;
            }
            else
                return RUNNING;
        }

        private static Status combineProgressAndErrorStatus(Status progress, Status error)
        {
            if (progress.isDone())
            {
                if (error.isError())
                    return ERROR;
                if (error.isFailure())
                    return FAILURE;
                return OK;
            }

            if (progress.isNotRun())
            {
                return NOT_RUN;
            }

            if (error.isError())
                return RUNNING_ERROR;
            if (error.isFailure())
                return RUNNING_FAILURE;
            return RUNNING;
        }

        /* error state predicates */

        private final String fName;

        private final int fOldCode;

        private Status(String name, int oldCode)
        {
            fName = name;
            fOldCode = oldCode;
        }

        public ProgressState convertToProgressState()
        {
            if (isRunning())
            {
                return ProgressState.RUNNING;
            }
            if (isDone())
            {
                return ProgressState.COMPLETED;
            }
            return ProgressState.NOT_STARTED;
        }

        /* progress state predicates */

        public Result convertToResult()
        {
            if (isNotRun())
                return Result.UNDEFINED;
            if (isError())
                return Result.ERROR;
            if (isFailure())
                return Result.FAILURE;
            if (isRunning())
            {
                return Result.UNDEFINED;
            }
            return Result.OK;
        }

        public int getOldCode()
        {
            return fOldCode;
        }

        public boolean isDone()
        {
            return this == OK || this == FAILURE || this == ERROR;
        }

        public boolean isError()
        {
            return this == ERROR || this == RUNNING_ERROR;
        }

        public boolean isErrorOrFailure()
        {
            return isError() || isFailure();
        }

        public boolean isFailure()
        {
            return this == FAILURE || this == RUNNING_FAILURE;
        }

        public boolean isNotRun()
        {
            return this == NOT_RUN;
        }

        public boolean isOK()
        {
            return this == OK || this == RUNNING || this == NOT_RUN;
        }

        public boolean isRunning()
        {
            return this == RUNNING || this == RUNNING_FAILURE || this == RUNNING_ERROR;
        }

        @Override
        public String toString()
        {
            return fName;
        }

    }

}
