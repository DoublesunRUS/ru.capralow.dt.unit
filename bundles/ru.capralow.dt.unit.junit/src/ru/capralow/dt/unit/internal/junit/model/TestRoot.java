/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.model;

import ru.capralow.dt.unit.junit.model.ITestRunSession;

/**
 * @author Aleksandr Kapralov
 *
 */
public class TestRoot
    extends TestSuiteElement
{

    private final ITestRunSession fSession;

    /**
     * @param session
     */
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
