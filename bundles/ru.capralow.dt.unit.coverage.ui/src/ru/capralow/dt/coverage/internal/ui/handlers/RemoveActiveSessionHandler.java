/**
 * Copyright (c) 2020, Aleksandr Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import ru.capralow.dt.coverage.CoverageTools;
import ru.capralow.dt.coverage.ICoverageSession;

/**
 * Handler to remove the currently active coverage session.
 */
public class RemoveActiveSessionHandler
    extends AbstractSessionManagerHandler
{

    public RemoveActiveSessionHandler()
    {
        super(CoverageTools.getSessionManager());
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        final ICoverageSession session = sessionManager.getActiveSession();
        sessionManager.removeSession(session);
        return null;
    }

    @Override
    public boolean isEnabled()
    {
        return sessionManager.getActiveSession() != null;
    }

}
