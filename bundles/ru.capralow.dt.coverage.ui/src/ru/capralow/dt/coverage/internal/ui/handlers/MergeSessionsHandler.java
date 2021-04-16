/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.handlers;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import ru.capralow.dt.coverage.CoverageStatus;
import ru.capralow.dt.coverage.CoverageTools;
import ru.capralow.dt.coverage.ICoverageSession;
import ru.capralow.dt.coverage.ISessionManager;
import ru.capralow.dt.coverage.internal.ui.UiMessages;
import ru.capralow.dt.coverage.internal.ui.dialogs.MergeSessionsDialog;

/**
 * Handler to merge session coverage session.
 */
public class MergeSessionsHandler
    extends AbstractSessionManagerHandler
{

    private static Job createJob(final ISessionManager sm, final Collection<ICoverageSession> sessions,
        final String description)
    {
        final Job job = new Job(UiMessages.MergingSessions_task)
        {

            @Override
            protected IStatus run(IProgressMonitor monitor)
            {
                try
                {
                    sm.mergeSessions(sessions, description, monitor);
                }
                catch (CoreException e)
                {
                    return CoverageStatus.MERGE_SESSIONS_ERROR.getStatus(e);
                }
                return Status.OK_STATUS;
            }
        };
        job.setPriority(Job.SHORT);
        return job;
    }

    public MergeSessionsHandler()
    {
        super(CoverageTools.getSessionManager());
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        final Shell parentShell = HandlerUtil.getActiveShell(event);
        final ISessionManager sm = CoverageTools.getSessionManager();
        List<ICoverageSession> sessions = sm.getSessions();
        String descr = UiMessages.MergeSessionsDialogDescriptionDefault_value;
        descr = MessageFormat.format(descr, new Object[] { new Date() });
        final MergeSessionsDialog d = new MergeSessionsDialog(parentShell, sessions, descr);
        if (d.open() == IDialogConstants.OK_ID)
        {
            createJob(sm, d.getSessions(), d.getDescription()).schedule();
        }
        return null;
    }

    @Override
    public boolean isEnabled()
    {
        return sessionManager.getSessions().size() > 1;
    }

}
