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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.resource.XtextResource;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.data.ExecutionDataStore;

import com._1c.g5.v8.dt.bm.xtext.BmAwareResourceSetProvider;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;

import ru.capralow.dt.coverage.internal.core.DebugOptions;
import ru.capralow.dt.coverage.internal.core.DebugOptions.ITracer;

/**
 * Analyzes the class files that belong to given package fragment roots. This
 * analyzer implements an cache to remember the class files that have been
 * analyzed before.
 */
final class PackageFragementRootAnalyzer {

	private static final ITracer TRACER = DebugOptions.ANALYSISTRACER;

	private final ExecutionDataStore executionData;
	private final Map<Object, AnalyzedNodes> cache;

	private IResourceLookup resourceLookup;
	private BmAwareResourceSetProvider resourceSetProvider;

	public PackageFragementRootAnalyzer(final ExecutionDataStore executionData) {
		this.executionData = executionData;
		this.cache = new HashMap<>();
	}

	public AnalyzedNodes analyze(final URI root) throws CoreException {
		IResource location = null;

		try {
			location = getClassfilesLocation(root);

			if (location == null) {
				IFile moduleFile = resourceLookup.getPlatformResource(root);
				IPath path = moduleFile.getFullPath();

				TRACER.trace("No class files found for package fragment root {0}", //$NON-NLS-1$
						path);
				return AnalyzedNodes.EMPTY;
			}

			AnalyzedNodes nodes = cache.get(location);
			if (nodes != null) {
				return nodes;
			}

			final CoverageBuilder builder = new CoverageBuilder();

			final Analyzer analyzer = new Analyzer(executionData, builder);

			new ResourceTreeWalker(analyzer).walk(location);

			nodes = new AnalyzedNodes(builder.getClasses(), builder.getSourceFiles());

			cache.put(location, nodes);

			return nodes;

		} catch (Exception e) {
			// throw new
			//
			// CoreException(CoverageStatus.BUNDLE_ANALYSIS_ERROR.getStatus(root.getElementName(),
			// location, e));
		}

		return null;
	}

	private IResource getClassfilesLocation(URI root) {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(root.segment(1));

		ResourceSet resourceSet = resourceSetProvider.get(project);
		XtextResource bslModuleResource = (XtextResource) resourceSet.getResource(root, true);

		return (IResource) bslModuleResource;
	}

}
