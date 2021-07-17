/**
 * Copyright (c) 2020, Aleksandr Kapralov
 */
package ru.capralow.dt.coverage.internal.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
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
import com._1c.g5.v8.dt.profiling.core.IProfilingService;
import com.google.inject.Inject;

import ru.capralow.dt.coverage.ICoverageSession;
import ru.capralow.dt.coverage.MdUtils;
import ru.capralow.dt.coverage.analysis.IBslModelCoverage;
import ru.capralow.dt.coverage.internal.CoreMessages;
import ru.capralow.dt.coverage.internal.DebugOptions;
import ru.capralow.dt.coverage.internal.DebugOptions.ITracer;

public class SessionAnalyzer
{

    private static final ITracer PERFORMANCE = DebugOptions.PERFORMANCETRACER;

    private static final ArrayList<String> EXCLUDED_STATEMENTS = new ArrayList<>(Arrays.asList("конецпроцедуры", //$NON-NLS-1$
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
    private IProfilingService profilingService;

    @Inject
    private IV8ProjectManager projectManager;

    public Collection<ExecutionData> getExecutionData()
    {
        return executionDataStore.getContents();
    }

    public List<SessionInfo> getSessionInfos()
    {
        return sessionInfoStore.getInfos();
    }

    public IBslModelCoverage processSession(ICoverageSession session, IProgressMonitor monitor)
    {
        if (monitor.isCanceled())
            return null;

        IProfilingResult profilingResult = null;
        for (IProfilingResult profilingResults : profilingService.getResults())
        {
            if (monitor.isCanceled())
                return null;

            if (!profilingResults.getName().equals(session.getProfileName()))
                continue;

            profilingResult = profilingResults;
            break;
        }

        if (profilingResult == null)
            return null;

        PERFORMANCE.startTimer();
        PERFORMANCE.startMemoryUsage();

        Collection<URI> roots = session.getScope();

        monitor.beginTask(NLS.bind(CoreMessages.AnalyzingCoverageSession_task, session.getDescription()),
            1 + profilingResult.getReferences().size() + roots.size());

        monitor.worked(1);

        BslModelCoverage modelCoverage = new BslModelCoverage();

        boolean success = processProfilingResult(roots, profilingResult, modelCoverage, monitor);
        if (Boolean.TRUE.equals(success))
            success = fillMissedStatements(roots, modelCoverage, monitor);

        monitor.done();

        PERFORMANCE.stopTimer("loading " + session.getDescription()); //$NON-NLS-1$
        PERFORMANCE.stopMemoryUsage("loading " + session.getDescription()); //$NON-NLS-1$

        if (Boolean.FALSE.equals(success))
            return null;

        return modelCoverage;
    }

    private boolean fillMissedStatements(Collection<URI> roots, BslModelCoverage modelCoverage,
        IProgressMonitor monitor)
    {
        if (monitor.isCanceled())
            return false;

        for (URI root : roots)
        {
            monitor.worked(1);

            if (Boolean.FALSE.equals(modelCoverage.isModuleCovered(root)))
            {
                modelCoverage.putEmptyModule(root);
                continue;
            }

            EObject module = MdUtils.getEObjectByUri(root);
            if (!(module instanceof Module))
                continue;

            for (Method method : ((Module)module).allMethods())
            {
                if (monitor.isCanceled())
                    return false;

                BslNodeImpl methodCoverage = (BslNodeImpl)modelCoverage.getCoverageFor(EcoreUtil.getURI(method));

                for (Statement statement : method.allStatements())
                    processStatement(statement, methodCoverage);
            }

            modelCoverage.updateModuleCoverage(root);
        }

        return true;
    }

    private void processElseIfParts(IfStatement ifStatement, BslNodeImpl methodCoverage)
    {
        for (Conditional conditional : ifStatement.getElsIfParts())
        {
            ICompositeNode conditionalNode = NodeModelUtils.findActualNodeFor(conditional);
            int conditionalLineNum = conditionalNode.getStartLine();

            boolean elseIfBranchCovered = false;
            for (Statement subStatement : conditional.getStatements())
            {
                boolean statementCovered = processStatement(subStatement, methodCoverage);
                elseIfBranchCovered = elseIfBranchCovered || statementCovered;
            }

            if (elseIfBranchCovered)
                methodCoverage.increment(CounterImpl.COUNTER_0_1, CounterImpl.COUNTER_0_1, conditionalLineNum);
            else
                methodCoverage.increment(CounterImpl.COUNTER_1_0, CounterImpl.COUNTER_1_0, conditionalLineNum);
        }
    }

    private void processIfStatement(IfStatement ifStatement, BslNodeImpl methodCoverage)
    {
        ICompositeNode statementNode = NodeModelUtils.findActualNodeFor(ifStatement);
        int statementLineNum = statementNode.getStartLine();

        processElseIfParts(ifStatement, methodCoverage);

        boolean ifBranchCovered = false;
        boolean elseBranchCovered = ifStatement.getElseStatements().isEmpty();

        for (Statement subStatement : ifStatement.getIfPart().getStatements())
        {
            boolean statementCovered = processStatement(subStatement, methodCoverage);
            ifBranchCovered = ifBranchCovered || statementCovered;
        }

        for (Statement subStatement : ifStatement.getElseStatements())
        {
            boolean statementCovered = processStatement(subStatement, methodCoverage);
            elseBranchCovered = elseBranchCovered || statementCovered;
        }

        if (ifBranchCovered && elseBranchCovered)
            methodCoverage.increment(CounterImpl.COUNTER_0_1, CounterImpl.COUNTER_0_1, statementLineNum);
        else
            methodCoverage.increment(CounterImpl.COUNTER_1_0, CounterImpl.COUNTER_1_0, statementLineNum);
    }

    private void processModuleReference(BslModuleReference moduleReference, IProfilingResult profilingResult,
        Collection<URI> roots, BslModelCoverage modelCoverage, IProgressMonitor monitor)
    {
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
            configuration = ((IConfigurationProject)v8Project).getConfiguration();
        else if (v8Project instanceof IExtensionProject)
            configuration = ((IExtensionProject)v8Project).getConfiguration();
        else if (v8Project instanceof IExternalObjectProject)
            configuration = ((IExternalObjectProject)v8Project).getParent().getConfiguration();
        URI configurationUri = EcoreUtil.getURI(configuration);

        URI moduleUri = EcoreUtil.getURI(module);

        for (Method method : module.allMethods())
        {
            BslNodeImpl methodCoverage = new BslNodeImpl(ElementType.METHOD, method.getName());
            modelCoverage.putMethod(EcoreUtil.getURI(method), moduleUri, configurationUri, methodCoverage);
        }

        for (ILineProfilingResult profilingLine : profilingResult.getResultsForModule(moduleReference))
            processProfilingLine(profilingLine, moduleUri, modelCoverage);

    }

    private void processProfilingLine(ILineProfilingResult profilingLine, URI moduleUri, BslModelCoverage modelCoverage)
    {
        if (profilingLine.getLine().contains(profilingLine.getMethodSignature()) || profilingLine.getLine().isBlank())
            return;

        String methodName = profilingLine.getMethodSignature();
        int methodEndIndex = methodName.indexOf('(');
        if (methodEndIndex != -1)
            methodName = methodName.substring(0, methodEndIndex);

        BslNodeImpl methodCoverage = (BslNodeImpl)modelCoverage.getCoverageFor(methodName, moduleUri);
        if (methodCoverage == null)
            return;

        String line = profilingLine.getLine().toLowerCase().trim();
        boolean excludeStatement = false;
        for (int i = 0; i < EXCLUDED_STATEMENTS.size(); i++)
            if (line.startsWith(EXCLUDED_STATEMENTS.get(i)))
            {
                excludeStatement = true;
                break;
            }

        if (excludeStatement)
            return;

        methodCoverage.increment(CounterImpl.COUNTER_0_1, CounterImpl.COUNTER_0_0, profilingLine.getLineNo());
    }

    private boolean processProfilingResult(Collection<URI> roots, IProfilingResult profilingResult,
        BslModelCoverage modelCoverage, IProgressMonitor monitor)
    {
        if (monitor.isCanceled())
            return false;

        Iterator<BslModuleReference> moduleItr = profilingResult.getReferences().iterator();
        while (moduleItr.hasNext())
        {
            if (monitor.isCanceled())
                return false;

            monitor.worked(1);

            try
            {
                BslModuleReference moduleReference = moduleItr.next();
                processModuleReference(moduleReference, profilingResult, roots, modelCoverage, monitor);

            }
            catch (ConcurrentModificationException e)
            {
                return false;

            }
        }

        return true;
    }

    private boolean processStatement(Statement statement, BslNodeImpl methodCoverage)
    {
        if (statement instanceof EmptyStatement)
            return false;

        if (statement instanceof TryExceptStatement)
        {
            for (Statement tryStatement : ((TryExceptStatement)statement).getTryStatements())
                processStatement(tryStatement, methodCoverage);
            for (Statement exceptStatement : ((TryExceptStatement)statement).getExceptStatements())
                processStatement(exceptStatement, methodCoverage);
            return true;
        }

        if (statement instanceof ForStatement)
        {
            for (Statement forStatement : ((ForStatement)statement).getStatements())
                processStatement(forStatement, methodCoverage);
            return true;
        }

        if (statement instanceof ForToStatement)
        {
            for (Statement forToStatement : ((ForToStatement)statement).getStatements())
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
            processIfStatement((IfStatement)statement, methodCoverage);

        return !branchNotCovered;
    }

}
