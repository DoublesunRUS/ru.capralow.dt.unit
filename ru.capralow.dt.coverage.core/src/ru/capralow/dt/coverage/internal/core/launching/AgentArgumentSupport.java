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
 ******************************************************************************/
package ru.capralow.dt.coverage.internal.core.launching;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.jacoco.agent.AgentJar;
import org.jacoco.core.runtime.AgentOptions;

import ru.capralow.dt.coverage.core.CoverageStatus;
import ru.capralow.dt.coverage.core.ICorePreferences;
import ru.capralow.dt.coverage.internal.core.CoverageCorePlugin;

/**
 * Internal utility to calculate the agent VM parameter.
 */
public class AgentArgumentSupport {

	private final ICorePreferences preferences;

	protected AgentArgumentSupport(ICorePreferences preferences) {
		this.preferences = preferences;
	}

	public AgentArgumentSupport() {
		this(CoverageCorePlugin.getInstance().getPreferences());
	}

	/**
	 * Returns a wrapper for the given launch configuration that adds the required
	 * VM argument.
	 *
	 * @param serverPort
	 *            port of the local agent server
	 * @param config
	 *            launch configuration to wrap
	 * @return wrapped launch configuration
	 */
	public ILaunchConfiguration addArgument(int serverPort, ILaunchConfiguration config) throws CoreException {
		return new AdjustedLaunchConfiguration(getArgument(serverPort), config);
	}

	protected String getArgument(int serverPort) throws CoreException {
		final AgentOptions options = new AgentOptions();
		options.setIncludes(preferences.getAgentIncludes());
		options.setExcludes(preferences.getAgentExcludes());
		options.setExclClassloader(preferences.getAgentExclClassloader());
		options.setOutput(AgentOptions.OutputMode.tcpclient);
		options.setPort(serverPort);
		return quote(options.getVMArgument(getAgentFile()));
	}

	protected File getAgentFile() throws CoreException {
		try {
			final URL agentfileurl = FileLocator.toFileURL(AgentJar.getResource());
			return new Path(agentfileurl.getPath()).toFile();
		} catch (IOException e) {
			throw new CoreException(CoverageStatus.NO_LOCAL_AGENTJAR_ERROR.getStatus(e));
		}
	}

	protected String quote(String arg) {
		if (arg.indexOf(' ') == -1) {
			return arg;
		} else {
			return '"' + arg + '"';
		}
	}

}
