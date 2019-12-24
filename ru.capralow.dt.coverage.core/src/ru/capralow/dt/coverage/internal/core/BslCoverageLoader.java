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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;

import com._1c.g5.v8.dt.core.platform.IResourceLookup;

import ru.capralow.dt.coverage.core.CoverageStatus;
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

	private final ISessionManager sessionManager;

	private IBslModelCoverage coverage;

	private final List<IBslCoverageListener> listeners = new ArrayList<>();

	private IResourceLookup resourceLookup;

	private ISessionListener sessionListener = new ISessionListener() {

		public void sessionActivated(ICoverageSession session) {
			Job.getJobManager().cancel(LOADJOB);
			if (session == null) {
				coverage = null;
				fireCoverageChanged();
			} else {
				coverage = IBslModelCoverage.LOADING;
				fireCoverageChanged();
				new LoadSessionJob(session, resourceLookup).schedule();
			}
		}

		public void sessionAdded(ICoverageSession addedSession) {
		}

		public void sessionRemoved(ICoverageSession removedSession) {
		}

	};

	private static final Object LOADJOB = new Object();

	private class LoadSessionJob extends Job {

		private final ICoverageSession session;

		private IResourceLookup resourceLookup;

		public LoadSessionJob(ICoverageSession session, IResourceLookup resourceLookup) {
			super(NLS.bind(CoreMessages.AnalyzingCoverageSession_task, session.getDescription()));
			this.session = session;
			this.resourceLookup = resourceLookup;
		}

		protected IStatus run(IProgressMonitor monitor) {
			final IBslModelCoverage c;
			try {
				c = new SessionAnalyzer().processSession(session, monitor, resourceLookup);
			} catch (CoreException e) {
				return CoverageStatus.SESSION_LOAD_ERROR.getStatus(e);
			}
			coverage = monitor.isCanceled() ? null : c;
			fireCoverageChanged();
			return Status.OK_STATUS;
		}

		@Override
		public boolean belongsTo(Object family) {
			return family == LOADJOB;
		}

	}

	public BslCoverageLoader(ISessionManager sessionManager, IResourceLookup resourceLookup) {
		this.sessionManager = sessionManager;
		this.resourceLookup = resourceLookup;
		sessionManager.addSessionListener(sessionListener);
	}

	public void addJavaCoverageListener(IBslCoverageListener l) {
		if (l == null) {
			throw new IllegalArgumentException();
		}
		if (!listeners.contains(l)) {
			listeners.add(l);
		}
	}

	public void removeJavaCoverageListener(IBslCoverageListener l) {
		listeners.remove(l);
	}

	protected void fireCoverageChanged() {
		// avoid concurrent modification issues
		for (IBslCoverageListener l : new ArrayList<IBslCoverageListener>(listeners)) {
			l.coverageChanged();
		}
	}

	public IBslModelCoverage getJavaModelCoverage() {
		return coverage;
	}

	public void dispose() {
		sessionManager.removeSessionListener(sessionListener);
	}

}
