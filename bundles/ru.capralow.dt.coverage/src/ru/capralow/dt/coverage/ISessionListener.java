/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage;

/**
 * Callback interface for changes of the session manager. This interface is
 * intended to be implemented by clients that want to get notifications.
 *
 * @see ISessionManager#addSessionListener(ISessionListener)
 * @see ISessionManager#removeSessionListener(ISessionListener)
 */
public interface ISessionListener
{

    /**
     * Called when a new session has been activated or the last session has been
     * removed. In this case <code>null</code> is passed as a parameter.
     *
     * @param session activated session or <code>null</code>
     */
    void sessionActivated(ICoverageSession session);

    /**
     * Called when a session has been added.
     *
     * @param addedSession added session
     */
    void sessionAdded(ICoverageSession addedSession);

    /**
     * Called when a session has been removed.
     *
     * @param removedSession removes session
     */
    void sessionRemoved(ICoverageSession removedSession);

}
