/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import ru.capralow.dt.coverage.CoverageTools;
import ru.capralow.dt.coverage.ICoverageSession;
import ru.capralow.dt.coverage.internal.ui.UiMessages;
import ru.capralow.dt.coverage.internal.ui.editors.CoverageSessionInput;
import ru.capralow.dt.coverage.internal.ui.editors.ExecutionDataEditor;

/**
 * Handler to open the execution data of the current session in an editor.
 */
public class OpenSessionExecutionDataHandler
    extends AbstractSessionManagerHandler
{

    public OpenSessionExecutionDataHandler()
    {
        super(CoverageTools.getSessionManager());
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        final ICoverageSession session = sessionManager.getActiveSession();
        final IEditorInput input = new CoverageSessionInput(session);
        final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
        try
        {
            window.getActivePage().openEditor(input, ExecutionDataEditor.ID);
        }
        catch (PartInitException e)
        {
            throw new ExecutionException(UiMessages.ExecutionDataEditorOpeningError_message, e);
        }
        return null;
    }

    @Override
    public boolean isEnabled()
    {
        return sessionManager.getActiveSession() != null;
    }
}
