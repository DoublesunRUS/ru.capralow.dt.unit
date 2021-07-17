/**
 * Copyright (c) 2020, Aleksandr Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.handlers;

import java.util.Collections;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import ru.capralow.dt.coverage.CoverageTools;
import ru.capralow.dt.coverage.ICoverageSession;
import ru.capralow.dt.coverage.internal.ui.ContextHelp;
import ru.capralow.dt.coverage.internal.ui.CoverageUiPlugin;
import ru.capralow.dt.coverage.internal.ui.UiMessages;

/**
 * Handler to select the currently active coverage session.
 */
public class SelectActiveSessionHandler
    extends AbstractSessionManagerHandler
{

    public SelectActiveSessionHandler()
    {
        super(CoverageTools.getSessionManager());
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        final ListDialog dialog = new ListDialog(HandlerUtil.getActiveShell(event))
        {
            @Override
            protected void configureShell(Shell shell)
            {
                super.configureShell(shell);
                ContextHelp.setHelp(shell, ContextHelp.SELECT_ACTIVE_SESSION);
            }
        };
        dialog.setTitle(UiMessages.SelectActiveSessionDialog_title);
        dialog.setMessage(UiMessages.SelectActiveSessionDialog_message);
        dialog.setContentProvider(ArrayContentProvider.getInstance());
        dialog.setLabelProvider(new LabelProvider()
        {
            @Override
            public Image getImage(Object element)
            {
                return CoverageUiPlugin.getImage(CoverageUiPlugin.OBJ_SESSION);
            }

            @Override
            public String getText(Object element)
            {
                return ((ICoverageSession)element).getDescription();
            }
        });
        dialog.setInitialElementSelections(Collections.singletonList(sessionManager.getActiveSession()));
        dialog.setInput(sessionManager.getSessions());
        if (dialog.open() == Window.OK)
        {
            final Object[] result = dialog.getResult();
            if (result.length == 1)
            {
                sessionManager.activateSession((ICoverageSession)result[0]);
            }
        }
        return null;
    }

    @Override
    public boolean isEnabled()
    {
        return !sessionManager.getSessions().isEmpty();
    }
}
