/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal.launching;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com._1c.g5.v8.dt.profiling.core.IProfilingResult;
import com._1c.g5.v8.dt.profiling.core.IProfilingResultListener;
import com._1c.g5.v8.dt.profiling.core.IProfilingService;

import ru.capralow.dt.coverage.ICorePreferences;
import ru.capralow.dt.coverage.ICoverageSession;
import ru.capralow.dt.coverage.ISessionManager;
import ru.capralow.dt.coverage.internal.CoverageCorePlugin;
import ru.capralow.dt.coverage.internal.CoverageSession;
import ru.capralow.dt.coverage.launching.ICoverageLaunch;

public class AgentServer
    implements IProfilingResultListener
{

    private ICoverageLaunch launch;
    private ISessionManager sessionManager;
    private ICorePreferences preferences;

    private IProfilingService profilingService;

    public AgentServer(ICoverageLaunch launch, ISessionManager sessionManager, ICorePreferences preferences)
    {
        this.preferences = preferences;
        this.launch = launch;
        this.sessionManager = sessionManager;
        this.profilingService = CoverageCorePlugin.getInstance().getInjector().getInstance(IProfilingService.class);
    }

    @Override
    public void resultRenamed(IProfilingResult profilingResult, String newName)
    {
        // Нечего делать
    }

    @Override
    public void resultsCleared()
    {
        // Нечего делать
    }

    @Override
    public void resultsUpdated(IProfilingResult profilingResult)
    {
        ICoverageSession session = sessionManager.getSessionByName(profilingResult.getName());

        if (session == null)
        {
            session = new CoverageSession(profilingResult, launch.getScope(), launch.getLaunchConfiguration());

            sessionManager.addSession(session, preferences.getActivateNewSessions(), launch);
        }

        sessionManager.refreshActiveSession();

        Display.getDefault().asyncExec(() -> {
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            IViewPart view = page.findView("com._1c.g5.v8.dt.profiling.ui.view.ProfilingView"); //$NON-NLS-1$
            if (view != null)
                page.hideView(view);
        });
    }

    public void start()
    {
        profilingService.toggleTargetWaitingState(true);
    }

    public void stop()
    {
        profilingService.toggleTargetWaitingState(false);
    }
}
