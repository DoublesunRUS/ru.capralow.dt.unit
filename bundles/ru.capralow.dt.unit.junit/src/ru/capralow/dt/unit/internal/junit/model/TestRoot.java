package ru.capralow.dt.unit.internal.junit.model;

import ru.capralow.dt.unit.junit.model.ITestRunSession;

public class TestRoot
    extends TestSuiteElement
{

    private final ITestRunSession fSession;

    public TestRoot(ITestRunSession session)
    {
        super(null, "-1", session.getTestRunName(), 1, session.getTestRunName(), null, null); //$NON-NLS-1$
        fSession = session;
    }

    @Override
    public TestRoot getRoot()
    {
        return this;
    }

    @Override
    public ITestRunSession getTestRunSession()
    {
        return fSession;
    }
}
