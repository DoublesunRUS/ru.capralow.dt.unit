/**
 * Copyright (c) 2020, Aleksandr Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import ru.capralow.dt.coverage.CoverageTools;

/**
 * Handler to remove all coverage sessions.
 */
public class RemoveAllSessionsHandler
    extends AbstractSessionManagerHandler
{

    public RemoveAllSessionsHandler()
    {
        super(CoverageTools.getSessionManager());
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        sessionManager.removeAllSessions();
        return null;
    }

    @Override
    public boolean isEnabled()
    {
        return !sessionManager.getSessions().isEmpty();
    }

}
