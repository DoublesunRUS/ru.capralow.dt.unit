/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.editors;

import static ru.capralow.dt.coverage.internal.ui.UiMessages.ExecutionDataEditorSessionsPageColumnDumpTime_label;
import static ru.capralow.dt.coverage.internal.ui.UiMessages.ExecutionDataEditorSessionsPageColumnSessionId_label;
import static ru.capralow.dt.coverage.internal.ui.UiMessages.ExecutionDataEditorSessionsPageColumnStartTime_label;
import static ru.capralow.dt.coverage.internal.ui.UiMessages.ExecutionDataEditorSessionsPage_title;

import java.text.DateFormat;
import java.util.Date;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.jacoco.core.data.SessionInfo;

/**
 * Page to list session information.
 */
class SessionDataPage
    extends FormPage
{

    private final ExecutionDataContent content;
    private final DateFormat dateTimeFormat;

    SessionDataPage(FormEditor parent, ExecutionDataContent content)
    {
        super(parent, "sessions", ExecutionDataEditorSessionsPage_title); //$NON-NLS-1$
        this.content = content;
        this.dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
    }

    @Override
    protected void createFormContent(IManagedForm managedForm)
    {
        final FormToolkit toolkit = managedForm.getToolkit();

        final ScrolledForm form = managedForm.getForm();
        form.setText(ExecutionDataEditorSessionsPage_title);
        toolkit.decorateFormHeading(form.getForm());

        final Composite body = form.getBody();
        GridLayoutFactory.swtDefaults().applyTo(body);

        final Table sessionTable = toolkit.createTable(body, SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(sessionTable);
        sessionTable.setHeaderVisible(true);
        sessionTable.setLinesVisible(true);

        final TableViewer sessionTableViewer = new TableViewer(sessionTable);

        final TableViewerColumn sessionIdColumnViewer = new TableViewerColumn(sessionTableViewer, SWT.NONE);
        sessionIdColumnViewer.setLabelProvider(new SessionIdColumnLabelProvider());
        final TableColumn sessionIdColumn = sessionIdColumnViewer.getColumn();
        sessionIdColumn.setText(ExecutionDataEditorSessionsPageColumnSessionId_label);
        sessionIdColumn.setWidth(300);

        final TableViewerColumn startTimeColumnViewer = new TableViewerColumn(sessionTableViewer, SWT.NONE);
        startTimeColumnViewer.setLabelProvider(new StartTimeColumnLabelProvider());
        final TableColumn startTimeColumn = startTimeColumnViewer.getColumn();
        startTimeColumn.setText(ExecutionDataEditorSessionsPageColumnStartTime_label);
        startTimeColumn.setWidth(200);

        final TableViewerColumn dumpTimeColumnViewer = new TableViewerColumn(sessionTableViewer, SWT.NONE);
        dumpTimeColumnViewer.setLabelProvider(new DumpTimeColumnLabelProvider());
        final TableColumn dumpTimeColumn = dumpTimeColumnViewer.getColumn();
        dumpTimeColumn.setText(ExecutionDataEditorSessionsPageColumnDumpTime_label);
        dumpTimeColumn.setWidth(200);

        sessionTable.setSortColumn(startTimeColumn);
        sessionTable.setSortDirection(SWT.UP);

        sessionTableViewer.setContentProvider(new AbstractExecutionDataContentProvider()
        {
            @Override
            public Object[] getElements(ExecutionDataContent content2)
            {
                return content2.getSessionInfos();
            }
        });
        sessionTableViewer.setInput(content);
    }

    private final class DumpTimeColumnLabelProvider
        extends ColumnLabelProvider
    {

        @Override
        public String getText(Object element)
        {
            return dateTimeFormat.format(new Date(((SessionInfo)element).getDumpTimeStamp()));
        }
    }

    private static final class SessionIdColumnLabelProvider
        extends ColumnLabelProvider
    {

        @Override
        public String getText(Object element)
        {
            return ((SessionInfo)element).getId();
        }
    }

    private final class StartTimeColumnLabelProvider
        extends ColumnLabelProvider
    {

        @Override
        public String getText(Object element)
        {
            return dateTimeFormat.format(new Date(((SessionInfo)element).getStartTimeStamp()));
        }
    }

}
