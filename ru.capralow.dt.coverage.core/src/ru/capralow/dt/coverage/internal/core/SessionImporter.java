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

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;

import ru.capralow.dt.coverage.core.IExecutionDataSource;
import ru.capralow.dt.coverage.core.ISessionImporter;
import ru.capralow.dt.coverage.core.ISessionManager;

/**
 * Implementation of ISessionImporter.
 */
public class SessionImporter implements ISessionImporter {

	private final ISessionManager sessionManager;

	private String description;
	private IExecutionDataSource dataSource;
	private Set<URI> scope;

	public SessionImporter(ISessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setExecutionDataSource(final IExecutionDataSource source) {
		this.dataSource = source;
	}

	public void setScope(Set<URI> scope) {
		this.scope = scope;
	}

	public void importSession(IProgressMonitor monitor) throws CoreException {
		monitor.beginTask(CoreMessages.ImportingSession_task, 2);
		monitor.worked(1);
		final CoverageSession session = new CoverageSession(description, scope, dataSource, null);
		sessionManager.addSession(session, true, null);
		monitor.done();
	}

}
