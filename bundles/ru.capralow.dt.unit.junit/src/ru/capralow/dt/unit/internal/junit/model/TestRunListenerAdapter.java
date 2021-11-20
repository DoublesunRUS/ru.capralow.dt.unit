/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.model;

import org.eclipse.core.runtime.ListenerList;

import ru.capralow.dt.unit.internal.junit.JUnitPlugin;
import ru.capralow.dt.unit.internal.junit.model.TestElement.Status;
import ru.capralow.dt.unit.junit.TestRunListener;
import ru.capralow.dt.unit.junit.model.ITestCaseElement;

/**
 * Notifier for the callback listener API {@link TestRunListener}.
 */
public class TestRunListenerAdapter
    implements ITestSessionListener
{

    private final TestRunSession fSession;

    /**
     * @param session
     */
    public TestRunListenerAdapter(TestRunSession session)
    {
        fSession = session;
    }

    @Override
    public boolean acceptsSwapToDisk()
    {
        return true;
    }

    @Override
    public void runningBegins()
    {
        fireSessionStarted();
    }

    @Override
    public void sessionEnded(long elapsedTime)
    {
        fireSessionFinished();
        fSession.swapOut();
    }

    @Override
    public void sessionStarted()
    {
        // wait until all test are added
    }

    @Override
    public void sessionStopped(long elapsedTime)
    {
        fireSessionFinished();
        fSession.swapOut();
    }

    @Override
    public void sessionTerminated()
    {
        fSession.swapOut();
    }

    @Override
    public void testAdded(TestElement testElement)
    {
        // do nothing
    }

    @Override
    public void testEnded(TestCaseElement testCaseElement)
    {
        fireTestCaseFinished(testCaseElement);
    }

    @Override
    public void testFailed(TestElement testElement, Status status, String trace, String expected, String actual)
    {
        // ignore
    }

    @Override
    public void testReran(TestCaseElement testCaseElement, Status status, String trace, String expectedResult,
        String actualResult)
    {
        // ignore
    }

    @Override
    public void testStarted(TestCaseElement testCaseElement)
    {
        fireTestCaseStarted(testCaseElement);
    }

    private void fireSessionFinished()
    {
        for (TestRunListener listener : getListenerList())
        {
            listener.sessionFinished(fSession);
        }
    }

    private void fireSessionStarted()
    {
        for (TestRunListener listener : getListenerList())
        {
            listener.sessionStarted(fSession);
        }
    }

    private void fireTestCaseFinished(ITestCaseElement testCaseElement)
    {
        for (TestRunListener listener : getListenerList())
        {
            listener.testCaseFinished(testCaseElement);
        }
    }

    private void fireTestCaseStarted(ITestCaseElement testCaseElement)
    {
        for (TestRunListener listener : getListenerList())
        {
            listener.testCaseStarted(testCaseElement);
        }
    }

    private ListenerList<TestRunListener> getListenerList()
    {
        return JUnitPlugin.getInstance().getNewTestRunListeners();
    }
}
