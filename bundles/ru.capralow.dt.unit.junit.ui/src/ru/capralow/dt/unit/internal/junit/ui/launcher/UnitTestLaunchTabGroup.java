/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui.launcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTabGroup;

import com.google.inject.Inject;
import com.google.inject.Provider;

import ru.capralow.dt.unit.internal.junit.ui.JUnitUiPlugin;

public class UnitTestLaunchTabGroup
    extends AbstractLaunchConfigurationTabGroup
    implements IExecutableExtension

{
    private static final String EXPOINT_TABGROUP = "org.eclipse.debug.ui.launchConfigurationTabGroups"; //$NON-NLS-1$
    private static final String EDT_TABGROUP_ID =
        "com._1c.g5.v8.dt.launching.ui.launchConfigurationTabGroup.RuntimeClient"; //$NON-NLS-1$
    private static final String CONFIGATTR_TYPE = "type"; //$NON-NLS-1$
    private static final String CONFIGATTR_ID = "id"; //$NON-NLS-1$

    private ILaunchConfigurationTabGroup tabGroupDelegate;
    private ILaunchConfigurationTab unitTab;

    @Inject
    private Provider<UnitTestLaunchTab> unitTestLaunchTabProvider;

    @Override
    public void createTabs(ILaunchConfigurationDialog dialog, String mode)
    {
        tabGroupDelegate.createTabs(dialog, mode);
        unitTab = unitTestLaunchTabProvider.get();
    }

    @Override
    public void dispose()
    {
        tabGroupDelegate.dispose();
        unitTab.dispose();
    }

    @Override
    public ILaunchConfigurationTab[] getTabs()
    {
        return insertUnitTestTab(tabGroupDelegate.getTabs(), unitTab);
    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration)
    {
        tabGroupDelegate.initializeFrom(configuration);
        unitTab.initializeFrom(configuration);
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration)
    {
        tabGroupDelegate.performApply(configuration);
        unitTab.performApply(configuration);
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
    {
        tabGroupDelegate.setDefaults(configuration);
        unitTab.setDefaults(configuration);
    }

    @Override
    public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
        throws CoreException
    {
        tabGroupDelegate = createDelegate(config.getAttribute(CONFIGATTR_TYPE));
    }

    protected ILaunchConfigurationTabGroup createDelegate(String type) throws CoreException
    {
        IExtensionPoint extensionpoint = Platform.getExtensionRegistry().getExtensionPoint(EXPOINT_TABGROUP);
        IConfigurationElement[] tabGroupConfigs = extensionpoint.getConfigurationElements();
        IConfigurationElement element = null;
        for (IConfigurationElement tabGroupConfig : tabGroupConfigs)
        {
            if (EDT_TABGROUP_ID.equals(tabGroupConfig.getAttribute(CONFIGATTR_ID)))
            {
                element = tabGroupConfig;
                break;
            }
        }
        if (element == null)
        {
            String msg = "No tab group registered to run " + type;
            throw new CoreException(JUnitUiPlugin.createErrorStatus(msg, null));
        }

        return (ILaunchConfigurationTabGroup)element.createExecutableExtension("class"); //$NON-NLS-1$
    }

    protected ILaunchConfigurationTab[] insertUnitTestTab(ILaunchConfigurationTab[] delegateTabs,
        ILaunchConfigurationTab unitTab2)
    {
        ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[delegateTabs.length + 1];
        tabs[0] = delegateTabs[0];
        tabs[1] = unitTab2;
        System.arraycopy(delegateTabs, 1, tabs, 2, delegateTabs.length - 1);
        return tabs;
    }

}
