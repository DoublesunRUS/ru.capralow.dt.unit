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
package ru.capralow.dt.coverage.internal.ui.coverageview;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.jacoco.core.analysis.ICoverageNode;

import ru.capralow.dt.coverage.core.CoverageTools;

/**
 * Internal cache to calculate and keep the maximum total amount within a group.
 */
class MaxTotalCache {

	private final ViewSettings settings;
	private final ITreeContentProvider contentProvider;

	private Map<URI, Integer> maxTotals;

	MaxTotalCache(ViewSettings settings) {
		this.settings = settings;
		this.contentProvider = new WorkbenchContentProvider();
		this.maxTotals = new HashMap<>();
	}

	int getMaxTotal(Object element) {
		Integer max = maxTotals.get(element);
		if (max == null) {
			max = Integer.valueOf(calculateMaxTotal((URI) element));
			maxTotals.put((URI) element, max);
		}
		return max.intValue();
	}

	private int calculateMaxTotal(URI uri) {
		int max = 0;
		// for (Object sibling : contentProvider.getChildren(parent)) {
		final ICoverageNode coverage = CoverageTools.getCoverageInfo(uri);
		if (coverage != null) {
			max = Math.max(max, coverage.getCounter(settings.getCounters()).getTotalCount());
		}
		// }
		return max;
	}

	void reset() {
		maxTotals.clear();
	}

}
