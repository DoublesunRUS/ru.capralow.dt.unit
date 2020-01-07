/*******************************************************************************
 * Copyright (c) 2006, 2019 Mountainminds GmbH & Co. KG and Contributors
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *
 * Adapted by Alexander Kapralov
 *
 ******************************************************************************/
package ru.capralow.dt.coverage.internal.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;

import ru.capralow.dt.coverage.core.ICoverageSession;
import ru.capralow.dt.coverage.core.ISessionListener;
import ru.capralow.dt.coverage.core.ISessionManager;
import ru.capralow.dt.coverage.core.analysis.IBslCoverageListener;
import ru.capralow.dt.coverage.core.analysis.IBslModelCoverage;
import ru.capralow.dt.coverage.internal.core.analysis.SessionAnalyzer;

/**
 * Internal utility class that loads the coverage data asynchronously, holds the
 * current {@link IBslModelCoverage} object and sends out events in case of
 * changed coverage information.
 */
public class BslCoverageLoader {

	private ISessionManager sessionManager;

	private IBslModelCoverage coverage;

	private List<IBslCoverageListener> listeners = new ArrayList<>();

	private ISessionListener sessionListener = new ISessionListener() {

		public void sessionActivated(ICoverageSession session) {
			Job.getJobManager().cancel(LOADJOB);
			if (session == null) {
				coverage = null;
				fireCoverageChanged();

			} else {
				coverage = IBslModelCoverage.LOADING;
				fireCoverageChanged();

				new LoadSessionJob(session).schedule();

			}
		}

		public void sessionAdded(ICoverageSession addedSession) {
		}

		public void sessionRemoved(ICoverageSession removedSession) {
		}

	};

	private static Object LOADJOB = new Object();

	private class LoadSessionJob extends Job {

		private ICoverageSession session;

		public LoadSessionJob(ICoverageSession session) {
			super(NLS.bind(CoreMessages.AnalyzingCoverageSession_task, session.getDescription()));
			this.session = session;
		}

		protected IStatus run(IProgressMonitor monitor) {
			SessionAnalyzer analyzer = CoverageCorePlugin.getInstance().getInjector()
					.getInstance(SessionAnalyzer.class);

			IBslModelCoverage c = analyzer.processSession(session, monitor);

			coverage = monitor.isCanceled() ? null : c;
			fireCoverageChanged();
			return Status.OK_STATUS;
		}

		@Override
		public boolean belongsTo(Object family) {
			return family == LOADJOB;
		}

	}

	public BslCoverageLoader(ISessionManager sessionManager) {
		this.sessionManager = sessionManager;
		sessionManager.addSessionListener(sessionListener);
	}

	public void addBslCoverageListener(IBslCoverageListener l) {
		if (l == null) {
			throw new IllegalArgumentException();
		}
		if (!listeners.contains(l)) {
			listeners.add(l);
		}
	}

	public void removeBslCoverageListener(IBslCoverageListener l) {
		listeners.remove(l);
	}

	protected void fireCoverageChanged() {
		// avoid concurrent modification issues
		for (IBslCoverageListener l : new ArrayList<IBslCoverageListener>(listeners)) {
			l.coverageChanged();
		}
	}

	public IBslModelCoverage getBslModelCoverage() {
		return coverage;
	}

	public void dispose() {
		sessionManager.removeSessionListener(sessionListener);
	}

}
