/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.coverageview;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.jacoco.core.analysis.ICoverageNode;

import ru.capralow.dt.coverage.CoverageTools;

/**
 * Internal cache to calculate and keep the maximum total amount within a group.
 */
class MaxTotalCache
{

    private final ViewSettings settings;
    private final ITreeContentProvider contentProvider;

    private Map<URI, Integer> maxTotals;

    MaxTotalCache(ViewSettings settings)
    {
        this.settings = settings;
        this.contentProvider = new WorkbenchContentProvider();
        this.maxTotals = new HashMap<>();
    }

    private int calculateMaxTotal(URI uri)
    {
        int max = 0;
        // for (Object sibling : contentProvider.getChildren(parent)) {
        final ICoverageNode coverage = CoverageTools.getCoverageInfo(uri);
        if (coverage != null)
        {
            max = Math.max(max, coverage.getCounter(settings.getCounters()).getTotalCount());
        }
        // }
        return max;
    }

    int getMaxTotal(Object element)
    {
        Integer max = maxTotals.get(element);
        if (max == null)
        {
            max = Integer.valueOf(calculateMaxTotal((URI)element));
            maxTotals.put((URI)element, max);
        }
        return max.intValue();
    }

    void reset()
    {
        maxTotals.clear();
    }

}
