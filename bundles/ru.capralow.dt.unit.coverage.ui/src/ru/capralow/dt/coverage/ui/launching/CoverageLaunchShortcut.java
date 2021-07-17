/**
 * Copyright (c) 2020, Aleksandr Kapralov
 */
package ru.capralow.dt.coverage.ui.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;

import ru.capralow.dt.coverage.CoverageTools;
import ru.capralow.dt.coverage.internal.ui.CoverageUiPlugin;

/**
 * Generic ILaunchShortcut implementation that delegates to another
 * ILaunchShortcut with a given id. The id is specified via the executable
 * extension attribute "class":
 *
 * <pre>
 *   class="ru.capralow.dt.coverage.internal.ui.launching.CoverageLaunchShortcut:org.eclipse.jdt.debug.ui.localJavaShortcut"
 * </pre>
 */
public class CoverageLaunchShortcut
    implements ILaunchShortcut, IExecutableExtension
{

    private String delegateId;
    private ILaunchShortcut delegate;

    @Override
    public void launch(IEditorPart editor, String mode)
    {
        ILaunchShortcut launchDelegate = getDelegate();
        if (launchDelegate != null)
        {
            launchDelegate.launch(editor, CoverageTools.LAUNCH_MODE);
        }
    }

    // IExecutableExtension interface:

    @Override
    public void launch(ISelection selection, String mode)
    {
        ILaunchShortcut launchDelegate = getDelegate();
        if (launchDelegate != null)
        {
            launchDelegate.launch(selection, CoverageTools.LAUNCH_MODE);
        }
    }

    // ILaunchShortcut interface:

    @Override
    public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
        throws CoreException
    {
        delegateId = String.valueOf(data);
    }

    private ILaunchShortcut getDelegate()
    {
        if (delegate == null)
        {
            IExtensionPoint extensionPoint = Platform.getExtensionRegistry()
                .getExtensionPoint(IDebugUIConstants.PLUGIN_ID, IDebugUIConstants.EXTENSION_POINT_LAUNCH_SHORTCUTS);
            for (final IConfigurationElement config : extensionPoint.getConfigurationElements())
            {
                if (delegateId.equals(config.getAttribute("id"))) //$NON-NLS-1$
                {
                    try
                    {
                        delegate = (ILaunchShortcut)config.createExecutableExtension("class"); //$NON-NLS-1$
                    }
                    catch (CoreException e)
                    {
                        CoverageUiPlugin.log(e);
                    }
                    break;
                }
            }
            if (delegate == null)
            {
                String msg = "ILaunchShortcut declaration not found: " + delegateId;
                CoverageUiPlugin.getInstance().getLog().log(CoverageUiPlugin.errorStatus(msg, null));
            }
        }
        return delegate;
    }

}
