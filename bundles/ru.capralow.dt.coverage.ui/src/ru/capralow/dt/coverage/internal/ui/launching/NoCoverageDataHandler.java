/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import ru.capralow.dt.coverage.internal.ui.CoverageUiPlugin;
import ru.capralow.dt.coverage.internal.ui.UiMessages;

/**
 * Status handler that issues an error message when no coverage data has been
 * found.
 */
public class NoCoverageDataHandler
    implements IStatusHandler
{

    @Override
    public Object handleStatus(IStatus status, Object source) throws CoreException
    {
        Shell parent = CoverageUiPlugin.getInstance().getShell();
        String title = UiMessages.NoCoverageDataError_title;
        String message = UiMessages.NoCoverageDataError_message;

        MessageDialog.openError(parent, title, message);
        return Boolean.FALSE;
    }

}
