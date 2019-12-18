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

import com._1c.g5.v8.dt.bsl.model.Module;

import ru.capralow.dt.coverage.core.IExecutionDataSource;
import ru.capralow.dt.coverage.core.ISessionImporter;
import ru.capralow.dt.coverage.core.ISessionManager;

/**
 * Implementation of ISessionImporter.
 */
public class SessionImporter implements ISessionImporter {

	private final ISessionManager sessionManager;
	private final ExecutionDataFiles executionDataFiles;

	private String description;
	private IExecutionDataSource dataSource;
	private Set<Module> scope;
	private boolean copy;

	public SessionImporter(ISessionManager sessionManager, ExecutionDataFiles executionDataFiles) {
		this.sessionManager = sessionManager;
		this.executionDataFiles = executionDataFiles;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setExecutionDataSource(final IExecutionDataSource source) {
		this.dataSource = source;
	}

	public void setScope(Set<Module> scope) {
		this.scope = scope;
	}

	public void setCopy(boolean copy) {
		this.copy = copy;
	}

	public void importSession(IProgressMonitor monitor) throws CoreException {
		monitor.beginTask(CoreMessages.ImportingSession_task, 2);
		final IExecutionDataSource source;
		if (this.copy) {
			source = this.executionDataFiles.newFile(dataSource);
		} else {
			source = dataSource;
		}
		monitor.worked(1);
		final CoverageSession session = new CoverageSession(description, scope, source, null);
		sessionManager.addSession(session, true, null);
		monitor.done();
	}

}
