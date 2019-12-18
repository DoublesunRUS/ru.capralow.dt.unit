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
package ru.capralow.dt.coverage.internal.core;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.IExecutionDataVisitor;
import org.jacoco.core.data.ISessionInfoVisitor;
import org.jacoco.core.data.SessionInfo;
import org.jacoco.core.data.SessionInfoStore;

import ru.capralow.dt.coverage.core.IExecutionDataSource;

/**
 * In-memory {@link IExecutionDataSource} implementation.
 */
public class MemoryExecutionDataSource implements IExecutionDataSource, ISessionInfoVisitor, IExecutionDataVisitor {

	private final SessionInfoStore sessionInfoStore;
	private ExecutionDataStore executionDataStore;

	public MemoryExecutionDataSource() {
		sessionInfoStore = new SessionInfoStore();
		executionDataStore = new ExecutionDataStore();
	}

	public boolean isEmpty() {
		return sessionInfoStore.isEmpty();
	}

	public void accept(IExecutionDataVisitor executionDataVisitor, ISessionInfoVisitor sessionInfoVisitor)
			throws CoreException {
		sessionInfoStore.accept(sessionInfoVisitor);
		executionDataStore.accept(executionDataVisitor);
	}

	public void visitSessionInfo(SessionInfo info) {
		sessionInfoStore.visitSessionInfo(info);
	}

	public void visitClassExecution(ExecutionData data) {
		executionDataStore.visitClassExecution(data);
	}

	/**
	 * Collects execution data from the given reader.
	 *
	 * @param reader
	 *            reader to read execution data from
	 */
	public void readFrom(ExecutionDataReader reader) throws IOException {
		reader.setSessionInfoVisitor(sessionInfoStore);
		reader.setExecutionDataVisitor(executionDataStore);
		reader.read();
	}

}
