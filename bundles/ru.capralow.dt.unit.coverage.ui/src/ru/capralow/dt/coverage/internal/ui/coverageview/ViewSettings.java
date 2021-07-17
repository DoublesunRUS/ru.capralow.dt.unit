/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.coverageview;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IMemento;
import org.jacoco.core.analysis.ICoverageNode.CounterEntity;
import org.jacoco.core.analysis.ICoverageNode.ElementType;

import ru.capralow.dt.coverage.internal.ui.UiMessages;

/**
 * All setting for the coverage view that will become persisted in the view's
 * memento.
 */
public class ViewSettings
{

    private static final String KEY_SORTCOLUMN = "sortcolumn"; //$NON-NLS-1$
    private static final String KEY_REVERSESORT = "reversesort"; //$NON-NLS-1$
    private static final String KEY_COUNTERS = "counters"; //$NON-NLS-1$
    private static final String KEY_HIDEUNUSEDELEMENTS = "hideunusedelements"; //$NON-NLS-1$
    private static final String KEY_ROOTTYPE = "roottype"; //$NON-NLS-1$
    private static final String KEY_COLUMN0 = "column0"; //$NON-NLS-1$
    private static final String KEY_COLUMN1 = "column1"; //$NON-NLS-1$
    private static final String KEY_COLUMN2 = "column2"; //$NON-NLS-1$
    private static final String KEY_COLUMN3 = "column3"; //$NON-NLS-1$
    private static final String KEY_COLUMN4 = "column4"; //$NON-NLS-1$
    private static final String KEY_LINKED = "linked"; //$NON-NLS-1$

    private static final Map<CounterEntity, String[]> COLUMNS_HEADERS = new HashMap<>();

    static
    {
        COLUMNS_HEADERS.put(CounterEntity.INSTRUCTION,
            new String[] { UiMessages.CoverageViewColumnElement_label, UiMessages.CoverageViewColumnCoverage_label,
                UiMessages.CoverageViewColumnCoveredInstructions_label,
                UiMessages.CoverageViewColumnMissedInstructions_label,
                UiMessages.CoverageViewColumnTotalInstructions_label });
        COLUMNS_HEADERS.put(CounterEntity.BRANCH,
            new String[] { UiMessages.CoverageViewColumnElement_label, UiMessages.CoverageViewColumnCoverage_label,
                UiMessages.CoverageViewColumnCoveredBranches_label, UiMessages.CoverageViewColumnMissedBranches_label,
                UiMessages.CoverageViewColumnTotalBranches_label });
        COLUMNS_HEADERS.put(CounterEntity.LINE,
            new String[] { UiMessages.CoverageViewColumnElement_label, UiMessages.CoverageViewColumnCoverage_label,
                UiMessages.CoverageViewColumnCoveredLines_label, UiMessages.CoverageViewColumnMissedLines_label,
                UiMessages.CoverageViewColumnTotalLines_label });
        COLUMNS_HEADERS.put(CounterEntity.METHOD,
            new String[] { UiMessages.CoverageViewColumnElement_label, UiMessages.CoverageViewColumnCoverage_label,
                UiMessages.CoverageViewColumnCoveredMethods_label, UiMessages.CoverageViewColumnMissedMethods_label,
                UiMessages.CoverageViewColumnTotalMethods_label });
        COLUMNS_HEADERS.put(CounterEntity.CLASS,
            new String[] { UiMessages.CoverageViewColumnElement_label, UiMessages.CoverageViewColumnCoverage_label,
                UiMessages.CoverageViewColumnCoveredTypes_label, UiMessages.CoverageViewColumnMissedTypes_label,
                UiMessages.CoverageViewColumnTotalTypes_label });
        COLUMNS_HEADERS.put(CounterEntity.COMPLEXITY, new String[] { UiMessages.CoverageViewColumnElement_label,
            UiMessages.CoverageViewColumnCoverage_label, UiMessages.CoverageViewColumnCoveredComplexity_label,
            UiMessages.CoverageViewColumnMissedComplexity_label, UiMessages.CoverageViewColumnTotalComplexity_label });
    }

    private static final int[] DEFAULT_COLUMNWIDTH = new int[] { 300, 80, 120, 120, 120 };

    private static boolean getBoolean(IMemento memento, String key, boolean preset)
    {
        if (memento == null)
            return preset;

        Boolean b = memento.getBoolean(key);
        return b == null ? preset : b.booleanValue();
    }

    private static <T extends Enum<T>> T getEnum(IMemento memento, String key, Class<T> type, T preset)
    {
        if (memento == null)
            return preset;

        final String s = memento.getString(key);
        if (s == null)
            return preset;

        try
        {
            return Enum.valueOf(type, s);

        }
        catch (IllegalArgumentException e)
        {
            return preset;

        }
    }

    private static int getInt(IMemento memento, String key, int preset)
    {
        if (memento == null)
            return preset;

        Integer i = memento.getInteger(key);
        return i == null ? preset : i.intValue();
    }

    private static int getWidth(IMemento memento, String key, int preset)
    {
        final int w = getInt(memento, key, preset);
        return w == 0 ? preset : w;
    }

    private int sortcolumn;
    private boolean reversesort;
    private CounterEntity counters;

    private ElementType roottype;

    private boolean hideunusedelements;

    private int[] columnwidths = new int[5];

    private boolean linked;

    public String[] getColumnHeaders()
    {
        return COLUMNS_HEADERS.get(counters);
    }

    public CounterEntity getCounters()
    {
        return counters;
    }

    public boolean getHideUnusedElements()
    {
        return hideunusedelements;
    }

    public ElementType getRootType()
    {
        return roottype;
    }

    public int getSortColumn()
    {
        return sortcolumn;
    }

    public void init(IMemento memento)
    {
        sortcolumn = getInt(memento, KEY_SORTCOLUMN, CoverageView.COLUMN_MISSED);
        reversesort = getBoolean(memento, KEY_REVERSESORT, true);
        counters = getEnum(memento, KEY_COUNTERS, CounterEntity.class, CounterEntity.INSTRUCTION);
        roottype = getEnum(memento, KEY_ROOTTYPE, ElementType.class, ElementType.GROUP);
        hideunusedelements = getBoolean(memento, KEY_HIDEUNUSEDELEMENTS, false);
        columnwidths[0] = getWidth(memento, KEY_COLUMN0, DEFAULT_COLUMNWIDTH[0]);
        columnwidths[1] = getWidth(memento, KEY_COLUMN1, DEFAULT_COLUMNWIDTH[1]);
        columnwidths[2] = getWidth(memento, KEY_COLUMN2, DEFAULT_COLUMNWIDTH[2]);
        columnwidths[3] = getWidth(memento, KEY_COLUMN3, DEFAULT_COLUMNWIDTH[3]);
        columnwidths[4] = getWidth(memento, KEY_COLUMN4, DEFAULT_COLUMNWIDTH[4]);
        linked = getBoolean(memento, KEY_LINKED, false);
    }

    public boolean isLinked()
    {
        return linked;
    }

    public boolean isReverseSort()
    {
        return reversesort;
    }

    public void restoreColumnWidth(TreeViewer viewer)
    {
        final TreeColumn[] columns = viewer.getTree().getColumns();
        for (int i = 0; i < columnwidths.length; i++)
        {
            columns[i].setWidth(columnwidths[i]);
        }
    }

    public void save(IMemento memento)
    {
        memento.putInteger(KEY_SORTCOLUMN, sortcolumn);
        memento.putBoolean(KEY_REVERSESORT, reversesort);
        memento.putString(KEY_COUNTERS, counters.name());
        memento.putString(KEY_ROOTTYPE, roottype.name());
        memento.putBoolean(KEY_HIDEUNUSEDELEMENTS, hideunusedelements);
        memento.putInteger(KEY_COLUMN0, columnwidths[0]);
        memento.putInteger(KEY_COLUMN1, columnwidths[1]);
        memento.putInteger(KEY_COLUMN2, columnwidths[2]);
        memento.putInteger(KEY_COLUMN3, columnwidths[3]);
        memento.putInteger(KEY_COLUMN4, columnwidths[4]);
        memento.putBoolean(KEY_LINKED, linked);
    }

    public void setCounters(CounterEntity counters)
    {
        this.counters = counters;
    }

    public void setHideUnusedElements(boolean flag)
    {
        hideunusedelements = flag;
    }

    public void setLinked(boolean linked)
    {
        this.linked = linked;
    }

    public void setRootType(ElementType roottype)
    {
        this.roottype = roottype;
    }

    public void storeColumnWidth(TreeViewer viewer)
    {
        final TreeColumn[] columns = viewer.getTree().getColumns();
        for (int i = 0; i < columnwidths.length; i++)
        {
            columnwidths[i] = columns[i].getWidth();
        }
    }

    public void toggleSortColumn(int column)
    {
        if (sortcolumn == column)
        {
            reversesort = !reversesort;
        }
        else
        {
            reversesort = false;
            sortcolumn = column;
        }
    }

    public void updateColumnHeaders(TreeViewer viewer)
    {
        final String[] headers = COLUMNS_HEADERS.get(counters);
        final TreeColumn[] columns = viewer.getTree().getColumns();
        for (int i = 0; i < headers.length; i++)
        {
            columns[i].setText(headers[i]);
        }
    }

}
