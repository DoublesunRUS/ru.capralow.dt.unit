/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal.launching;

import java.util.Set;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.Launch;
import org.eclipse.emf.common.util.URI;

import com._1c.g5.v8.dt.profiling.core.IProfilingService;

import ru.capralow.dt.coverage.CoverageTools;
import ru.capralow.dt.coverage.internal.CoverageCorePlugin;
import ru.capralow.dt.coverage.launching.ICoverageLaunch;

/**
 * Implementation of {@link ICoverageLaunch}.
 */
public class CoverageLaunch
    extends Launch
    implements ICoverageLaunch
{

    private Set<URI> scope;
    private AgentServer agentServer;

    public CoverageLaunch(ILaunchConfiguration launchConfiguration, Set<URI> set)
    {
        super(launchConfiguration, CoverageTools.LAUNCH_MODE, null);
        this.scope = set;
        CoverageCorePlugin plugin = CoverageCorePlugin.getInstance();
        this.agentServer = new AgentServer(this, plugin.getSessionManager(), plugin.getPreferences());

        IProfilingService profilingService =
            CoverageCorePlugin.getInstance().getInjector().getInstance(IProfilingService.class);
        profilingService.addProfilingResultsListener(agentServer);
    }

    public AgentServer getAgentServer()
    {
        return agentServer;
    }

    // ICoverageLaunch interface

    @Override
    public Set<URI> getScope()
    {
        return scope;
    }

}
