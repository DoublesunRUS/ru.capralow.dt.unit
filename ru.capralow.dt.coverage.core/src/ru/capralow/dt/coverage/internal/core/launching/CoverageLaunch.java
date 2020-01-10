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

import java.util.Set;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.Launch;
import org.eclipse.emf.common.util.URI;

import ru.capralow.dt.coverage.core.CoverageTools;
import ru.capralow.dt.coverage.core.launching.ICoverageLaunch;
import ru.capralow.dt.coverage.internal.core.CoverageCorePlugin;

/**
 * Implementation of {@link ICoverageLaunch}.
 */
public class CoverageLaunch extends Launch implements ICoverageLaunch {

	private Set<URI> scope;
	private AgentServer agentServer;

	public CoverageLaunch(ILaunchConfiguration launchConfiguration, Set<URI> set) {
		super(launchConfiguration, CoverageTools.LAUNCH_MODE, null);
		this.scope = set;
		CoverageCorePlugin plugin = CoverageCorePlugin.getInstance();
		this.agentServer = new AgentServer(this, plugin.getSessionManager(), plugin.getPreferences());
	}

	public AgentServer getAgentServer() {
		return agentServer;
	}

	// ICoverageLaunch interface

	@Override
	public Set<URI> getScope() {
		return scope;
	}

}
