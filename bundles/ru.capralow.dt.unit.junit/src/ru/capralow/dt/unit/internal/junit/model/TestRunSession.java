/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.model;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchesListener2;

import com._1c.g5.v8.dt.core.platform.IV8Project;

import ru.capralow.dt.unit.internal.junit.JUnitPlugin;
import ru.capralow.dt.unit.internal.junit.model.TestElement.Status;
import ru.capralow.dt.unit.junit.model.ITestElement;
import ru.capralow.dt.unit.junit.model.ITestElementContainer;
import ru.capralow.dt.unit.junit.model.ITestRunSession;

/**
 * A test run session holds all information about a test run, i.e.
 * launch configuration, launch, test tree (including results).
 */
public class TestRunSession
    implements ITestRunSession
{

    /**
     * The launch, or <code>null</code> iff this session was run externally.
     */
    private final ILaunch fLaunch;
    private final String fTestRunName;

    /**
     * V8 project, or <code>null</code>.
     */
    private final IV8Project fConfigurationProject;

    private final ListenerList<ITestSessionListener> fSessionListeners;

    /**
     * The model root, or <code>null</code> if swapped to disk.
     */
    private TestRoot fTestRoot;

    /**
     * The test run session's cached result, or <code>null</code> if <code>fTestRoot != null</code>.
     */
    private Result fTestResult;

    /**
     * Map from testId to testElement.
     */
    private HashMap<String, TestElement> fIdToTest;

    /**
     * The TestSuites for which additional children are expected.
     */
    private List<IncompleteTestSuite> fIncompleteTestSuites;

    private List<IncompleteTestSuite> fFactoryTestSuites;

    /**
     * Suite for unrooted test case elements, or <code>null</code>.
     */
    private TestSuiteElement fUnrootedSuite;

    /**
     * Number of tests started during this test run.
     */
    volatile int fStartedCount;
    /**
     * Number of tests ignored during this test run.
     */
    volatile int fIgnoredCount;
    /**
     * Number of tests whose assumption failed during this test run.
     */
    volatile int fAssumptionFailureCount;
    /**
     * Number of errors during this test run.
     */
    volatile int fErrorCount;
    /**
     * Number of failures during this test run.
     */
    volatile int fFailureCount;
    /**
     * Total number of tests to run.
     */
    volatile int fTotalCount;
    /**
     * <ul>
     * <li>If &gt; 0: Start time in millis</li>
     * <li>If &lt; 0: Unique identifier for imported test run</li>
     * <li>If = 0: Session not started yet</li>
     * </ul>
     */
    volatile long fStartTime;
    volatile boolean fIsRunning;

    volatile boolean fIsStopped;

    /**
     * @param launch
     * @param configurationProject
     * @param testExtensionName
     */
    public TestRunSession(ILaunch launch, IV8Project configurationProject, String testExtensionName)
    {
        Assert.isNotNull(launch);

        fLaunch = launch;
        fConfigurationProject = configurationProject;

        ILaunchConfiguration launchConfiguration = launch.getLaunchConfiguration();
        if (launchConfiguration != null)
        {
            fTestRunName = launchConfiguration.getName();
        }
        else
        {
            fTestRunName = configurationProject.getProject().getName();
        }

        fTestRoot = new TestRoot(this);
        fIdToTest = new HashMap<>();

        // TODO: Тут вставить код ожидания результата от VA
//        fTestRunnerClient = new RemoteTestRunnerClient();
//        fTestRunnerClient.startListening(new ITestRunListener2[] { new TestSessionNotifier() }, testExtensionName);

        final ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
        launchManager.addLaunchListener(new ILaunchesListener2()
        {
            @Override
            public void launchesAdded(ILaunch[] launches)
            {
                // Нечего делать
            }

            @Override
            public void launchesChanged(ILaunch[] launches)
            {
                // Нечего делать
            }

            @Override
            public void launchesRemoved(ILaunch[] launches)
            {
                if (Arrays.asList(launches).contains(fLaunch))
                {
//                    if (fTestRunnerClient != null)
//                    {
//                        fTestRunnerClient.stopWaiting();
//                    }
                    launchManager.removeLaunchListener(this);
                }
            }

            @Override
            public void launchesTerminated(ILaunch[] launches)
            {
                if (Arrays.asList(launches).contains(fLaunch))
                {
//                    if (fTestRunnerClient != null)
//                    {
//                        fTestRunnerClient.stopWaiting();
//                    }
                    launchManager.removeLaunchListener(this);
                }
            }
        });

        fSessionListeners = new ListenerList<>();
        addTestSessionListener(new TestRunListenerAdapter(this));
    }

    /**
     * Creates a test run session.
     *
     * @param testRunName name of the test run
     * @param project may be <code>null</code>
     */
    public TestRunSession(String testRunName, IV8Project project)
    {
        fLaunch = null;
        fConfigurationProject = project;
        fStartTime = -System.currentTimeMillis();

        Assert.isNotNull(testRunName);
        fTestRunName = testRunName;

        fTestRoot = new TestRoot(this);
        fIdToTest = new HashMap<>();

//        fTestRunnerClient = null;

        fSessionListeners = new ListenerList<>();
    }

    /**
     * @param listener
     */
    public synchronized void addTestSessionListener(ITestSessionListener listener)
    {
        swapIn();
        fSessionListeners.add(listener);
    }

    /**
     * @param parent
     * @param id
     * @param testName
     * @param isSuite
     * @param testCount
     * @param isDynamicTest
     * @param displayName
     * @param parameterTypes
     * @param uniqueId
     * @return TestElement
     */
    public TestElement createTestElement(TestSuiteElement parent, String id, String testName, boolean isSuite,
        int testCount, boolean isDynamicTest, String displayName, String[] parameterTypes, String uniqueId)
    {
        TestElement testElement;

        String[] modifiedParameterTypes = parameterTypes;
        if (parameterTypes != null && parameterTypes.length > 1)
        {
            modifiedParameterTypes = Arrays.stream(parameterTypes).map(String::trim).toArray(String[]::new);
        }

        if (isSuite)
        {
            TestSuiteElement testSuiteElement =
                new TestSuiteElement(parent, id, testName, testCount, displayName, modifiedParameterTypes, uniqueId);
            testElement = testSuiteElement;
            if (testCount > 0)
            {
                fIncompleteTestSuites.add(new IncompleteTestSuite(testSuiteElement, testCount));
            }
            else if (fFactoryTestSuites != null)
            {
                fFactoryTestSuites.add(new IncompleteTestSuite(testSuiteElement, testCount));
            }
        }
        else
        {
            testElement =
                new TestCaseElement(parent, id, testName, displayName, isDynamicTest, modifiedParameterTypes, uniqueId);
        }

        fIdToTest.put(id, testElement);

        return testElement;
    }

    /**
     * @return TestElement
     */
    public TestElement[] getAllFailedTestElements()
    {
        ArrayList<ITestElement> failures = new ArrayList<>();
        addFailures(failures, getTestRoot());
        return failures.toArray(new TestElement[failures.size()]);
    }

    /**
     * @return int
     */
    public int getAssumptionFailureCount()
    {
        return fAssumptionFailureCount;
    }

    @Override
    public ITestElement[] getChildren()
    {
        return getTestRoot().getChildren();
    }

    @Override
    public double getElapsedTimeInSeconds()
    {
        if (fTestRoot == null)
        {
            return Double.NaN;
        }

        return fTestRoot.getElapsedTimeInSeconds();
    }

    /**
     * @return int
     */
    public int getErrorCount()
    {
        return fErrorCount;
    }

    /**
     * @return int
     */
    public int getFailureCount()
    {
        return fFailureCount;
    }

    @Override
    public FailureTrace getFailureTrace()
    {
        return null;
    }

    /**
     * @return int
     */
    public int getIgnoredCount()
    {
        return fIgnoredCount;
    }

    /**
     * @return the launch, or <code>null</code> iff this session was run externally
     */
    public ILaunch getLaunch()
    {
        return fLaunch;
    }

    /*
     * @see ru.capralow.dt.unit.junit.model.ITestRunSession#getJavaProject()
     */
    @Override
    public IV8Project getLaunchedProject()
    {
        return fConfigurationProject;
    }

    @Override
    public ITestElementContainer getParentContainer()
    {
        return null;
    }

    @Override
    public ProgressState getProgressState()
    {
        if (isRunning())
        {
            return ProgressState.RUNNING;
        }
        if (isStopped())
        {
            return ProgressState.STOPPED;
        }
        return ProgressState.COMPLETED;
    }

    /**
     * @return int
     */
    public int getStartedCount()
    {
        return fStartedCount;
    }

    /**
     * @return long
     */
    public long getStartTime()
    {
        return fStartTime;
    }

    /**
     * @param id
     * @return TestElement
     */
    public TestElement getTestElement(String id)
    {
        return fIdToTest.get(id);
    }

    @Override
    public Result getTestResult(boolean includeChildren)
    {
        if (fTestRoot != null)
        {
            return fTestRoot.getTestResult(true);
        }
        else
        {
            return fTestResult;
        }
    }

    /**
     * @return TestRoot
     */
    public synchronized TestRoot getTestRoot()
    {
        swapIn();
        return fTestRoot;
    }

    @Override
    public String getTestRunName()
    {
        return fTestRunName;
    }

    @Override
    public ITestRunSession getTestRunSession()
    {
        return this;
    }

    /**
     * @return int
     */
    public int getTotalCount()
    {
        return fTotalCount;
    }

    /**
     * @return <code>true</code> iff this session has been started, but not ended nor stopped nor terminated
     */
    public boolean isRunning()
    {
        return fIsRunning;
    }

    /**
     * @return boolean
     */
    public boolean isStarting()
    {
        return getStartTime() == 0 && fLaunch != null && !fLaunch.isTerminated();
    }

    /**
     * @return <code>true</code> iff the session has been stopped or terminated
     */
    public boolean isStopped()
    {
        return fIsStopped;
    }

    /**
     * @param testElement
     * @param completed
     */
    public void registerTestEnded(TestElement testElement, boolean completed)
    {
        if (testElement instanceof TestCaseElement)
        {
            fTotalCount++;
            if (!completed)
            {
                return;
            }
            fStartedCount++;
            if (((TestCaseElement)testElement).isIgnored())
            {
                fIgnoredCount++;
            }
            if (!testElement.getStatus().isErrorOrFailure())
            {
                setStatus(testElement, Status.OK);
            }
        }

        if (testElement.isAssumptionFailure())
        {
            fAssumptionFailureCount++;
        }
    }

    /**
     * @param testElement
     * @param status
     * @param trace
     * @param expected
     * @param actual
     */
    public void registerTestFailureStatus(TestElement testElement, Status status, String trace, String expected,
        String actual)
    {
        testElement.setStatus(status, trace, expected, actual);
        if (!testElement.isAssumptionFailure())
        {
            if (status.isError())
            {
                fErrorCount++;
            }
            else if (status.isFailure())
            {
                fFailureCount++;
            }
        }
    }

    /**
     *
     */
    public void removeSwapFile()
    {
        File swapFile = getSwapFile();
        if (swapFile.exists())
        {
            swapFile.delete();
        }
    }

    /**
     * @param listener
     */
    public void removeTestSessionListener(ITestSessionListener listener)
    {
        fSessionListeners.remove(listener);
    }

    /**
     *
     */
    public void stopTestRun()
    {
        if (isRunning())
        {
            fIsStopped = true;
        }
//        if (fTestRunnerClient != null)
//            fTestRunnerClient.stopTest();
    }

    /**
     *
     */
    public synchronized void swapIn()
    {
        if (fTestRoot != null)
        {
            return;
        }

        try
        {
            JUnitModel.importIntoTestRunSession(getSwapFile(), this);
        }
        catch (IllegalStateException | CoreException e)
        {
            JUnitPlugin.log(e);
            fTestRoot = new TestRoot(this);
            fTestResult = null;
        }
    }

    /**
     *
     */
    public synchronized void swapOut()
    {
        if (fTestRoot == null)
        {
            return;
        }

        for (ITestSessionListener registered : fSessionListeners)
        {
            if (!registered.acceptsSwapToDisk())
            {
                return;
            }
        }

        try
        {
            File swapFile = getSwapFile();

            JUnitModel.exportTestRunSession(this, swapFile);
            fTestResult = fTestRoot.getTestResult(true);
            fTestRoot = null;
//            fTestRunnerClient = null;
            fIdToTest = new HashMap<>();
            fIncompleteTestSuites = null;
            fFactoryTestSuites = null;
            fUnrootedSuite = null;

        }
        catch (IllegalStateException | CoreException e)
        {
            JUnitPlugin.log(e);
        }
    }

    @Override
    public String toString()
    {
        return fTestRunName + " " + DateFormat.getDateTimeInstance().format(new Date(fStartTime)); //$NON-NLS-1$
    }

    private void addFailures(ArrayList<ITestElement> failures, ITestElement testElement)
    {
        Result testResult = testElement.getTestResult(true);
        if (testResult == Result.ERROR || testResult == Result.FAILURE)
        {
            failures.add(testElement);
        }
        if (testElement instanceof TestSuiteElement)
        {
            TestSuiteElement testSuiteElement = (TestSuiteElement)testElement;
            ITestElement[] children = testSuiteElement.getChildren();
            for (ITestElement child : children)
            {
                addFailures(failures, child);
            }
        }
    }

    private TestElement addTreeEntry(String treeEntry)
    {
        // format: "testId","testName","isSuite","testcount","isDynamicTest",
        // "parentId","displayName","parameterTypes","uniqueId"
        int index0 = treeEntry.indexOf(',');
        String id = treeEntry.substring(0, index0);

        StringBuffer testNameBuffer = new StringBuffer(100);
        int index1 = scanTestName(treeEntry, index0 + 1, testNameBuffer);
        String testName = testNameBuffer.toString().trim();

        int index2 = treeEntry.indexOf(',', index1 + 1);
        boolean isSuite = treeEntry.substring(index1 + 1, index2).equals("true"); //$NON-NLS-1$

        int testCount;
        boolean isDynamicTest;
        String parentId;
        String displayName;
        StringBuffer displayNameBuffer = new StringBuffer(100);
        String[] parameterTypes;
        StringBuffer parameterTypesBuffer = new StringBuffer(200);
        String uniqueId;
        StringBuffer uniqueIdBuffer = new StringBuffer(200);
        int index3 = treeEntry.indexOf(',', index2 + 1);
        if (index3 == -1)
        {
            testCount = Integer.parseInt(treeEntry.substring(index2 + 1));
            isDynamicTest = false;
            parentId = null;
            displayName = null;
            parameterTypes = null;
            uniqueId = null;
        }
        else
        {
            testCount = Integer.parseInt(treeEntry.substring(index2 + 1, index3));

            int index4 = treeEntry.indexOf(',', index3 + 1);
            isDynamicTest = treeEntry.substring(index3 + 1, index4).equals("true"); //$NON-NLS-1$

            int index5 = treeEntry.indexOf(',', index4 + 1);
            parentId = treeEntry.substring(index4 + 1, index5);
            if (parentId.equals("-1")) //$NON-NLS-1$
            {
                parentId = null;
            }

            int index6 = scanTestName(treeEntry, index5 + 1, displayNameBuffer);
            displayName = displayNameBuffer.toString().trim();
            if (displayName.equals(testName))
            {
                displayName = null;
            }

            int index7 = scanTestName(treeEntry, index6 + 1, parameterTypesBuffer);
            String parameterTypesString = parameterTypesBuffer.toString().trim();
            if (parameterTypesString.isEmpty())
            {
                parameterTypes = null;
            }
            else
            {
                parameterTypes = parameterTypesString.split(","); //$NON-NLS-1$
                Arrays.parallelSetAll(parameterTypes, i -> parameterTypes[i].trim());
            }

            scanTestName(treeEntry, index7 + 1, uniqueIdBuffer);
            uniqueId = uniqueIdBuffer.toString().trim();
            if (uniqueId.isEmpty())
            {
                uniqueId = null;
            }
        }

        if (isDynamicTest)
        {
            if (parentId != null)
            {
                for (IncompleteTestSuite suite : fFactoryTestSuites)
                {
                    if (parentId.equals(suite.fTestSuiteElement.getId()))
                    {
                        return createTestElement(suite.fTestSuiteElement, id, testName, isSuite, testCount,
                            isDynamicTest, displayName, parameterTypes, uniqueId);
                    }
                }
            }
            return createTestElement(getUnrootedSuite(), id, testName, isSuite, testCount, isDynamicTest, displayName,
                parameterTypes, uniqueId); // should not reach here
        }
        else
        {
            if (fIncompleteTestSuites.isEmpty())
            {
                return createTestElement(fTestRoot, id, testName, isSuite, testCount, isDynamicTest, displayName,
                    parameterTypes, uniqueId);
            }
            else
            {
                int suiteIndex = fIncompleteTestSuites.size() - 1;
                IncompleteTestSuite openSuite = fIncompleteTestSuites.get(suiteIndex);
                openSuite.fOutstandingChildren--;
                if (openSuite.fOutstandingChildren <= 0)
                {
                    fIncompleteTestSuites.remove(suiteIndex);
                }
                return createTestElement(openSuite.fTestSuiteElement, id, testName, isSuite, testCount, isDynamicTest,
                    displayName, parameterTypes, uniqueId);
            }
        }
    }

    private File getSwapFile() throws IllegalStateException
    {
        File historyDir = JUnitPlugin.getHistoryDirectory();
        String isoTime = new SimpleDateFormat("yyyyMMdd-HHmmss.SSS").format(new Date(getStartTime())); //$NON-NLS-1$
        String swapFileName = isoTime + ".xml"; //$NON-NLS-1$
        return new File(historyDir, swapFileName);
    }

    private TestSuiteElement getUnrootedSuite()
    {
        if (fUnrootedSuite == null)
        {
            fUnrootedSuite =
                (TestSuiteElement)createTestElement(fTestRoot, "-2", Messages.TestRunSession_unrootedTests, true, //$NON-NLS-1$
                    0, false, Messages.TestRunSession_unrootedTests, null, null);
        }
        return fUnrootedSuite;
    }

    /**
     * Append the test name from <code>s</code> to <code>testName</code>.
     *
     * @param s the string to scan
     * @param start the offset of the first character in <code>s</code>
     * @param testName the result
     *
     * @return the index of the next ','
     */
    private int scanTestName(String s, int start, StringBuffer testName)
    {
        boolean inQuote = false;
        int i = start;
        for (; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if (c == '\\' && !inQuote)
            {
                inQuote = true;
                continue;
            }
            else if (inQuote)
            {
                inQuote = false;
                testName.append(c);
            }
            else if (c == ',')
            {
                break;
            }
            else
            {
                testName.append(c);
            }
        }
        return i;
    }

    private void setStatus(TestElement testElement, Status status)
    {
        testElement.setStatus(status);
    }

    void reset()
    {
        fStartedCount = 0;
        fFailureCount = 0;
        fAssumptionFailureCount = 0;
        fErrorCount = 0;
        fIgnoredCount = 0;
        fTotalCount = 0;

        fTestRoot = new TestRoot(this);
        fTestResult = null;
        fIdToTest = new HashMap<>();
    }

    private static class IncompleteTestSuite
    {
        public TestSuiteElement fTestSuiteElement;
        public int fOutstandingChildren;

        IncompleteTestSuite(TestSuiteElement testSuiteElement, int outstandingChildren)
        {
            fTestSuiteElement = testSuiteElement;
            fOutstandingChildren = outstandingChildren;
        }
    }

    /**
     * An {@link ITestRunListener2} that listens to events from the
     * {@link RemoteTestRunnerClient} and translates them into high-level model
     * events (broadcasted to {@link ITestSessionListener}s).
     */
    private class TestSessionNotifier
        implements ITestRunListener2
    {

        @Override
        public void testEnded(String testId, String testName)
        {
            TestElement testElement = getTestElement(testId);
            if (testElement == null)
            {
                testElement = createUnrootedTestElement(testId, testName);
            }
            else if (!(testElement instanceof TestCaseElement))
            {
                logUnexpectedTest(testId, testElement);
                return;
            }
            TestCaseElement testCaseElement = (TestCaseElement)testElement;

            if (testCaseElement.getStatus() == Status.RUNNING)
            {
                setStatus(testCaseElement, Status.OK);
            }

            for (ITestSessionListener listener : fSessionListeners)
            {
                listener.testEnded(testCaseElement);
            }
        }

        @Override
        public void testFailed(int statusCode, String testId, String testName, String trace, String expected,
            String actual)
        {
            TestElement testElement = getTestElement(testId);
            if (testElement == null)
            {
                testElement = createUnrootedTestElement(testId, testName);
            }

            Status status;
            if (testName.startsWith("Assumption failed"))
            {
                testElement.setAssumptionFailed(true);
                fAssumptionFailureCount++;
                status = Status.OK;
            }
            else
            {
                status = Status.convert(statusCode);
            }

            registerTestFailureStatus(testElement, status, trace, expected, actual);

            for (ITestSessionListener listener : fSessionListeners)
            {
                listener.testFailed(testElement, status, trace, expected, actual);
            }
        }

        @Override
        public void testReran(String testId, String className, String testName, int statusCode, String trace,
            String expectedResult, String actualResult)
        {
            TestElement testElement = getTestElement(testId);
            if (testElement == null)
            {
                testElement = createUnrootedTestElement(testId, testName);
            }
            else if (!(testElement instanceof TestCaseElement))
            {
                logUnexpectedTest(testId, testElement);
                return;
            }
            TestCaseElement testCaseElement = (TestCaseElement)testElement;

            Status status = Status.convert(statusCode);
            registerTestFailureStatus(testElement, status, trace, expectedResult, actualResult);

            for (ITestSessionListener listener : fSessionListeners)
            {
                listener.testReran(testCaseElement, status, trace, expectedResult, actualResult);
            }
        }

        @Override
        public void testRunEnded(long elapsedTime)
        {
            fIsRunning = false;

            for (ITestSessionListener listener : fSessionListeners)
            {
                listener.sessionEnded(elapsedTime);
            }
        }

        @Override
        public void testRunStarted(int testCount)
        {
            fIncompleteTestSuites = new ArrayList<>();
            fFactoryTestSuites = new ArrayList<>();

            fStartedCount = 0;
            fIgnoredCount = 0;
            fFailureCount = 0;
            fAssumptionFailureCount = 0;
            fErrorCount = 0;
            fTotalCount = testCount;

            fStartTime = System.currentTimeMillis();
            fIsRunning = true;

            for (ITestSessionListener listener : fSessionListeners)
            {
                listener.sessionStarted();
            }
        }

        @Override
        public void testRunStopped(long elapsedTime)
        {
            fIsRunning = false;
            fIsStopped = true;

            for (ITestSessionListener listener : fSessionListeners)
            {
                listener.sessionStopped(elapsedTime);
            }
        }

        @Override
        public void testRunTerminated()
        {
            fIsRunning = false;
            fIsStopped = true;

            for (ITestSessionListener listener : fSessionListeners)
            {
                listener.sessionTerminated();
            }
        }

        @Override
        public void testStarted(String testId, String testName)
        {
            if (fStartedCount == 0)
            {
                for (ITestSessionListener listener : fSessionListeners)
                {
                    listener.runningBegins();
                }
            }
            TestElement testElement = getTestElement(testId);
            if (testElement == null)
            {
                testElement = createUnrootedTestElement(testId, testName);
            }
            else if (!(testElement instanceof TestCaseElement))
            {
                logUnexpectedTest(testId, testElement);
                return;
            }
            TestCaseElement testCaseElement = (TestCaseElement)testElement;
            setStatus(testCaseElement, Status.RUNNING);

            if (testCaseElement.isDynamicTest())
            {
                fTotalCount++;
            }

            fStartedCount++;

            for (ITestSessionListener listener : fSessionListeners)
            {
                listener.testStarted(testCaseElement);
            }
        }

        @Override
        public void testTreeEntry(String description)
        {
            TestElement testElement = addTreeEntry(description);

            for (ITestSessionListener listener : fSessionListeners)
            {
                listener.testAdded(testElement);
            }
        }

        private TestElement createUnrootedTestElement(String testId, String testName)
        {
            TestSuiteElement unrootedSuite = getUnrootedSuite();
            TestElement testElement =
                createTestElement(unrootedSuite, testId, testName, false, 1, false, testName, null, null);

            for (ITestSessionListener listener : fSessionListeners)
            {
                listener.testAdded(testElement);
            }

            return testElement;
        }

        private void logUnexpectedTest(String testId, TestElement testElement)
        {
            JUnitPlugin.log(new Exception("Unexpected TestElement type for testId '" + testId + "': " + testElement)); //$NON-NLS-2$
        }
    }
}
