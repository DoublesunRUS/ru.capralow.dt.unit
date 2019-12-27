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

import java.time.temporal.ChronoField;
import java.util.List;
import java.util.UUID;

import org.eclipse.core.runtime.CoreException;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.IExecutionDataVisitor;
import org.jacoco.core.data.ISessionInfoVisitor;
import org.jacoco.core.data.SessionInfo;
import org.jacoco.core.data.SessionInfoStore;

import com._1c.g5.v8.dt.debug.model.base.data.BSLModuleType;
import com._1c.g5.v8.dt.profiling.core.ILineProfilingResult;
import com._1c.g5.v8.dt.profiling.core.IProfilingResult;

import ru.capralow.dt.coverage.core.IExecutionDataSource;

/**
 * In-memory {@link IExecutionDataSource} implementation.
 */
public class ProfilingResultsDataSource implements IExecutionDataSource, ISessionInfoVisitor, IExecutionDataVisitor {

	private final SessionInfoStore sessionInfoStore;
	private ExecutionDataStore executionDataStore;

	public ProfilingResultsDataSource() {
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
	 * @param profilingResults
	 *            reader to read execution data from
	 */
	public void readFrom(List<IProfilingResult> profilingResults) {
		for (IProfilingResult profilingResult : profilingResults) {

			sessionInfoStore.visitSessionInfo(new SessionInfo(profilingResult.getName(),
					profilingResult.getDateOfSession().getLong(ChronoField.EPOCH_DAY),
					System.currentTimeMillis()));

			for (ILineProfilingResult result : profilingResult.getProfilingResults()) {
				if (result.getModuleID().getType() == BSLModuleType.EXT_MD_MODULE)
					continue;

				long moduleID = UUID.fromString(result.getModuleID().getObjectID()).getMostSignificantBits()
						& Long.MAX_VALUE;

				ExecutionData executionData = executionDataStore.get(moduleID);
				if (executionData == null) {
					executionData = new ExecutionData(moduleID, result.getMethodSignature(), 1000);
					executionDataStore.visitClassExecution(executionData);
				}

				executionData.getProbes()[result.getLineNo() - 1] = true;
			}
		}
	}

}
