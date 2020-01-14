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

import com._1c.g5.v8.dt.profiling.core.IProfilingResult;
import com._1c.g5.v8.dt.profiling.core.IProfilingResultListener;
import com._1c.g5.v8.dt.profiling.core.IProfilingService;

import ru.capralow.dt.coverage.core.ICorePreferences;
import ru.capralow.dt.coverage.core.ISessionManager;
import ru.capralow.dt.coverage.core.launching.ICoverageLaunch;
import ru.capralow.dt.coverage.internal.core.CoreMessages;
import ru.capralow.dt.coverage.internal.core.CoverageCorePlugin;
import ru.capralow.dt.coverage.internal.core.CoverageSession;

public class AgentServer implements IProfilingResultListener {

	private ICoverageLaunch launch;
	private ISessionManager sessionManager;
	private ICorePreferences preferences;

	private boolean dataReceived;

	private IProfilingService profilingService;

	public AgentServer(ICoverageLaunch launch, ISessionManager sessionManager, ICorePreferences preferences) {
		this.preferences = preferences;
		this.launch = launch;
		this.sessionManager = sessionManager;
		this.dataReceived = false;
		this.profilingService = CoverageCorePlugin.getInstance().getInjector().getInstance(IProfilingService.class);
	}

	public boolean hasDataReceived() {
		return dataReceived;
	}

	public void start() {
		profilingService.addProfilingResultsListener(this);
		profilingService.toggleTargetWaitingState(true);
	}

	public void stop() {
		profilingService.toggleTargetWaitingState(false);
		// profilingService.removeProfilingResultsListener(this);
	}

	private String createDescription() {
		Object[] args = new Object[] { launch.getLaunchConfiguration().getName(), new Date() };
		return MessageFormat.format(CoreMessages.LaunchSessionDescription_value, args);
	}

	@Override
	public void resultRenamed(IProfilingResult profilingResult, String newName) {
		// Нечего делать
	}

	@Override
	public void resultsCleared() {
		// Нечего делать
	}

	@Override
	public void resultsUpdated(IProfilingResult profilingResult) {
		dataReceived = !profilingResult.getProfilingResults().isEmpty();

		CoverageSession session = new CoverageSession(createDescription(),
				launch.getScope(),
				profilingResult,
				launch.getLaunchConfiguration());

		sessionManager.addSession(session, preferences.getActivateNewSessions(), launch);
	}
}
