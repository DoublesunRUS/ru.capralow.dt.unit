/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.dialogs;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ICoverageNode;

import ru.capralow.dt.coverage.CoverageTools;
import ru.capralow.dt.coverage.ICoverageSession;
import ru.capralow.dt.coverage.internal.ui.ContextHelp;
import ru.capralow.dt.coverage.internal.ui.RedGreenBar;
import ru.capralow.dt.coverage.internal.ui.UiMessages;

/**
 * Property page for coverage details of a Java element.
 */
public class CoveragePropertyPage
    extends PropertyPage
{

    private static final NumberFormat COVERAGE_VALUE =
        new DecimalFormat(UiMessages.CoveragePropertyPageColumnCoverage_value);

    private static final NumberFormat COUNTER_VALUE = NumberFormat.getIntegerInstance();

    private static String getSessionDescription()
    {
        ICoverageSession session = CoverageTools.getSessionManager().getActiveSession();
        return session == null ? UiMessages.CoveragePropertyPageNoSession_value : session.getDescription();
    }

    private void createColumn(TableViewer viewer, int align, int width, String caption, CellLabelProvider labelProvider)
    {
        TableViewerColumn column = new TableViewerColumn(viewer, align);
        column.getColumn().setText(caption);
        column.getColumn().setWidth(convertWidthInCharsToPixels(width));
        column.setLabelProvider(labelProvider);
    }

    private Control createTable(Composite parent)
    {
        final Table table = new Table(parent, SWT.BORDER);
        initializeDialogUnits(table);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        TableViewer viewer = new TableViewer(table);
        createColumn(viewer, SWT.LEFT, 20, UiMessages.CoveragePropertyPageColumnCounter_label, new CellLabelProvider()
        {
            @Override
            public void update(ViewerCell cell)
            {
                final Line line = (Line)cell.getElement();
                cell.setText(line.label);
            }
        });
        createColumn(viewer, SWT.RIGHT, 20, UiMessages.CoveragePropertyPageColumnCoverage_label,
            new OwnerDrawLabelProvider()
            {
                @Override
                public void update(ViewerCell cell)
                {
                    final Line line = (Line)cell.getElement();
                    cell.setText(COVERAGE_VALUE.format(line.counter.getCoveredRatio()));
                }

                @Override
                protected void erase(Event event, Object element)
                {
                    // Нечего делать
                }

                @Override
                protected void measure(Event event, Object element)
                {
                    // Нечего делать
                }

                @Override
                protected void paint(Event event, Object element)
                {
                    final Line line = (Line)element;
                    RedGreenBar.draw(event, table.getColumn(1).getWidth(), line.counter);
                }
            });
        createColumn(viewer, SWT.RIGHT, 16, UiMessages.CoveragePropertyPageColumnCovered_label, new CellLabelProvider()
        {
            @Override
            public void update(ViewerCell cell)
            {
                final Line line = (Line)cell.getElement();
                cell.setText(COUNTER_VALUE.format(line.counter.getCoveredCount()));
            }
        });
        createColumn(viewer, SWT.RIGHT, 16, UiMessages.CoveragePropertyPageColumnMissed_label, new CellLabelProvider()
        {
            @Override
            public void update(ViewerCell cell)
            {
                final Line line = (Line)cell.getElement();
                cell.setText(COUNTER_VALUE.format(line.counter.getMissedCount()));
            }
        });
        createColumn(viewer, SWT.RIGHT, 16, UiMessages.CoveragePropertyPageColumnTotal_label, new CellLabelProvider()
        {
            @Override
            public void update(ViewerCell cell)
            {
                final Line line = (Line)cell.getElement();
                cell.setText(COUNTER_VALUE.format(line.counter.getTotalCount()));
            }
        });
        viewer.setContentProvider(ArrayContentProvider.getInstance());
        viewer.addFilter(new ViewerFilter()
        {
            @Override
            public boolean select(Viewer viewer2, Object parentElement, Object element)
            {
                return ((Line)element).counter.getTotalCount() != 0;
            }
        });
        viewer.setInput(getLines());
        return table;
    }

    private Line[] getLines()
    {
        ICoverageNode c = CoverageTools.getCoverageInfo(getElement());
        if (c == null)
            return new Line[0];

        return new Line[] { new Line(UiMessages.CoveragePropertyPageInstructions_label, c.getInstructionCounter()),
            new Line(UiMessages.CoveragePropertyPageBranches_label, c.getBranchCounter()),
            new Line(UiMessages.CoveragePropertyPageLines_label, c.getLineCounter()),
            new Line(UiMessages.CoveragePropertyPageMethods_label, c.getMethodCounter()),
            new Line(UiMessages.CoveragePropertyPageTypes_label, c.getClassCounter()),
            new Line(UiMessages.CoveragePropertyPageComplexity_label, c.getComplexityCounter()) };
    }

    @Override
    protected Control createContents(Composite parent)
    {
        ContextHelp.setHelp(parent, ContextHelp.COVERAGE_PROPERTIES);
        noDefaultAndApplyButton();
        Composite main = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        main.setLayout(layout);

        Label l1 = new Label(main, SWT.NONE);
        l1.setText(UiMessages.CoveragePropertyPageSession_label);
        l1.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

        Text t1 = new Text(main, SWT.READ_ONLY | SWT.WRAP);
        t1.setText(getSessionDescription());
        t1.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
        t1.setBackground(t1.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

        Control table = createTable(main);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.horizontalSpan = 2;
        table.setLayoutData(gd);

        return main;
    }

    private static class Line
    {
        public final String label;
        public final ICounter counter;

        Line(String label, ICounter counter)
        {
            this.label = label;
            this.counter = counter;
        }
    }

}
