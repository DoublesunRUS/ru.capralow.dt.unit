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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ISourceFileCoverage;

/**
 * Internally used container for {@link IClassCoverage} and
 * {@link ISourceFileCoverage} nodes.
 */
final class AnalyzedNodes {

	static final AnalyzedNodes EMPTY = new AnalyzedNodes(Collections.<IClassCoverage>emptySet(),
			Collections.<ISourceFileCoverage>emptySet());

	private final Map<String, IClassCoverage> classmap;
	private final Map<String, ISourceFileCoverage> sourcemap;

	AnalyzedNodes(final Collection<IClassCoverage> classes, final Collection<ISourceFileCoverage> sourcefiles) {
		this.classmap = new HashMap<>();
		for (final IClassCoverage c : classes) {
			classmap.put(c.getName(), c);
		}
		this.sourcemap = new HashMap<>();
		for (final ISourceFileCoverage s : sourcefiles) {
			final String key = sourceKey(s.getPackageName(), s.getName());
			sourcemap.put(key, s);
		}
	}

	IClassCoverage getClassCoverage(final String vmname) {
		return classmap.get(vmname);
	}

	ISourceFileCoverage getSourceFileCoverage(final String vmpackagename, final String filename) {
		return sourcemap.get(sourceKey(vmpackagename, filename));
	}

	private static String sourceKey(final String vmpackagename, final String filename) {
		return vmpackagename + '/' + filename;
	}

}
