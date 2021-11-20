/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.model;

/**
 * @author Aleksandr Kapralov
 *
 */
public interface ITestRunSessionListener
{

    /**
     * @param testRunSession the new session, or <code>null</code>
     */
    void sessionAdded(TestRunSession testRunSession);

    /**
     * @param testRunSession
     */
    void sessionRemoved(TestRunSession testRunSession);

}
