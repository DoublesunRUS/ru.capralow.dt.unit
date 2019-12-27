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

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import com._1c.g5.v8.dt.profiling.core.IProfilingResult;
import com._1c.g5.v8.dt.profiling.core.IProfilingService;

import ru.capralow.dt.coverage.core.ICorePreferences;
import ru.capralow.dt.coverage.core.ISessionManager;
import ru.capralow.dt.coverage.core.launching.ICoverageLaunch;
import ru.capralow.dt.coverage.internal.core.CoreMessages;
import ru.capralow.dt.coverage.internal.core.CoverageCorePlugin;
import ru.capralow.dt.coverage.internal.core.CoverageSession;
import ru.capralow.dt.coverage.internal.core.ProfilingResultsDataSource;

public class AgentServer {

	private final ICoverageLaunch launch;
	private final ISessionManager sessionManager;
	private final ICorePreferences preferences;

	private boolean dataReceived;

	private IProfilingService profilingService;

	public AgentServer(ICoverageLaunch launch, ISessionManager sessionManager, ICorePreferences preferences) {
		this.preferences = preferences;
		this.launch = launch;
		this.sessionManager = sessionManager;
		this.dataReceived = false;
		this.profilingService = CoverageCorePlugin.getInjector().getInstance(IProfilingService.class);
	}

	public void start() {
		profilingService.toggleTargetWaitingState(true);
	}

	public void stop() {
		profilingService.toggleTargetWaitingState(false);

		List<IProfilingResult> profilingResults = profilingService.getResults();
		if (profilingResults.isEmpty())
			return;

		ProfilingResultsDataSource dataSource = new ProfilingResultsDataSource();
		dataSource.readFrom(profilingResults);

		dataReceived = true;
		final CoverageSession session = new CoverageSession(createDescription(),
				launch.getScope(),
				dataSource,
				launch.getLaunchConfiguration());
		sessionManager.addSession(session, preferences.getActivateNewSessions(), launch);
	}

	public boolean hasDataReceived() {
		return dataReceived;
	}

	private String createDescription() {
		final Object[] args = new Object[] { launch.getLaunchConfiguration().getName(), new Date() };
		return MessageFormat.format(CoreMessages.LaunchSessionDescription_value, args);
	}
}
