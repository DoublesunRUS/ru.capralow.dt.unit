/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.DebugUITools;

import ru.capralow.dt.coverage.CoverageTools;
import ru.capralow.dt.coverage.ICoverageSession;

/**
 * Handler to re-launch the currently active coverage session.
 */
public class RelaunchSessionHandler
    extends AbstractSessionManagerHandler
{

    public RelaunchSessionHandler()
    {
        super(CoverageTools.getSessionManager());
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        final ICoverageSession session = sessionManager.getActiveSession();
        final ILaunchConfiguration config = session.getLaunchConfiguration();
        DebugUITools.launch(config, CoverageTools.LAUNCH_MODE);
        return null;
    }

    @Override
    public boolean isEnabled()
    {
        final ICoverageSession session = sessionManager.getActiveSession();
        return session != null && session.getLaunchConfiguration() != null;
    }

}
