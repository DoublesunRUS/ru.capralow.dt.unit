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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.jacoco.core.data.ExecutionDataStore;

import com._1c.g5.v8.dt.bsl.model.Module;

import ru.capralow.dt.coverage.internal.core.DebugOptions;
import ru.capralow.dt.coverage.internal.core.DebugOptions.ITracer;

/**
 * Analyzes the class files that belong to given package fragment roots. This
 * analyzer implements an cache to remember the class files that have been
 * analyzed before.
 */
final class PackageFragementRootAnalyzer {

	private static final ITracer TRACER = DebugOptions.ANALYSISTRACER;

	private final ExecutionDataStore executiondata;
	private final Map<Object, AnalyzedNodes> cache;

	PackageFragementRootAnalyzer(final ExecutionDataStore executiondata) {
		this.executiondata = executiondata;
		this.cache = new HashMap<>();
	}

	AnalyzedNodes analyze(final Module root) throws CoreException {
		return null;
		// if (root.isExternal()) {
		// return analyzeExternal(root);
		// } else {
		// return analyzeInternal(root);
		// }
	}

	private AnalyzedNodes analyzeInternal(final Module root) throws CoreException {
		return null;
		// IResource location = null;
		// try {
		// location = getClassfilesLocation(root);
		//
		// if (location == null) {
		// TRACER.trace("No class files found for package fragment root {0}",
		// //$NON-NLS-1$
		// root.getPath());
		// return AnalyzedNodes.EMPTY;
		// }
		//
		// AnalyzedNodes nodes = cache.get(location);
		// if (nodes != null) {
		// return nodes;
		// }
		//
		// final CoverageBuilder builder = new CoverageBuilder();
		// final Analyzer analyzer = new Analyzer(executiondata, builder);
		// new ResourceTreeWalker(analyzer).walk(location);
		// nodes = new AnalyzedNodes(builder.getClasses(), builder.getSourceFiles());
		// cache.put(location, nodes);
		// return nodes;
		// } catch (Exception e) {
		// // throw new
		// //
		// CoreException(CoverageStatus.BUNDLE_ANALYSIS_ERROR.getStatus(root.getElementName(),
		// // location, e));
		// }
	}

	private AnalyzedNodes analyzeExternal(final Module root) throws CoreException {
		return null;
		// IPath location = null;
		// try {
		// location = root.getPath();
		//
		// AnalyzedNodes nodes = cache.get(location);
		// if (nodes != null) {
		// return nodes;
		// }
		//
		// final CoverageBuilder builder = new CoverageBuilder();
		// final Analyzer analyzer = new Analyzer(executiondata, builder);
		// new ResourceTreeWalker(analyzer).walk(location);
		// nodes = new AnalyzedNodes(builder.getClasses(), builder.getSourceFiles());
		// cache.put(location, nodes);
		// return nodes;
		// } catch (Exception e) {
		// // throw new
		// //
		// CoreException(CoverageStatus.BUNDLE_ANALYSIS_ERROR.getStatus(root.getElementName(),
		// // location, e));
		// }
	}

	private IResource getClassfilesLocation(Module root) throws CoreException {
		return null;
		// // For binary roots the underlying resource directly points to class files:
		// if (root.getKind() == IPackageFragmentRoot.K_BINARY) {
		// return root.getResource();
		// }
		//
		// // For source roots we need to find the corresponding output folder:
		// IPath path = root.getRawClasspathEntry().getOutputLocation();
		// if (path == null) {
		// path = root.getJavaProject().getOutputLocation();
		// }
		// return root.getResource().getWorkspace().getRoot().findMember(path);
	}

}
