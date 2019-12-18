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
package ru.capralow.dt.coverage.internal.core.launching;

import java.io.IOException;
import java.net.ServerSocket;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.jacoco.core.runtime.RemoteControlWriter;

import com._1c.g5.v8.dt.profiling.core.IProfilingResult;
import com._1c.g5.v8.dt.profiling.core.IProfilingService;

import ru.capralow.dt.coverage.core.CoverageStatus;
import ru.capralow.dt.coverage.core.ICorePreferences;
import ru.capralow.dt.coverage.core.ISessionManager;
import ru.capralow.dt.coverage.core.launching.ICoverageLaunch;
import ru.capralow.dt.coverage.internal.core.CoreMessages;
import ru.capralow.dt.coverage.internal.core.CoverageSession;
import ru.capralow.dt.coverage.internal.core.ExecutionDataFiles;

/**
 * Internal TCP/IP server for the JaCoCo agent to connect to.
 *
 */
public class AgentServer {

	private final ICoverageLaunch launch;
	private final ISessionManager sessionManager;
	private final ExecutionDataFiles files;
	private final ICorePreferences preferences;

	private ServerSocket serverSocket;
	private RemoteControlWriter writer;
	private boolean dataReceived;

	private IProfilingService profilingService;

	AgentServer(ICoverageLaunch launch, ISessionManager sessionManager, ExecutionDataFiles files,
			ICorePreferences preferences, IProfilingService profilingService) {
		// super(AgentServer.class.getName());
		this.preferences = preferences;
		// setSystem(true);
		this.launch = launch;
		this.sessionManager = sessionManager;
		this.files = files;
		this.dataReceived = false;
		this.profilingService = profilingService;
	}

	public void start() {
		profilingService.toggleTargetWaitingState(true);
	}

	public void requestDump(boolean reset) throws CoreException {
		if (writer != null) {
			try {
				writer.visitDumpCommand(true, reset);
			} catch (IOException e) {
				throw new CoreException(CoverageStatus.DUMP_REQUEST_ERROR.getStatus(e));
			}
		}
	}

	public void stop() {
		profilingService.toggleTargetWaitingState(false);

		List<IProfilingResult> profilingResults = profilingService.getResults();
		if (profilingResults.isEmpty())
			return;

		dataReceived = true;
		final CoverageSession session = new CoverageSession(createDescription(),
				launch.getScope(),
				profilingResults,
				// files.newFile(null),
				launch.getLaunchConfiguration());
		sessionManager.addSession(session, preferences.getActivateNewSessions(), launch);
	}

	public boolean hasDataReceived() {
		return dataReceived;
	}

	public int getPort() {
		return serverSocket.getLocalPort();
	}

	private String createDescription() {
		final Object[] args = new Object[] { launch.getLaunchConfiguration().getName(), new Date() };
		return MessageFormat.format(CoreMessages.LaunchSessionDescription_value, args);
	}
}
