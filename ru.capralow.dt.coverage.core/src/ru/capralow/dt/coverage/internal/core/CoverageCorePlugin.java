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
 * Adapted by Alexander A. Kapralov
 *
 ******************************************************************************/
package ru.capralow.dt.coverage.internal.core;

import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.debug.core.model.IProcess;
import org.osgi.framework.BundleContext;

import com.google.inject.Guice;
import com.google.inject.Injector;

import ru.capralow.dt.coverage.core.CoverageStatus;
import ru.capralow.dt.coverage.core.ICorePreferences;
import ru.capralow.dt.coverage.core.ISessionManager;
import ru.capralow.dt.coverage.internal.core.launching.CoverageLaunch;

/**
 * Bundle activator for the 1Unit Coverage core.
 */
public class CoverageCorePlugin extends Plugin {

	public static final String ID = "ru.capralow.dt.coverage.core"; //$NON-NLS-1$

	/** Status used to trigger user prompts */
	private static final IStatus PROMPT_STATUS = new Status(IStatus.INFO, "org.eclipse.debug.ui", 200, "", null); //$NON-NLS-1$//$NON-NLS-2$

	private static CoverageCorePlugin instance;

	private static Injector injector;

	private ICorePreferences preferences = ICorePreferences.DEFAULT;

	private ISessionManager sessionManager;

	private BslCoverageLoader coverageLoader;

	private ExecutionDataFiles executionDataFiles;

	public static synchronized Injector getInjector() {
		if (injector == null)
			injector = createInjector();

		return injector;
	}

	public static IStatus createErrorStatus(String message, Throwable throwable) {
		return new Status(IStatus.ERROR, ID, 0, message, throwable);
	}

	private static Injector createInjector() {
		try {
			return Guice.createInjector(new ExternalDependenciesModule(getInstance()));

		} catch (Exception e) {
			String msg = MessageFormat.format(CoreMessages.Failed_to_create_injector_for_0,
					getInstance().getBundle().getSymbolicName());
			log(createErrorStatus(msg, e));
			return injector;

		}
	}

	public static void log(IStatus status) {
		getInstance().getLog().log(status);
	}

	private ILaunchListener launchListener = new ILaunchListener() {
		public void launchRemoved(ILaunch launch) {
			if (preferences.getAutoRemoveSessions()) {
				sessionManager.removeSessionsFor(launch);
			}
		}

		public void launchAdded(ILaunch launch) {
		}

		public void launchChanged(ILaunch launch) {
		}
	};

	private IDebugEventSetListener debugListener = new IDebugEventSetListener() {
		public void handleDebugEvents(DebugEvent[] events) {
			for (final DebugEvent e : events) {
				if (e.getSource() instanceof IProcess && e.getKind() == DebugEvent.TERMINATE) {
					final IProcess proc = (IProcess) e.getSource();
					final ILaunch launch = proc.getLaunch();
					if (launch instanceof CoverageLaunch) {
						final CoverageLaunch coverageLaunch = (CoverageLaunch) launch;
						coverageLaunch.getAgentServer().stop();
						checkExecutionData(coverageLaunch);
					}
				}
			}
		}

		/**
		 * Issues an user prompt using the status handler registered for the given
		 * status.
		 *
		 * @param status
		 *            IStatus object to find prompter for
		 * @param info
		 *            additional information passed to the handler
		 * @return boolean result returned by the status handler
		 * @throws CoreException
		 *             if the status has severity error and no handler is available
		 */
		private boolean showPrompt(IStatus status, Object info) throws CoreException {
			IStatusHandler prompter = DebugPlugin.getDefault().getStatusHandler(PROMPT_STATUS);
			if (prompter == null) {
				if (status.getSeverity() == IStatus.ERROR) {
					throw new CoreException(status);
				} else {
					return true;
				}
			} else {
				return ((Boolean) prompter.handleStatus(status, info)).booleanValue();
			}
		}

		private void checkExecutionData(CoverageLaunch launch) {
			if (!launch.getAgentServer().hasDataReceived()) {
				try {
					showPrompt(CoverageStatus.NO_COVERAGE_DATA_ERROR.getStatus(), launch);
				} catch (CoreException e) {
					getLog().log(e.getStatus());
				}
			}
		}
	};

	@Override
	public void start(BundleContext context) throws Exception {
		instance = this;
		super.start(context);

		executionDataFiles = new ExecutionDataFiles(getStateLocation());
		executionDataFiles.deleteTemporaryFiles();

		sessionManager = new SessionManager(executionDataFiles);

		coverageLoader = new BslCoverageLoader(sessionManager);

		DebugPlugin.getDefault().getLaunchManager().addLaunchListener(launchListener);
		DebugPlugin.getDefault().addDebugEventListener(debugListener);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		executionDataFiles.deleteTemporaryFiles();

		DebugPlugin.getDefault().removeDebugEventListener(debugListener);
		DebugPlugin.getDefault().getLaunchManager().removeLaunchListener(launchListener);

		executionDataFiles = null;

		coverageLoader.dispose();
		coverageLoader = null;

		sessionManager = null;

		super.stop(context);
		instance = null;
	}

	public static CoverageCorePlugin getInstance() {
		return instance;
	}

	public void setPreferences(ICorePreferences preferences) {
		this.preferences = preferences;
	}

	public ICorePreferences getPreferences() {
		return this.preferences;
	}

	public ISessionManager getSessionManager() {
		return sessionManager;
	}

	public BslCoverageLoader getBslCoverageLoader() {
		return coverageLoader;
	}

	public ExecutionDataFiles getExecutionDataFiles() {
		return executionDataFiles;
	}

}
