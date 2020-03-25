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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.osgi.util.NLS;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.jacoco.core.analysis.ICoverageNode.ElementType;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfo;
import org.jacoco.core.data.SessionInfoStore;

import com._1c.g5.v8.dt.bsl.model.Conditional;
import com._1c.g5.v8.dt.bsl.model.EmptyStatement;
import com._1c.g5.v8.dt.bsl.model.ForStatement;
import com._1c.g5.v8.dt.bsl.model.ForToStatement;
import com._1c.g5.v8.dt.bsl.model.IfStatement;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.bsl.model.TryExceptStatement;
import com._1c.g5.v8.dt.core.platform.IConfigurationProject;
import com._1c.g5.v8.dt.core.platform.IExtensionProject;
import com._1c.g5.v8.dt.core.platform.IExternalObjectProject;
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

	private static final ArrayList<String> excludedStatements = new ArrayList<>(Arrays.asList("конецпроцедуры", //$NON-NLS-1$
			"endprocedure", //$NON-NLS-1$
			"конецфункции", //$NON-NLS-1$
			"endfunction", //$NON-NLS-1$
			"конецпопытки", //$NON-NLS-1$
			"endtry", //$NON-NLS-1$
			"конецесли", //$NON-NLS-1$
			"endif", //$NON-NLS-1$
			"конеццикла", //$NON-NLS-1$
			"enddo", //$NON-NLS-1$
			"иначеесли", //$NON-NLS-1$
			"endif")); //$NON-NLS-1$

	private ExecutionDataStore executionDataStore;

	private SessionInfoStore sessionInfoStore;

	@Inject
	private IBslModuleLocator bslModuleLocator;

	@Inject
	private IV8ProjectManager projectManager;

	public Collection<ExecutionData> getExecutionData() {
		return executionDataStore.getContents();
	}

	public List<SessionInfo> getSessionInfos() {
		return sessionInfoStore.getInfos();
	}

	public IBslModelCoverage processSession(ICoverageSession session, IProgressMonitor monitor) {
		if (monitor.isCanceled())
			return null;

		final List<IProfilingResult> profilingResults = session.getProfilingResults();
		if (profilingResults.isEmpty())
			return null;

		PERFORMANCE.startTimer();
		PERFORMANCE.startMemoryUsage();

		Collection<URI> roots = session.getScope();

		monitor.beginTask(NLS.bind(CoreMessages.AnalyzingCoverageSession_task, session.getDescription()),
				1 + roots.size() * 2 + profilingResults.size());

		monitor.worked(1);

		BslModelCoverage modelCoverage = new BslModelCoverage();

		processProfilingResults(roots, profilingResults, modelCoverage, monitor);

		fillMissedStatements(roots, modelCoverage, monitor);

		monitor.done();

		PERFORMANCE.stopTimer("loading " + session.getDescription()); //$NON-NLS-1$
		PERFORMANCE.stopMemoryUsage("loading " + session.getDescription()); //$NON-NLS-1$

		return modelCoverage;
	}

	private void fillMissedStatements(Collection<URI> roots, BslModelCoverage modelCoverage, IProgressMonitor monitor) {
		if (monitor.isCanceled())
			return;

		for (URI root : roots) {
			monitor.worked(1);

			if (Boolean.FALSE.equals(modelCoverage.isModuleCovered(root))) {
				modelCoverage.putEmptyModule(root);
				continue;
			}

			EObject module = MdUtils.getEObjectByURI(root);
			if (!(module instanceof Module))
				continue;

			for (Method method : ((Module) module).allMethods()) {
				if (monitor.isCanceled())
					return;

				BslNodeImpl methodCoverage = (BslNodeImpl) modelCoverage.getCoverageFor(EcoreUtil.getURI(method));

				for (Statement statement : method.allStatements())
					processStatement(statement, methodCoverage);
			}

			modelCoverage.updateModuleCoverage(root);
		}

	}

	private void processElseIfParts(IfStatement ifStatement, BslNodeImpl methodCoverage) {
		for (Conditional conditional : ifStatement.getElsIfParts()) {
			ICompositeNode conditionalNode = NodeModelUtils.findActualNodeFor(conditional);
			int conditionalLineNum = conditionalNode.getStartLine();

			boolean elseIfBranchCovered = false;
			for (Statement subStatement : conditional.getStatements()) {
				boolean statementCovered = processStatement(subStatement, methodCoverage);
				elseIfBranchCovered = elseIfBranchCovered || statementCovered;
			}

			if (elseIfBranchCovered)
				methodCoverage.increment(CounterImpl.COUNTER_0_1, CounterImpl.COUNTER_0_1, conditionalLineNum);
			else
				methodCoverage.increment(CounterImpl.COUNTER_1_0, CounterImpl.COUNTER_1_0, conditionalLineNum);
		}
	}

	private void processIfStatement(IfStatement ifStatement, BslNodeImpl methodCoverage) {
		ICompositeNode statementNode = NodeModelUtils.findActualNodeFor(ifStatement);
		int statementLineNum = statementNode.getStartLine();

		processElseIfParts(ifStatement, methodCoverage);

		boolean ifBranchCovered = false;
		boolean elseBranchCovered = ifStatement.getElseStatements().isEmpty();

		for (Statement subStatement : ifStatement.getIfPart().getStatements()) {
			boolean statementCovered = processStatement(subStatement, methodCoverage);
			ifBranchCovered = ifBranchCovered || statementCovered;
		}

		for (Statement subStatement : ifStatement.getElseStatements()) {
			boolean statementCovered = processStatement(subStatement, methodCoverage);
			elseBranchCovered = elseBranchCovered || statementCovered;
		}

		if (ifBranchCovered && elseBranchCovered)
			methodCoverage.increment(CounterImpl.COUNTER_0_1, CounterImpl.COUNTER_0_1, statementLineNum);
		else
			methodCoverage.increment(CounterImpl.COUNTER_1_0, CounterImpl.COUNTER_1_0, statementLineNum);
	}

	private void processModuleReference(BslModuleReference moduleReference, IProfilingResult profilingResult,
			Collection<URI> roots, BslModelCoverage modelCoverage, IProgressMonitor monitor) {
		if (monitor.isCanceled())
			return;

		IProject project = moduleReference.getProject();

		if (project == null)
			return;

		Module module = bslModuleLocator.getModule(moduleReference, true);
		if (module == null || !roots.contains(EcoreUtil.getURI(module)))
			return;

		EList<Method> moduleMethods = module.allMethods();
		if (moduleMethods.isEmpty())
			return;

		IV8Project v8Project = projectManager.getProject(project);
		Configuration configuration = null;
		if (v8Project instanceof IConfigurationProject)
			configuration = ((IConfigurationProject) v8Project).getConfiguration();
		else if (v8Project instanceof IExtensionProject)
			configuration = ((IExtensionProject) v8Project).getConfiguration();
		else if (v8Project instanceof IExternalObjectProject)
			configuration = ((IExternalObjectProject) v8Project).getParent().getConfiguration();
		URI configurationURI = EcoreUtil.getURI(configuration);

		URI moduleURI = EcoreUtil.getURI(module);

		for (Method method : module.allMethods()) {
			BslNodeImpl methodCoverage = new BslNodeImpl(ElementType.METHOD, method.getName());
			modelCoverage.putMethod(EcoreUtil.getURI(method), moduleURI, configurationURI, methodCoverage);
		}

		for (ILineProfilingResult profilingLine : profilingResult.getResultsForModule(moduleReference))
			processProfilingLine(profilingLine, moduleURI, modelCoverage);

	}

	private void processProfilingLine(ILineProfilingResult profilingLine, URI moduleURI,
			BslModelCoverage modelCoverage) {
		if (profilingLine.getLine().contains(profilingLine.getMethodSignature()) || profilingLine.getLine().isBlank())
			return;

		String methodName = profilingLine.getMethodSignature().substring(0,
				profilingLine.getMethodSignature().indexOf('('));

		BslNodeImpl methodCoverage = (BslNodeImpl) modelCoverage.getCoverageFor(methodName, moduleURI);
		if (methodCoverage == null)
			return;

		String line = profilingLine.getLine().toLowerCase().trim();
		boolean excludeStatement = false;
		for (int i = 0; i < excludedStatements.size(); i++)
			if (line.startsWith(excludedStatements.get(i))) {
				excludeStatement = true;
				break;
			}

		if (excludeStatement)
			return;

		methodCoverage.increment(CounterImpl.COUNTER_0_1, CounterImpl.COUNTER_0_0, profilingLine.getLineNo());
	}

	private void processProfilingResults(Collection<URI> roots, List<IProfilingResult> profilingResults,
			BslModelCoverage modelCoverage, IProgressMonitor monitor) {
		if (monitor.isCanceled())
			return;

		for (IProfilingResult profilingResult : profilingResults) {
			if (monitor.isCanceled())
				return;

			monitor.worked(1);

			for (BslModuleReference moduleReference : profilingResult.getReferences())
				processModuleReference(moduleReference, profilingResult, roots, modelCoverage, monitor);
		}
	}

	private boolean processStatement(Statement statement, BslNodeImpl methodCoverage) {
		if (statement instanceof EmptyStatement)
			return false;

		if (statement instanceof TryExceptStatement) {
			for (Statement tryStatement : ((TryExceptStatement) statement).getTryStatements())
				processStatement(tryStatement, methodCoverage);
			for (Statement exceptStatement : ((TryExceptStatement) statement).getExceptStatements())
				processStatement(exceptStatement, methodCoverage);
			return true;
		}

		if (statement instanceof ForStatement) {
			for (Statement forStatement : ((ForStatement) statement).getStatements())
				processStatement(forStatement, methodCoverage);
			return true;
		}

		if (statement instanceof ForToStatement) {
			for (Statement forToStatement : ((ForToStatement) statement).getStatements())
				processStatement(forToStatement, methodCoverage);
			return true;
		}

		ICompositeNode statementNode = NodeModelUtils.findActualNodeFor(statement);
		int statementLineNum = statementNode.getStartLine();
		LineImpl statementLine = methodCoverage.getLine(statementLineNum);

		boolean branchNotCovered = statementLine == null || statementLine.equals(LineImpl.EMPTY);

		if (branchNotCovered)
			methodCoverage.increment(CounterImpl.COUNTER_1_0, CounterImpl.COUNTER_0_0, statementLineNum);

		if (statement instanceof IfStatement)
			processIfStatement((IfStatement) statement, methodCoverage);

		return !branchNotCovered;
	}

}
