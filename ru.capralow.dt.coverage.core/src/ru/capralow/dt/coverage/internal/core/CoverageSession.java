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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.emf.common.util.URI;
import org.jacoco.core.data.IExecutionDataVisitor;
import org.jacoco.core.data.ISessionInfoVisitor;

import com._1c.g5.v8.dt.profiling.core.IProfilingResult;

import ru.capralow.dt.coverage.core.ICoverageSession;

/**
 * A {@link ru.capralow.dt.coverage.core.ICoverageSession} implementation.
 */
public class CoverageSession extends PlatformObject implements ICoverageSession {

	private String description;
	private Set<URI> scope;
	private ILaunchConfiguration launchConfiguration;
	private List<IProfilingResult> profilingResults;

	public CoverageSession(String description, Set<URI> set, ILaunchConfiguration launchConfiguration) {
		this.description = description;
		this.scope = Collections.unmodifiableSet(new HashSet<>(set));
		this.launchConfiguration = launchConfiguration;
		this.profilingResults = new ArrayList<>();
	}

	// ICoverageSession implementation

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public Set<URI> getScope() {
		return scope;
	}

	@Override
	public ILaunchConfiguration getLaunchConfiguration() {
		return launchConfiguration;
	}

	@Override
	public void accept(IProfilingResult profilingResult) {
		profilingResults.add(profilingResult);
	}

	@Override
	public void accept(IExecutionDataVisitor executionDataVisitor, ISessionInfoVisitor sessionInfoVisitor)
			throws CoreException {
		// Для BSL используется IProfilingResult
	}

	@Override
	public List<IProfilingResult> getProfilingResults() {
		return profilingResults;
	}

}
