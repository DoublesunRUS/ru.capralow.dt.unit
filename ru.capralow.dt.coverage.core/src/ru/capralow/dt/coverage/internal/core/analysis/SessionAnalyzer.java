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
package ru.capralow.dt.coverage.internal.core.analysis;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.osgi.util.NLS;
import org.jacoco.core.analysis.CoverageNodeImpl;
import org.jacoco.core.analysis.ICoverageNode.ElementType;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfo;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.core.internal.analysis.ClassCoverageImpl;
import org.jacoco.core.internal.analysis.CounterImpl;
import org.jacoco.core.internal.analysis.MethodCoverageImpl;

import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.debug.core.model.BslModuleReference;
import com._1c.g5.v8.dt.debug.core.model.IBslModuleLocator;
import com._1c.g5.v8.dt.profiling.core.ILineProfilingResult;
import com._1c.g5.v8.dt.profiling.core.IProfilingResult;

import ru.capralow.dt.coverage.core.ICoverageSession;
import ru.capralow.dt.coverage.core.analysis.IBslModelCoverage;
import ru.capralow.dt.coverage.internal.core.CoreMessages;
import ru.capralow.dt.coverage.internal.core.CoverageCorePlugin;
import ru.capralow.dt.coverage.internal.core.DebugOptions;
import ru.capralow.dt.coverage.internal.core.DebugOptions.ITracer;

/**
 * Internal class to analyze all Java elements of a particular coverage session.
 */
public class SessionAnalyzer {

	private static final ITracer PERFORMANCE = DebugOptions.PERFORMANCETRACER;

	private BslModelCoverage modelCoverage;

	private ExecutionDataStore executionDataStore;

	private SessionInfoStore sessionInfoStore;

	private IResourceLookup resourceLookup;

	public IBslModelCoverage processSession(ICoverageSession session, IProgressMonitor monitor) throws CoreException {
		PERFORMANCE.startTimer();
		PERFORMANCE.startMemoryUsage();

		this.resourceLookup = CoverageCorePlugin.getInjector().getInstance(IResourceLookup.class);

		IBslModuleLocator bslModuleLocator = CoverageCorePlugin.getInjector().getInstance(IBslModuleLocator.class);

		modelCoverage = new BslModelCoverage();
		final Collection<URI> roots = session.getScope();
		monitor.beginTask(NLS.bind(CoreMessages.AnalyzingCoverageSession_task, session.getDescription()),
				1 + roots.size());
		List<IProfilingResult> profilingResults = session.getProfilingResults();
		monitor.worked(1);

		for (IProfilingResult profilingResult : profilingResults) {
			if (monitor.isCanceled())
				break;

			for (BslModuleReference moduleReference : profilingResult.getReferences()) {
				if (monitor.isCanceled())
					break;

				if (moduleReference.getProject() == null)
					continue;

				Module module = bslModuleLocator.getModule(moduleReference, true);

				ClassCoverageImpl classCoverage = new ClassCoverageImpl(moduleReference.toString(), 0, false);

				for (ILineProfilingResult profilingLine : profilingResult.getResultsForModule(moduleReference)) {
					profilingLine.getLineNo();
				}

				final MethodCoverageImpl methodCoverage = new MethodCoverageImpl("name", "desc", "sign"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

				final CoverageNodeImpl nodeCoverage = new CoverageNodeImpl(ElementType.METHOD, "string"); //$NON-NLS-1$

				final CounterImpl nodeCounter = ((CounterImpl) nodeCoverage.getInstructionCounter()).increment(5, 3);

				methodCoverage.increment(nodeCounter, nodeCounter, 1);

				classCoverage.addMethod(methodCoverage);

				// modelCoverage.putMethod(root, root, classCoverage);
			}

		}

		monitor.done();
		PERFORMANCE.stopTimer("loading " + session.getDescription()); //$NON-NLS-1$
		PERFORMANCE.stopMemoryUsage("loading " + session.getDescription()); //$NON-NLS-1$
		return modelCoverage;
	}

	public List<SessionInfo> getSessionInfos() {
		return sessionInfoStore.getInfos();
	}

	public Collection<ExecutionData> getExecutionData() {
		return executionDataStore.getContents();
	}

	String getName(URI root) {
		IFile moduleFile = resourceLookup.getPlatformResource(root);
		IPath path = moduleFile.getFullPath();

		return path.toString();
	}

}
