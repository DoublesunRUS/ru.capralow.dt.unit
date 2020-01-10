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
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.osgi.util.NLS;
import org.jacoco.core.analysis.ICoverageNode.ElementType;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfo;
import org.jacoco.core.data.SessionInfoStore;

import com._1c.g5.v8.dt.bm.index.emf.IBmEmfIndexManager;
import com._1c.g5.v8.dt.bm.index.emf.IBmEmfIndexProvider;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.debug.core.model.BslModuleReference;
import com._1c.g5.v8.dt.debug.core.model.IBslModuleLocator;
import com._1c.g5.v8.dt.metadata.mdclass.MdObject;
import com._1c.g5.v8.dt.profiling.core.ILineProfilingResult;
import com._1c.g5.v8.dt.profiling.core.IProfilingResult;
import com.google.inject.Inject;

import ru.capralow.dt.coverage.core.ICoverageSession;
import ru.capralow.dt.coverage.core.analysis.IBslModelCoverage;
import ru.capralow.dt.coverage.internal.core.CoreMessages;
import ru.capralow.dt.coverage.internal.core.DebugOptions;
import ru.capralow.dt.coverage.internal.core.DebugOptions.ITracer;

public class SessionAnalyzer {

	private static final ITracer PERFORMANCE = DebugOptions.PERFORMANCETRACER;

	private BslModelCoverage modelCoverage;

	private ExecutionDataStore executionDataStore;

	private SessionInfoStore sessionInfoStore;

	@Inject
	private IBmEmfIndexManager bmEmfIndexManager;

	@Inject
	private IBslModuleLocator bslModuleLocator;

	@Inject
	private IResourceLookup resourceLookup;

	@Inject
	private IV8ProjectManager projectManager;

	public IBslModelCoverage processSession(ICoverageSession session, IProgressMonitor monitor) {
		PERFORMANCE.startTimer();
		PERFORMANCE.startMemoryUsage();

		modelCoverage = new BslModelCoverage();
		Collection<URI> roots = session.getScope();
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

				IProject project = moduleReference.getProject();

				if (project == null)
					continue;

				Module module = bslModuleLocator.getModule(moduleReference, true);
				if (module == null)
					continue;

				EList<Method> moduleMethods = module.allMethods();
				if (moduleMethods.isEmpty())
					continue;

				IBmEmfIndexProvider bmEmfIndexProvider = bmEmfIndexManager.getEmfIndexProvider(project);

				ModuleNodeImpl projectCoverage = new ModuleNodeImpl(ElementType.GROUP, project.getName());

				ModuleNodeImpl moduleCoverage = new ModuleNodeImpl(ElementType.CLASS, module.getUniqueName());

				ModuleNodeImpl methodCoverage = new ModuleNodeImpl(ElementType.METHOD, moduleMethods.get(0).getName());

				CounterImpl moduleCounter = CounterImpl.COUNTER_0_0;

				for (ILineProfilingResult profilingLine : profilingResult.getResultsForModule(moduleReference)) {
					moduleCounter = moduleCounter.increment(1, 1);
					profilingLine.getLineNo();
				}

				moduleCoverage.instructionCounter = moduleCounter;

				modelCoverage.putMethod(moduleMethods.get(0), (MdObject) module.getOwner(), moduleCoverage);
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

	private String getName(URI root) {
		IFile moduleFile = resourceLookup.getPlatformResource(root);
		IPath path = moduleFile.getFullPath();

		return path.toString();
	}

}
