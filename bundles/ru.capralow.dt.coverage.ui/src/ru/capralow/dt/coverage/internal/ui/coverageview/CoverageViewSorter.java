/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.coverageview;

import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TreeColumn;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ICoverageNode.CounterEntity;

import ru.capralow.dt.coverage.CoverageTools;

/**
 * Internal sorter for the coverage view.
 */
class CoverageViewSorter
    extends ViewerComparator
{

    private static void setSortColumnAndDirection(TreeColumn sortColumn, boolean reverse)
    {
        sortColumn.getParent().setSortColumn(sortColumn);
        sortColumn.getParent().setSortDirection(reverse ? SWT.DOWN : SWT.UP);
    }

    private final ViewSettings settings;
    private final CoverageView view;

    private final ViewerComparator elementsorter = new ViewerComparator();

    CoverageViewSorter(ViewSettings settings, CoverageView view)
    {
        this.settings = settings;
        this.view = view;
    }

    @Override
    public int compare(Viewer viewer, Object e1, Object e2)
    {
        CounterEntity counters = settings.getCounters();
        ICounter c1 = CoverageTools.getCoverageInfo(e1).getCounter(counters);
        ICounter c2 = CoverageTools.getCoverageInfo(e2).getCounter(counters);
        int res = 0;
        switch (settings.getSortColumn())
        {
        case CoverageView.COLUMN_ELEMENT:
            res = elementsorter.compare(viewer, e1, e2);
            break;
        case CoverageView.COLUMN_RATIO:
            res = Double.compare(c1.getCoveredRatio(), c2.getCoveredRatio());
            break;
        case CoverageView.COLUMN_COVERED:
            res = c1.getCoveredCount() - c2.getCoveredCount();
            break;
        case CoverageView.COLUMN_MISSED:
            res = c1.getMissedCount() - c2.getMissedCount();
            break;
        case CoverageView.COLUMN_TOTAL:
            res = c1.getTotalCount() - c2.getTotalCount();
            break;
        default:
            // Нечего делать
        }
        if (res == 0)
        {
            res = elementsorter.compare(viewer, e1, e2);
        }
        else
        {
            res = settings.isReverseSort() ? -res : res;
        }
        return res;
    }

    void addColumn(final TreeViewerColumn viewerColumn, final int columnidx)
    {
        final TreeColumn column = viewerColumn.getColumn();
        if (settings.getSortColumn() == columnidx)
        {
            setSortColumnAndDirection(column, settings.isReverseSort());
        }
        column.addSelectionListener(new SelectionListener()
        {
            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
                // Нечего делать
            }

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                settings.toggleSortColumn(columnidx);
                setSortColumnAndDirection(column, settings.isReverseSort());
                view.refreshViewer();
            }
        });
    }

}
