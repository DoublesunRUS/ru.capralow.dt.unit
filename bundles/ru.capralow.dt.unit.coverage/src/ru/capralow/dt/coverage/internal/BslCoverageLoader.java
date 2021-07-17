/**
 * Copyright (c) 2020, Aleksandr Kapralov
 */
package ru.capralow.dt.coverage.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;

import ru.capralow.dt.coverage.ICoverageSession;
import ru.capralow.dt.coverage.ISessionListener;
import ru.capralow.dt.coverage.ISessionManager;
import ru.capralow.dt.coverage.analysis.IBslCoverageListener;
import ru.capralow.dt.coverage.analysis.IBslModelCoverage;
import ru.capralow.dt.coverage.internal.analysis.SessionAnalyzer;

/**
 * Internal utility class that loads the coverage data asynchronously, holds the
 * current {@link IBslModelCoverage} object and sends out events in case of
 * changed coverage information.
 */
public class BslCoverageLoader
{

    private static Object loadJob = new Object();

    private ISessionManager sessionManager;

    private IBslModelCoverage coverage;

    private List<IBslCoverageListener> listeners = new ArrayList<>();

    private ISessionListener sessionListener = new ISessionListener()
    {

        @Override
        public void sessionActivated(ICoverageSession session)
        {
            Job.getJobManager().cancel(loadJob);
            if (session == null)
            {
                coverage = null;
                fireCoverageChanged();

            }
            else
            {
                coverage = IBslModelCoverage.LOADING;
                fireCoverageChanged();

                new LoadSessionJob(session).schedule();

            }
        }

        @Override
        public void sessionAdded(ICoverageSession addedSession)
        {
            // Нечего делать
        }

        @Override
        public void sessionRemoved(ICoverageSession removedSession)
        {
            // Нечего делать
        }

    };

    public BslCoverageLoader(ISessionManager sessionManager)
    {
        this.sessionManager = sessionManager;
        sessionManager.addSessionListener(sessionListener);
    }

    public void addBslCoverageListener(IBslCoverageListener l)
    {
        if (l == null)
        {
            throw new IllegalArgumentException();
        }
        if (!listeners.contains(l))
        {
            listeners.add(l);
        }
    }

    public void dispose()
    {
        sessionManager.removeSessionListener(sessionListener);
    }

    public IBslModelCoverage getBslModelCoverage()
    {
        return coverage;
    }

    public void removeBslCoverageListener(IBslCoverageListener l)
    {
        listeners.remove(l);
    }

    protected void fireCoverageChanged()
    {
        // avoid concurrent modification issues
        for (IBslCoverageListener l : new ArrayList<>(listeners))
        {
            l.coverageChanged();
        }
    }

    private class LoadSessionJob
        extends Job
    {

        private ICoverageSession session;

        LoadSessionJob(ICoverageSession session)
        {
            super(NLS.bind(CoreMessages.AnalyzingCoverageSession_task, session.getDescription()));
            this.session = session;
        }

        @Override
        public boolean belongsTo(Object family)
        {
            return family == loadJob;
        }

        @Override
        protected IStatus run(IProgressMonitor monitor)
        {
            SessionAnalyzer analyzer =
                CoverageCorePlugin.getInstance().getInjector().getInstance(SessionAnalyzer.class);

            IBslModelCoverage c = analyzer.processSession(session, monitor);

            coverage = monitor.isCanceled() ? null : c;
            fireCoverageChanged();
            return Status.OK_STATUS;
        }

    }

}
