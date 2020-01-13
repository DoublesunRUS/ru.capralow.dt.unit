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
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.osgi.util.NLS;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.jacoco.core.analysis.ICoverageNode.ElementType;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfo;
import org.jacoco.core.data.SessionInfoStore;

import com._1c.g5.v8.dt.bm.index.emf.IBmEmfIndexManager;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.core.platform.IConfigurationProject;
import com._1c.g5.v8.dt.core.platform.IExtensionProject;
import com._1c.g5.v8.dt.core.platform.IExternalObjectProject;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.debug.core.model.BslModuleReference;
import com._1c.g5.v8.dt.debug.core.model.IBslModuleLocator;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com._1c.g5.v8.dt.profiling.core.ILineProfilingResult;
import com._1c.g5.v8.dt.profiling.core.IProfilingResult;
import com.google.inject.Inject;

import ru.capralow.dt.coverage.core.ICoverageSession;
import ru.capralow.dt.coverage.core.MdUtils;
import ru.capralow.dt.coverage.core.analysis.IBslModelCoverage;
import ru.capralow.dt.coverage.internal.core.CoreMessages;
import ru.capralow.dt.coverage.internal.core.DebugOptions;
import ru.capralow.dt.coverage.internal.core.DebugOptions.ITracer;

public class SessionAnalyzer {

	private static final ITracer PERFORMANCE = DebugOptions.PERFORMANCETRACER;

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

	public Collection<ExecutionData> getExecutionData() {
		return executionDataStore.getContents();
	}

	public List<SessionInfo> getSessionInfos() {
		return sessionInfoStore.getInfos();
	}

	public IBslModelCoverage processSession(ICoverageSession session, IProgressMonitor monitor) {
		PERFORMANCE.startTimer();
		PERFORMANCE.startMemoryUsage();

		Collection<URI> roots = session.getScope();

		monitor.beginTask(NLS.bind(CoreMessages.AnalyzingCoverageSession_task, session.getDescription()),
				1 + roots.size());

		BslModelCoverage modelCoverage = new BslModelCoverage();
		List<IProfilingResult> profilingResults = session.getProfilingResults();

		monitor.worked(1);

		for (URI root : roots) {
			IV8Project v8Project = projectManager.getProject(root);
			Configuration configuration = null;
			if (v8Project instanceof IConfigurationProject)
				configuration = ((IConfigurationProject) v8Project).getConfiguration();
			else if (v8Project instanceof IExtensionProject)
				configuration = ((IExtensionProject) v8Project).getConfiguration();
			else if (v8Project instanceof IExternalObjectProject)
				configuration = ((IExternalObjectProject) v8Project).getParent().getConfiguration();

			URI configurationURI = EcoreUtil.getURI(configuration);

			Module module = MdUtils.getModuleByURI(root);
			if (module == null)
				continue;

			for (Method method : module.allMethods()) {
				BslNodeImpl methodCoverage = new BslNodeImpl(ElementType.METHOD, method.getName());

				for (Statement statement : method.allStatements()) {
					ICompositeNode statementNode = NodeModelUtils.findActualNodeFor(statement);

					methodCoverage
							.increment(CounterImpl.COUNTER_1_0, CounterImpl.COUNTER_0_0, statementNode.getStartLine());
				}

				modelCoverage.putMethod(EcoreUtil.getURI(method), root, configurationURI, methodCoverage);
			}

		}

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

				for (ILineProfilingResult profilingLine : profilingResult.getResultsForModule(moduleReference)) {
					URI profilingMethod = null;
					for (Method method : moduleMethods) {
						if (method.getName().equals(profilingLine.getMethodSignature().substring(0,
								profilingLine.getMethodSignature().indexOf('(')))) {

							profilingMethod = EcoreUtil.getURI(method);
							break;
						}
					}

					if (profilingMethod == null)
						continue;

					BslNodeImpl methodCoverage = (BslNodeImpl) modelCoverage.getCoverageFor(profilingMethod);
					if (methodCoverage == null)
						continue;

					methodCoverage
							.increment(CounterImpl.COUNTER_0_1, CounterImpl.COUNTER_0_0, profilingLine.getLineNo());
				}
			}

		}

		monitor.done();
		PERFORMANCE.stopTimer("loading " + session.getDescription()); //$NON-NLS-1$
		PERFORMANCE.stopMemoryUsage("loading " + session.getDescription()); //$NON-NLS-1$
		return modelCoverage;
	}

	private String getName(URI root) {
		IFile moduleFile = resourceLookup.getPlatformResource(root);
		IPath path = moduleFile.getFullPath();

		return path.toString();
	}

}
