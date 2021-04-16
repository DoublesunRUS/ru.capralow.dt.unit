/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.HandlerEvent;

import ru.capralow.dt.coverage.ICoverageSession;
import ru.capralow.dt.coverage.ISessionListener;
import ru.capralow.dt.coverage.ISessionManager;

/**
 * Abstract base for handlers that need a {@link ISessionManager} instance.
 */
public abstract class AbstractSessionManagerHandler
    extends AbstractHandler
    implements ISessionListener
{

    protected final ISessionManager sessionManager;

    protected AbstractSessionManagerHandler(ISessionManager sessionManager)
    {
        this.sessionManager = sessionManager;
        sessionManager.addSessionListener(this);
    }

    @Override
    public void dispose()
    {
        sessionManager.removeSessionListener(this);
    }

    @Override
    public void sessionActivated(ICoverageSession session)
    {
        fireEnabledChanged();
    }

    @Override
    public void sessionAdded(ICoverageSession addedSession)
    {
        fireEnabledChanged();
    }

    @Override
    public void sessionRemoved(ICoverageSession removedSession)
    {
        fireEnabledChanged();
    }

    private void fireEnabledChanged()
    {
        fireHandlerChanged(new HandlerEvent(this, true, false));
    }

}
