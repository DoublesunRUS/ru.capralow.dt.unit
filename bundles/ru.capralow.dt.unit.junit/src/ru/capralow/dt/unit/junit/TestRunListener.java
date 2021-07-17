package ru.capralow.dt.unit.junit;

import ru.capralow.dt.unit.junit.model.ITestCaseElement;
import ru.capralow.dt.unit.junit.model.ITestRunSession;

/**
 * A test run listener that can be registered at {@link JUnitCore#addTestRunListener(TestRunListener)}
 * or as a contribution to the <code>org.eclipse.jdt.junit.testRunListeners</code> extension point.
 * The latter approach has the advantage that the contributing plug-in is automatically loaded when a test run starts.
 * <p>
 * A test run starts with the call to {@link #sessionLaunched(ITestRunSession)} and
 * {@link #sessionStarted(ITestRunSession)}, followed by calls to
 * {@link #testCaseStarted(ITestCaseElement)} and {@link #testCaseFinished(ITestCaseElement)}
 * for all test cases contained in the tree.
 * </p>
 * <p>
 * A test run session is ended with the call to {@link #sessionFinished(ITestRunSession)}. After that
 * call, no references must be kept to the session or any of the test cases or suites.
 * </p>
 *
 * @since 3.3
 */
public abstract class TestRunListener
{

    /**
     * A test run session has finished. The test tree can be accessed through the session element.
     *
     * <p>
     * Important: The implementor of this method must not keep the session element when the method is finished.
     * </p>
     *
     * @param session the test
     */
    public void sessionFinished(ITestRunSession session)
    {
        // Abstract
    }

    /**
     * A test run session has been launched. The test tree is not available yet.
     * <p>
     * Important: The implementor of this method must not keep a reference to the session element
     * after {@link #sessionFinished(ITestRunSession)} has finished.
     * </p>
     *
     * @param session the session that has just been launched
     */
    public void sessionLaunched(ITestRunSession session)
    {
        // Abstract
    }

    /**
     * A test run session has started. The test tree can be accessed through the session element.
     * <p>
     * Important: The implementor of this method must not keep a reference to the session element
     * after {@link #sessionFinished(ITestRunSession)} has finished.
     * </p>
     *
     * @param session the session that has just started.
     */
    public void sessionStarted(ITestRunSession session)
    {
        // Abstract
    }

    /**
     * A test case has ended. The result can be accessed from the element.
     * <p>
     * Important: The implementor of this method must not keep a reference to the test case element
     * after {@link #sessionFinished(ITestRunSession)} has finished.
     * </p>
     *
     * @param testCaseElement the test that has finished running
     */
    public void testCaseFinished(ITestCaseElement testCaseElement)
    {
        // Abstract
    }

    /**
     * A test case has started. The result can be accessed from the element.
     * <p>
     * Important: The implementor of this method must not keep a reference to the test case element
     * after {@link #sessionFinished(ITestRunSession)} has finished.
     * </p>
     * @param testCaseElement the test that has started to run
     */
    public void testCaseStarted(ITestCaseElement testCaseElement)
    {
        // Abstract
    }
}
