/**
 * Copyright (c) 2020, Aleksandr Kapralov
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
import ru.capralow.dt.coverage.internal.ui.wizards.SessionImportWizard;

/**
 * Handler to import a JaCoCo coverage session.
 *
 * Unlike the default handler for the import command, this implementation does
 * not overwrite menu icons and labels.
 */
public class ImportSessionHandler
    extends AbstractSessionManagerHandler
{

    public ImportSessionHandler()
    {
        super(CoverageTools.getSessionManager());
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {

        final IWorkbenchSite site = HandlerUtil.getActiveSite(event);
        final ICommandService cs = site.getService(ICommandService.class);
        final IHandlerService hs = site.getService(IHandlerService.class);
        final Command command = cs.getCommand(IWorkbenchCommandConstants.FILE_IMPORT);

        try
        {
            hs.executeCommand(ParameterizedCommand.generateCommand(command,
                Collections.singletonMap(IWorkbenchCommandConstants.FILE_IMPORT_PARM_WIZARDID, SessionImportWizard.ID)),
                null);
        }
        catch (CommandException e)
        {
            CoverageUiPlugin.log(e);
        }

        return null;
    }

}
