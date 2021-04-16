/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.launching;

import java.util.Collections;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate2;
import org.eclipse.osgi.util.NLS;

import ru.capralow.dt.coverage.CoverageStatus;
import ru.capralow.dt.coverage.ScopeUtils;
import ru.capralow.dt.coverage.internal.CoreMessages;
import ru.capralow.dt.coverage.internal.launching.AgentServer;
import ru.capralow.dt.coverage.internal.launching.CoverageLaunch;

/**
 * Abstract base class for coverage mode launchers. Coverage launchers perform
 * adjust the launch configuration to inject the JaCoCo coverage agent and then
 * delegate to the corresponding launcher responsible for the "debug" mode.
 */
public abstract class CoverageLauncher
    implements ICoverageLauncher, IExecutableExtension
{

    /** Launch mode for the launch delegates used internally. */
    public static final String DELEGATELAUNCHMODE = ILaunchManager.DEBUG_MODE;

    private static ILaunchConfigurationDelegate getLaunchDelegate(String launchtype) throws CoreException
    {
        ILaunchConfigurationType type =
            DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType(launchtype);
        if (type == null)
        {
            throw new CoreException(CoverageStatus.UNKOWN_LAUNCH_TYPE_ERROR.getStatus(launchtype));
        }
        return type.getDelegates(Collections.singleton(DELEGATELAUNCHMODE))[0].getDelegate();
    }

    protected ILaunchConfigurationDelegate launchdelegate;

    // IExecutableExtension interface:

    protected ILaunchConfigurationDelegate2 launchdelegate2;

    @Override
    public boolean buildForLaunch(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor)
        throws CoreException
    {
        if (launchdelegate2 == null)
            return true;

        return launchdelegate2.buildForLaunch(configuration, DELEGATELAUNCHMODE, monitor);
    }

    // ILaunchConfigurationDelegate interface:

    @Override
    public boolean finalLaunchCheck(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor)
        throws CoreException
    {
        if (launchdelegate2 == null)
            return true;

        return launchdelegate2.finalLaunchCheck(configuration, DELEGATELAUNCHMODE, monitor);
    }

    // ILaunchConfigurationDelegate2 interface:

    @Override
    public ILaunch getLaunch(ILaunchConfiguration configuration, String mode) throws CoreException
    {
        return new CoverageLaunch(configuration, ScopeUtils.getConfiguredScope(configuration));
    }

    @Override
    public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
        throws CoreException
    {
        monitor.beginTask(NLS.bind(CoreMessages.Launching_task, configuration.getName()), 2);
        if (monitor.isCanceled())
        {
            return;
        }

        // Start agent server
        CoverageLaunch coverageLaunch = (CoverageLaunch)launch;
        AgentServer server = coverageLaunch.getAgentServer();
        server.start();

        launchdelegate.launch(configuration, DELEGATELAUNCHMODE, launch, new SubProgressMonitor(monitor, 1));

        monitor.done();
    }

    @Override
    public boolean preLaunchCheck(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor)
        throws CoreException
    {
        if (launchdelegate2 == null)
            return true;

        return launchdelegate2.preLaunchCheck(configuration, DELEGATELAUNCHMODE, monitor);
    }

    @Override
    public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
        throws CoreException
    {
        final String launchtype = config.getAttribute("type"); //$NON-NLS-1$
        launchdelegate = getLaunchDelegate(launchtype);
        if (launchdelegate instanceof ILaunchConfigurationDelegate2)
        {
            launchdelegate2 = (ILaunchConfigurationDelegate2)launchdelegate;
        }
    }

}
