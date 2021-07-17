/**
 * Copyright (c) 2020, Aleksandr Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.handlers;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import ru.capralow.dt.coverage.internal.ui.CoverageUiPlugin;
import ru.capralow.dt.coverage.internal.ui.UiMessages;

/**
 * Internal label provider for {@link ILaunch} objects.
 */
class LaunchLabelProvider
    extends LabelProvider
{

    public static String getLaunchText(ILaunch launch)
    {
        // new launch configuration
        final ILaunchConfiguration config = launch.getLaunchConfiguration();
        if (config == null)
        {
            return UiMessages.DumpExecutionDataUnknownLaunch_value;
        }
        StringBuilder sb = new StringBuilder(config.getName());
        sb.append(" ["); //$NON-NLS-1$
        try
        {
            sb.append(config.getType().getName());
        }
        catch (CoreException e)
        {
            CoverageUiPlugin.log(e);
        }
        sb.append("]"); //$NON-NLS-1$
        return sb.toString();
    }

    @Override
    public Image getImage(Object element)
    {
        return CoverageUiPlugin.getImage(CoverageUiPlugin.ELCL_DUMP);
    }

    @Override
    public String getText(Object element)
    {
        return getLaunchText((ILaunch)element);
    }

}
