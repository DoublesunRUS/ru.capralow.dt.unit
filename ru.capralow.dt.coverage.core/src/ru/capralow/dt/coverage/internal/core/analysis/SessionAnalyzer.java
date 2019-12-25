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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osgi.util.NLS;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.IPackageCoverage;
import org.jacoco.core.analysis.ISourceFileCoverage;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfo;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.core.internal.analysis.BundleCoverageImpl;

import com._1c.g5.v8.dt.core.platform.IResourceLookup;

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

		modelCoverage = new BslModelCoverage();
		final Collection<URI> roots = session.getScope();
		monitor.beginTask(NLS.bind(CoreMessages.AnalyzingCoverageSession_task, session.getDescription()),
				1 + roots.size());
		executionDataStore = new ExecutionDataStore();
		sessionInfoStore = new SessionInfoStore();
		session.accept(executionDataStore, sessionInfoStore);
		monitor.worked(1);

		// final PackageFragementRootAnalyzer analyzer = new
		// PackageFragementRootAnalyzer(executionDataStore);
		//
		// for (final URI root : roots) {
		// if (monitor.isCanceled()) {
		// break;
		// }
		// processPackageFragmentRoot(root, analyzer, new SubProgressMonitor(monitor,
		// 1));
		// }
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

	private void processPackageFragmentRoot(URI root, PackageFragementRootAnalyzer analyzer, IProgressMonitor monitor)
			throws CoreException {
		final TypeVisitor visitor = new TypeVisitor(analyzer.analyze(root));
		new TypeTraverser(root).process(visitor, monitor);

		final IBundleCoverage bundle = new BundleCoverageImpl(getName(root),
				visitor.getClasses(),
				visitor.getSources());
		modelCoverage.putFragmentRoot(root, bundle);
		putPackages(bundle.getPackages(), root);
	}

	// package private for testing
	String getName(URI root) {
		IFile moduleFile = resourceLookup.getPlatformResource(root);
		IPath path = moduleFile.getFullPath();

		return path.toString();
	}

	private void putPackages(Collection<IPackageCoverage> packages, URI root) {
		for (IPackageCoverage c : packages) {
			final String name = c.getName().replace('/', '.');
			// final IPackageFragment fragment = root.getPackageFragment(name);
			// modelCoverage.putFragment(fragment, c);
		}
	}

	private class TypeVisitor implements ITypeVisitor {

		private final AnalyzedNodes nodes;

		private final Set<IClassCoverage> classes;
		private final Set<ISourceFileCoverage> sources;

		TypeVisitor(AnalyzedNodes nodes) {
			this.nodes = nodes;
			this.classes = new HashSet<>();
			this.sources = new HashSet<>();
		}

		Collection<IClassCoverage> getClasses() {
			return classes;
		}

		Collection<ISourceFileCoverage> getSources() {
			return sources;
		}

		public void visit(IType type, String vmname) {
			final IClassCoverage coverage = nodes.getClassCoverage(vmname);
			if (coverage != null) {
				classes.add(coverage);
				// modelCoverage.putType(type, coverage);
			}
		}

		public void visit(IClassFile classfile) throws JavaModelException {
			final String vmname = classfile.getType().getFullyQualifiedName().replace('.', '/');
			final IClassCoverage coverage = nodes.getClassCoverage(vmname);
			if (coverage != null) {
				modelCoverage.putClassFile(classfile, coverage);
				// Add source file coverage manually in case of binary roots
				// as we will not see compilation units:
				final ISourceFileCoverage source = nodes.getSourceFileCoverage(coverage.getPackageName(),
						coverage.getSourceFileName());
				if (source != null) {
					sources.add(source);
				}
			}
		}

		public void visit(ICompilationUnit unit) throws JavaModelException {
			final String vmpackagename = unit.getParent().getElementName().replace('.', '/');
			final ISourceFileCoverage coverage = nodes.getSourceFileCoverage(vmpackagename, unit.getElementName());
			if (coverage != null) {
				sources.add(coverage);
				modelCoverage.putCompilationUnit(unit, coverage);
			}
		}

	}

}
