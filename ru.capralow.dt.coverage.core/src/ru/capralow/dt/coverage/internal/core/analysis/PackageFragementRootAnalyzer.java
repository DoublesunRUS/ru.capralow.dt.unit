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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.data.ExecutionDataStore;

import com._1c.g5.v8.dt.bm.xtext.BmAwareResourceSetProvider;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;

import ru.capralow.dt.coverage.core.CoverageStatus;
import ru.capralow.dt.coverage.internal.core.CoverageCorePlugin;
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
		this.resourceLookup = CoverageCorePlugin.getInjector().getInstance(IResourceLookup.class);
		this.resourceSetProvider = CoverageCorePlugin.getInjector().getInstance(BmAwareResourceSetProvider.class);
	}

	public AnalyzedNodes analyze(final URI root) throws CoreException {
		try {
			AnalyzedNodes nodes = cache.get(root);
			if (nodes != null) {
				return nodes;
			}

			final CoverageBuilder builder = new CoverageBuilder();

			final Analyzer analyzer = new Analyzer(executionData, builder);

			nodes = new AnalyzedNodes(builder.getClasses(), builder.getSourceFiles());

			cache.put(root, nodes);

			return nodes;

		} catch (Exception e) {
			throw new CoreException(
					CoverageStatus.BUNDLE_ANALYSIS_ERROR.getStatus(root.toPlatformString(true), root, e));

		}
	}
}
