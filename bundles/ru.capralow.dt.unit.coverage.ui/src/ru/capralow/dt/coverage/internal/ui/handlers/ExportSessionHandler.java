/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.handlers;

import java.util.Collections;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.CommandException;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;

import ru.capralow.dt.coverage.CoverageTools;
import ru.capralow.dt.coverage.internal.ui.CoverageUiPlugin;
import ru.capralow.dt.coverage.internal.ui.wizards.SessionExportWizard;

/**
 * Handler to export a JaCoCo coverage session.
 *
 * Unlike the default handler for the export command, this implementation does
 * not overwrite menu icons and labels.
 */
public class ExportSessionHandler
    extends AbstractSessionManagerHandler
{

    public ExportSessionHandler()
    {
        super(CoverageTools.getSessionManager());
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {

        final IWorkbenchSite site = HandlerUtil.getActiveSite(event);
        final ICommandService cs = site.getService(ICommandService.class);
        final IHandlerService hs = site.getService(IHandlerService.class);
        final Command command = cs.getCommand(IWorkbenchCommandConstants.FILE_EXPORT);

        try
        {
            hs.executeCommand(ParameterizedCommand.generateCommand(command,
                Collections.singletonMap(IWorkbenchCommandConstants.FILE_EXPORT_PARM_WIZARDID, SessionExportWizard.ID)),
                null);
        }
        catch (CommandException e)
        {
            CoverageUiPlugin.log(e);
        }

        return null;
    }

    @Override
    public boolean isEnabled()
    {
        return !sessionManager.getSessions().isEmpty();
    }

}
