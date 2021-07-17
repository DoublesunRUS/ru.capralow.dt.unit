/**
 * Copyright (c) 2020, Aleksandr Kapralov
 */
package ru.capralow.dt.coverage.ui.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTabGroup;

import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com.google.inject.Inject;

import ru.capralow.dt.coverage.internal.ui.CoverageUiPlugin;

/**
 * The coverage tab group simply uses the tab group for the launch type "debug"
 * and inserts the "Coverage" tab at the second position.
 */
public class CoverageTabGroup
    implements ILaunchConfigurationTabGroup, IExecutableExtension
{

    private static final String DELEGATE_LAUNCHMODE = ILaunchManager.DEBUG_MODE;
    private static final String EXPOINT_TABGROUP = "org.eclipse.debug.ui.launchConfigurationTabGroups"; //$NON-NLS-1$
    private static final String CONFIGATTR_TYPE = "type"; //$NON-NLS-1$

    private ILaunchConfigurationTabGroup tabGroupDelegate;
    private ILaunchConfigurationTab coverageTab;

    @Inject
    private IV8ProjectManager projectManager;
    @Inject
    private IResourceLookup resourceLookup;

    // IExecutableExtension interface

    @Override
    public void createTabs(ILaunchConfigurationDialog dialog, String mode)
    {
        tabGroupDelegate.createTabs(dialog, mode);
        coverageTab = createCoverageTab(dialog, mode);
    }

    @Override
    public void dispose()
    {
        tabGroupDelegate.dispose();
        coverageTab.dispose();
    }

    // ILaunchConfigurationTabGroup interface

    @Override
    public ILaunchConfigurationTab[] getTabs()
    {
        return insertCoverageTab(tabGroupDelegate.getTabs(), coverageTab);
    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration)
    {
        tabGroupDelegate.initializeFrom(configuration);
        coverageTab.initializeFrom(configuration);
    }

    @Deprecated
    @Override
    public void launched(ILaunch launch)
    {
        // deprecated method will not be called
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration)
    {
        tabGroupDelegate.performApply(configuration);
        coverageTab.performApply(configuration);
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
    {
        tabGroupDelegate.setDefaults(configuration);
        coverageTab.setDefaults(configuration);
    }

    @Override
    public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
        throws CoreException
    {
        tabGroupDelegate = createDelegate(config.getAttribute(CONFIGATTR_TYPE));
    }

    protected ILaunchConfigurationTab createCoverageTab(ILaunchConfigurationDialog dialog, String mode)
    {
        return new CoverageTab(projectManager, resourceLookup);
    }

    protected ILaunchConfigurationTabGroup createDelegate(String type) throws CoreException
    {
        IExtensionPoint extensionpoint = Platform.getExtensionRegistry().getExtensionPoint(EXPOINT_TABGROUP);
        IConfigurationElement[] tabGroupConfigs = extensionpoint.getConfigurationElements();
        IConfigurationElement element = null;
        findloop: for (IConfigurationElement tabGroupConfig : tabGroupConfigs)
        {
            if (type.equals(tabGroupConfig.getAttribute(CONFIGATTR_TYPE)))
            {
                IConfigurationElement[] modeConfigs = tabGroupConfig.getChildren("launchMode"); //$NON-NLS-1$
                if (modeConfigs.length == 0)
                {
                    element = tabGroupConfig;
                }
                for (final IConfigurationElement config : modeConfigs)
                {
                    if (DELEGATE_LAUNCHMODE.equals(config.getAttribute("mode"))) //$NON-NLS-1$
                    {
                        element = tabGroupConfig;
                        break findloop;
                    }
                }
            }
        }
        if (element == null)
        {
            String msg = "No tab group registered to run " + type; //$NON-NLS-1$
            throw new CoreException(CoverageUiPlugin.errorStatus(msg, null));
        }

        return (ILaunchConfigurationTabGroup)element.createExecutableExtension("class"); //$NON-NLS-1$
    }

    protected ILaunchConfigurationTab[] insertCoverageTab(ILaunchConfigurationTab[] delegateTabs,
        ILaunchConfigurationTab coverageTab2)
    {
        ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[delegateTabs.length + 1];
        tabs[0] = delegateTabs[0];
        tabs[1] = coverageTab2;
        System.arraycopy(delegateTabs, 1, tabs, 2, delegateTabs.length - 1);
        return tabs;
    }

}
