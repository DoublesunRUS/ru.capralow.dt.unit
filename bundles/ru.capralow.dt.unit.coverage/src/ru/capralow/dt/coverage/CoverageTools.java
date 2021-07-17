/**
 * Copyright (c) 2020, Aleksandr Kapralov
 */
package ru.capralow.dt.coverage;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.jacoco.core.analysis.ICoverageNode;

import ru.capralow.dt.coverage.analysis.IBslCoverageListener;
import ru.capralow.dt.coverage.analysis.IBslModelCoverage;
import ru.capralow.dt.coverage.internal.CoverageCorePlugin;
import ru.capralow.dt.coverage.internal.SessionExporter;
import ru.capralow.dt.coverage.internal.SessionImporter;
import ru.capralow.dt.coverage.launching.ICoverageLaunch;

/**
 * For central access to the tools provided by the coverage core plug-in this
 * class offers several static methods.
 */
public final class CoverageTools
{

    /**
     * The launch mode used for coverage sessions.
     */
    public static final String LAUNCH_MODE = "coverage"; //$NON-NLS-1$

    public static void addBslCoverageListener(IBslCoverageListener l)
    {
        CoverageCorePlugin.getInstance().getBslCoverageLoader().addBslCoverageListener(l);
    }

    public static IBslModelCoverage getBslModelCoverage()
    {
        return CoverageCorePlugin.getInstance().getBslCoverageLoader().getBslModelCoverage();
    }

    /**
     * Convenience method that tries to adapt the given object to ICoverageNode,
     * i.e. find coverage information from the active session.
     *
     * @param object Object to adapt
     * @return adapter or <code>null</code>
     */
    public static ICoverageNode getCoverageInfo(Object object)
    {
        if (object instanceof IAdaptable)
            return ((IAdaptable)object).getAdapter(ICoverageNode.class);

        IAdapterManager manager = Platform.getAdapterManager();
        return manager.getAdapter(object, ICoverageNode.class);
    }

    public static ISessionExporter getExporter(ICoverageSession session)
    {
        return new SessionExporter(session);
    }

    public static ISessionImporter getImporter()
    {
        return new SessionImporter(getSessionManager());
    }

    /**
     * Determines all current coverage launches which are running.
     *
     * @return list of running coverage launches
     */
    public static List<ICoverageLaunch> getRunningCoverageLaunches()
    {
        List<ICoverageLaunch> result = new ArrayList<>();
        for (ILaunch launch : DebugPlugin.getDefault().getLaunchManager().getLaunches())
        {
            if (launch instanceof ICoverageLaunch && !launch.isTerminated())
            {
                result.add((ICoverageLaunch)launch);
            }
        }
        return result;
    }

    /**
     * Returns the global session manager.
     *
     * @return global session manager
     */
    public static ISessionManager getSessionManager()
    {
        return CoverageCorePlugin.getInstance().getSessionManager();
    }

    public static void removeBslCoverageListener(IBslCoverageListener l)
    {
        CoverageCorePlugin.getInstance().getBslCoverageLoader().removeBslCoverageListener(l);
    }

    /**
     * Sets a {@link ICorePreferences} instance which will be used by the EclEmma
     * core to query preference settings if required.
     *
     * @param preferences callback object for preference settings
     */
    public static void setPreferences(ICorePreferences preferences)
    {
        CoverageCorePlugin.getInstance().setPreferences(preferences);
    }

    private CoverageTools()
    {
        // no instances
    }

}
