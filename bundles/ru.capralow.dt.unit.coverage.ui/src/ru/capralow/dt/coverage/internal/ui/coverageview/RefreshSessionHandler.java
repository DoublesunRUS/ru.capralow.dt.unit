/**
 * Copyright (c) 2020, Aleksandr Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.coverageview;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import ru.capralow.dt.coverage.ISessionManager;
import ru.capralow.dt.coverage.internal.ui.handlers.AbstractSessionManagerHandler;

/**
 * This handler reloads the active coverage session.
 */
class RefreshSessionHandler
    extends AbstractSessionManagerHandler
{

    RefreshSessionHandler(ISessionManager sessionManager)
    {
        super(sessionManager);
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        sessionManager.refreshActiveSession();
        return null;
    }

    @Override
    public boolean isEnabled()
    {
        return sessionManager.getActiveSession() != null;
    }

}
