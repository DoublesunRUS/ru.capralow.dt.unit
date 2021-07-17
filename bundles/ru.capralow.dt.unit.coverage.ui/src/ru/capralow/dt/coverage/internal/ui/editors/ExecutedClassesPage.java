/**
 * Copyright (c) 2020, Aleksandr Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.editors;

import static ru.capralow.dt.coverage.internal.ui.UiMessages.ExecutionDataEditorExecutedClassesPageColumnExecutedProbes_label;
import static ru.capralow.dt.coverage.internal.ui.UiMessages.ExecutionDataEditorExecutedClassesPageColumnId_label;
import static ru.capralow.dt.coverage.internal.ui.UiMessages.ExecutionDataEditorExecutedClassesPageColumnName_label;
import static ru.capralow.dt.coverage.internal.ui.UiMessages.ExecutionDataEditorExecutedClassesPageColumnTotalProbes_label;
import static ru.capralow.dt.coverage.internal.ui.UiMessages.ExecutionDataEditorExecutedClassesPageFilter_message;
import static ru.capralow.dt.coverage.internal.ui.UiMessages.ExecutionDataEditorExecutedClassesPageRefreshing_task;
import static ru.capralow.dt.coverage.internal.ui.UiMessages.ExecutionDataEditorExecutedClassesPage_title;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.progress.UIJob;
import org.jacoco.core.data.ExecutionData;

class ExecutedClassesPage
    extends FormPage
{

    private final ExecutionDataContent content;
    private final Job refreshJob;

    private TableViewer dataTableViewer;
    private Text filter;

    ExecutedClassesPage(FormEditor parent, ExecutionDataContent content)
    {
        super(parent, "classes", ExecutionDataEditorExecutedClassesPage_title); //$NON-NLS-1$
        this.content = content;
        this.refreshJob = new RefreshJob();
    }

    private void triggerRefresh()
    {
        refreshJob.cancel();
        refreshJob.schedule(250L);
    }

    @Override
    protected void createFormContent(IManagedForm managedForm)
    {
        final FormToolkit toolkit = managedForm.getToolkit();

        final ScrolledForm form = managedForm.getForm();
        form.setText(ExecutionDataEditorExecutedClassesPage_title);
        toolkit.decorateFormHeading(form.getForm());

        final Composite body = form.getBody();
        body.setLayout(new org.eclipse.swt.layout.GridLayout(1, true));

        filter = toolkit.createText(body, null, SWT.SINGLE | SWT.SEARCH | SWT.ICON_CANCEL);
        filter.addModifyListener(e -> triggerRefresh());
        filter.setMessage(ExecutionDataEditorExecutedClassesPageFilter_message);
        filter.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        final Table dataTable = toolkit.createTable(body, SWT.VIRTUAL | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.BORDER);
        dataTable.setHeaderVisible(true);
        dataTable.setLinesVisible(true);
        dataTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        dataTableViewer = new TableViewer(dataTable);

        final TableViewerColumn classIdColumnViewer = new TableViewerColumn(dataTableViewer, SWT.NONE);
        classIdColumnViewer.setLabelProvider(new ClassIdLabelProvider());
        final TableColumn classIdColumn = classIdColumnViewer.getColumn();
        classIdColumn.setText(ExecutionDataEditorExecutedClassesPageColumnId_label);
        classIdColumn.setWidth(200);
        classIdColumn.setResizable(true);

        final TableViewerColumn vmNameColumnViewer = new TableViewerColumn(dataTableViewer, SWT.NONE);
        vmNameColumnViewer.setLabelProvider(new VmNameLabelProvider());
        final TableColumn vmNameColumn = vmNameColumnViewer.getColumn();
        vmNameColumn.setText(ExecutionDataEditorExecutedClassesPageColumnName_label);
        vmNameColumn.setWidth(500);
        vmNameColumn.setResizable(true);

        final TableViewerColumn totalProbesColumnViewer = new TableViewerColumn(dataTableViewer, SWT.RIGHT);
        totalProbesColumnViewer.setLabelProvider(new TotalProbesLabelProvider());
        final TableColumn totalProbesColumn = totalProbesColumnViewer.getColumn();
        totalProbesColumn.setText(ExecutionDataEditorExecutedClassesPageColumnTotalProbes_label);
        totalProbesColumn.setWidth(100);
        totalProbesColumn.setResizable(true);

        final TableViewerColumn executedProbesColumnViewer = new TableViewerColumn(dataTableViewer, SWT.RIGHT);
        executedProbesColumnViewer.setLabelProvider(new ExecutedProbesLabelProvider());
        final TableColumn executedProbesColumn = executedProbesColumnViewer.getColumn();
        executedProbesColumn.setText(ExecutionDataEditorExecutedClassesPageColumnExecutedProbes_label);
        executedProbesColumn.setWidth(100);
        executedProbesColumn.setResizable(true);

        dataTable.setSortColumn(vmNameColumn);
        dataTable.setSortDirection(SWT.UP);
        dataTableViewer.setComparator(new ViewerComparator());
        dataTableViewer.setContentProvider(new AbstractExecutionDataContentProvider()
        {
            @Override
            public Object[] getElements(ExecutionDataContent content2)
            {
                return content2.getExecutionData();
            }
        });
        dataTableViewer.setInput(content);
    }

    private abstract static class AbstractExecutionDataColumnLabelProvider
        extends ColumnLabelProvider
    {
        public abstract String getText(ExecutionData element);

        @Override
        public final String getText(Object element)
        {
            return getText((ExecutionData)element);
        }
    }

    private static class ClassIdLabelProvider
        extends AbstractExecutionDataColumnLabelProvider
    {
        @Override
        public Font getFont(Object element)
        {
            return JFaceResources.getTextFont();
        }

        @Override
        public String getText(ExecutionData element)
        {
            return String.format("0x%016x", Long.valueOf(element.getId())); //$NON-NLS-1$
        }
    }

    private static class ExecutedProbesLabelProvider
        extends AbstractExecutionDataColumnLabelProvider
    {
        @Override
        public String getText(ExecutionData element)
        {
            int executed = 0;
            boolean[] data = element.getProbes();
            for (int i = 0; i < data.length; i++)
            {
                if (data[i])
                {
                    executed++;
                }
            }
            return Integer.toString(executed);
        }
    }

    private final class RefreshJob
        extends UIJob
    {

        RefreshJob()
        {
            super(ExecutionDataEditorExecutedClassesPageRefreshing_task);
            setSystem(true);
            setPriority(Job.SHORT);
            setUser(false);
        }

        @Override
        public IStatus runInUIThread(IProgressMonitor monitor)
        {
            dataTableViewer
                .setFilters(new ViewerFilter[] { ExecutedClassesFilters.fromPatternString(filter.getText().trim()) });
            return Status.OK_STATUS;
        }
    }

    private static class TotalProbesLabelProvider
        extends AbstractExecutionDataColumnLabelProvider
    {
        @Override
        public String getText(ExecutionData element)
        {
            return Integer.toString(element.getProbes().length);
        }
    }

    private static class VmNameLabelProvider
        extends AbstractExecutionDataColumnLabelProvider
    {
        @Override
        public String getText(ExecutionData element)
        {
            return element.getName();
        }
    }

}
