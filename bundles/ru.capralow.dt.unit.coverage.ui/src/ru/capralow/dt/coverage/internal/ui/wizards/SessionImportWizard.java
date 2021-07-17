/**
 * Copyright (c) 2020, Aleksandr Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import ru.capralow.dt.coverage.CoverageTools;
import ru.capralow.dt.coverage.ISessionImporter;
import ru.capralow.dt.coverage.internal.ui.CoverageUiPlugin;
import ru.capralow.dt.coverage.internal.ui.UiMessages;

/**
 * The import wizard for coverage sessions.
 */
public class SessionImportWizard
    extends Wizard
    implements IImportWizard
{

    public static final String ID = "ru.capralow.dt.coverage.ui.sessionImportWizard"; //$NON-NLS-1$

    private static final String SETTINGSID = "SessionImportWizard"; //$NON-NLS-1$

    private SessionImportPage1 page1;
    private SessionImportPage2 page2;

    public SessionImportWizard()
    {
        super();
        IDialogSettings pluginsettings = CoverageUiPlugin.getInstance().getDialogSettings();
        IDialogSettings wizardsettings = pluginsettings.getSection(SETTINGSID);
        if (wizardsettings == null)
        {
            wizardsettings = pluginsettings.addNewSection(SETTINGSID);
        }
        setDialogSettings(wizardsettings);
        setWindowTitle(UiMessages.ImportSession_title);
        setDefaultPageImageDescriptor(CoverageUiPlugin.getImageDescriptor(CoverageUiPlugin.WIZBAN_IMPORT_SESSION));
        setNeedsProgressMonitor(true);
    }

    @Override
    public void addPages()
    {
        page1 = new SessionImportPage1();
        addPage(page1);
        page2 = new SessionImportPage2();
        addPage(page2);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection)
    {
        // nothing to initialize
    }

    @Override
    public boolean performFinish()
    {
        page1.saveWidgetValues();
        page2.saveWidgetValues();
        return importSession();
    }

    private boolean importSession()
    {
        final ISessionImporter importer = CoverageTools.getImporter();
        importer.setDescription(page2.getSessionDescription());
        // importer.setExecutionDataSource(page1.getProfilingResults());
        importer.setScope(page2.getScope());
        IRunnableWithProgress op = new IRunnableWithProgress()
        {
            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
            {
                try
                {
                    importer.importSession(monitor);
                }
                catch (Exception e)
                {
                    throw new InvocationTargetException(e);
                }
            }
        };
        try
        {
            getContainer().run(true, true, op);
        }
        catch (InterruptedException e)
        {
            return false;
        }
        catch (InvocationTargetException ite)
        {
            Throwable ex = ite.getTargetException();
            CoverageUiPlugin.log(ex);
            final String title = UiMessages.ImportSessionErrorDialog_title;
            final String msg = UiMessages.ImportSessionErrorDialog_message;
            final IStatus status = CoverageUiPlugin.errorStatus(String.valueOf(ex.getMessage()), ex);
            ErrorDialog.openError(getShell(), title, msg, status);
            return false;
        }
        return true;
    }

}
