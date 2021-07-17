package ru.capralow.dt.unit.internal.junit.model;

import ru.capralow.dt.unit.internal.junit.model.TestElement.Status;

/**
 * A listener interface for observing the execution of a test session (initial run and reruns).
 */
public interface ITestSessionListener
{
    /**
     * @return <code>true</code> if the test run session can be swapped to disk although
     * this listener is still installed
     */
    boolean acceptsSwapToDisk();

    /**
     * All test have been added and running begins
     */
    void runningBegins();

    /**
     * A test run has ended.
     *
     * @param elapsedTime the total elapsed time of the test run
     */
    void sessionEnded(long elapsedTime);

    /**
     * A test run has started.
     */
    void sessionStarted();

    /**
     * A test run has been stopped prematurely.
     *
     * @param elapsedTime the time elapsed before the test run was stopped
     */
    void sessionStopped(long elapsedTime);

    /**
     * The VM instance performing the tests has terminated.
     */
    void sessionTerminated();

    /**
     * A test has been added to the plan.
     *
     * @param testElement the test
     */
    void testAdded(TestElement testElement);

    /**
     * An individual test has ended.
     *
     * @param testCaseElement the test
     */
    void testEnded(TestCaseElement testCaseElement);

    /**
     * An individual test has failed with a stack trace.
     *
     * @param testElement the test
     * @param status the outcome of the test; one of
     * {@link TestElement.Status#ERROR} or
     * {@link TestElement.Status#FAILURE}
     * @param trace the stack trace
     * @param expected expected value
     * @param actual actual value
     */
    void testFailed(TestElement testElement, Status status, String trace, String expected, String actual);

    /**
     * An individual test has been rerun.
     *
     * @param testCaseElement the test
     * @param status the outcome of the test that was rerun; one of
     * {@link TestElement.Status#OK}, {@link TestElement.Status#ERROR}, or {@link TestElement.Status#FAILURE}
     * @param trace the stack trace in the case of abnormal termination,
     * or the empty string if none
     * @param expectedResult expected value
     * @param actualResult actual value
     */
    void testReran(TestCaseElement testCaseElement, Status status, String trace, String expectedResult,
        String actualResult);

    /**
     * An individual test has started.
     *
     * @param testCaseElement the test
     */
    void testStarted(TestCaseElement testCaseElement);

}
