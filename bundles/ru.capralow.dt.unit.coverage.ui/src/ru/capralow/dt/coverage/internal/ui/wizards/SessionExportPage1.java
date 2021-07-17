/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.wizards;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import ru.capralow.dt.coverage.CoverageTools;
import ru.capralow.dt.coverage.ICoverageSession;
import ru.capralow.dt.coverage.ISessionExporter.ExportFormat;
import ru.capralow.dt.coverage.internal.ui.ContextHelp;
import ru.capralow.dt.coverage.internal.ui.UiMessages;

/**
 * This wizard page allows selecting a coverage session, the output format and
 * destination.
 */
public class SessionExportPage1
    extends WizardPage
{

    private static final String ID = "SessionExportPage1"; //$NON-NLS-1$

    private static final String STORE_PREFIX = ID + "."; //$NON-NLS-1$
    private static final String STORE_FORMAT = STORE_PREFIX + "format"; //$NON-NLS-1$
    private static final String STORE_DESTINATIONS = STORE_PREFIX + "destinations"; //$NON-NLS-1$

    private static ExportFormat readFormat(IDialogSettings settings)
    {
        final String format = settings.get(STORE_FORMAT);
        if (format != null)
        {
            try
            {
                return ExportFormat.valueOf(format);
            }
            catch (IllegalArgumentException e)
            {
                // we fall-back to default
            }
        }
        return ExportFormat.HTML;
    }

    private TableViewer sessionstable;
    private ComboViewer formatcombo;

    private Combo destinationcombo;

    public SessionExportPage1()
    {
        super(ID);
        setTitle(UiMessages.ExportSessionPage1_title);
        setDescription(UiMessages.ExportSessionPage1_description);
    }

    @Override
    public void createControl(Composite parent)
    {
        initializeDialogUnits(parent);
        Composite main = new Composite(parent, SWT.NONE);
        main.setLayout(new GridLayout());
        new Label(main, SWT.NONE).setText(UiMessages.ExportSessionPage1Sessions_label);
        sessionstable = new TableViewer(main, SWT.BORDER);
        sessionstable.setLabelProvider(new WorkbenchLabelProvider());
        sessionstable.setContentProvider(ArrayContentProvider.getInstance());
        sessionstable.setInput(CoverageTools.getSessionManager().getSessions());
        ICoverageSession active = CoverageTools.getSessionManager().getActiveSession();
        if (active != null)
        {
            sessionstable.setSelection(new StructuredSelection(active));
        }
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = convertHeightInCharsToPixels(8);
        sessionstable.getControl().setLayoutData(gd);
        Group group = new Group(main, SWT.NONE);
        group.setText(UiMessages.ExportSessionPage1DestinationGroup_label);
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createExportOptionsGroup(group);
        setControl(main);
        ContextHelp.setHelp(main, ContextHelp.SESSION_EXPORT);
        restoreWidgetValues();
    }

    public String getDestination()
    {
        return destinationcombo.getText().trim();
    }

    public ExportFormat getExportFormat()
    {
        final IStructuredSelection selection = (IStructuredSelection)formatcombo.getSelection();
        return (ExportFormat)selection.getFirstElement();
    }

    public ICoverageSession getSelectedSession()
    {
        IStructuredSelection sel = (IStructuredSelection)sessionstable.getSelection();
        return (ICoverageSession)sel.getFirstElement();
    }

    public void saveWidgetValues()
    {
        IDialogSettings settings = getDialogSettings();
        settings.put(STORE_FORMAT, getExportFormat().name());
        WidgetHistory.saveCombo(settings, STORE_DESTINATIONS, destinationcombo);
    }

    private void createExportOptionsGroup(Composite parent)
    {
        parent.setLayout(new GridLayout(3, false));
        new Label(parent, SWT.NONE).setText(UiMessages.ExportSessionPage1Format_label);
        formatcombo = new ComboViewer(parent, SWT.READ_ONLY);
        formatcombo.setContentProvider(ArrayContentProvider.getInstance());
        formatcombo.setLabelProvider(new LabelProvider()
        {
            @Override
            public String getText(Object element)
            {
                return ((ExportFormat)element).getLabel();
            }
        });
        formatcombo.setInput(ExportFormat.values());
        formatcombo.addSelectionChangedListener(event -> {
            IPath path = Path.fromOSString(destinationcombo.getText());
            path = path.removeFileExtension();
            final ExportFormat format = getExportFormat();
            if (!format.isFolderOutput())
            {
                path = path.addFileExtension(format.getFileExtension());
            }
            destinationcombo.setText(path.toOSString());
        });
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        formatcombo.getControl().setLayoutData(gd);
        new Label(parent, SWT.NONE).setText(UiMessages.ExportSessionPage1Destination_label);
        destinationcombo = new Combo(parent, SWT.BORDER);
        destinationcombo.addModifyListener(e -> update());
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.widthHint = convertHorizontalDLUsToPixels(120);
        destinationcombo.setLayoutData(gd);
        Button browsebutton = new Button(parent, SWT.NONE);
        browsebutton.setText(UiMessages.BrowseAction_label);
        setButtonLayoutData(browsebutton);
        browsebutton.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                if (getExportFormat().isFolderOutput())
                {
                    openFolderDialog();
                }
                else
                {
                    openFileDialog();
                }
            }
        });
        update();
    }

    private void openFileDialog()
    {
        FileDialog fd = new FileDialog(getShell(), SWT.SAVE);
        fd.setText(UiMessages.ExportSessionPage1BrowseDialog_title);
        fd.setFileName(destinationcombo.getText());
        String ext = getExportFormat().getFileExtension();
        fd.setFilterExtensions(new String[] { "*." + ext, "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
        String file = fd.open();
        if (file != null)
        {
            destinationcombo.setText(file);
        }
    }

    private void openFolderDialog()
    {
        final DirectoryDialog fd = new DirectoryDialog(getShell(), SWT.NONE);
        fd.setText(UiMessages.ExportSessionPage1BrowseDialog_title);
        fd.setFilterPath(destinationcombo.getText());
        final String folder = fd.open();
        if (folder != null)
        {
            destinationcombo.setText(folder);
        }
    }

    private void restoreWidgetValues()
    {
        IDialogSettings settings = getDialogSettings();
        formatcombo.setSelection(new StructuredSelection(readFormat(settings)));
        WidgetHistory.restoreCombo(settings, STORE_DESTINATIONS, destinationcombo);
    }

    private void update()
    {
        // make sure we have a session to export
        if (getSelectedSession() == null)
        {
            setErrorMessage(UiMessages.ExportSessionPage1NoSession_message);
            setPageComplete(false);
            return;
        }
        // a destination file must be spezified
        if (getDestination().length() == 0)
        {
            setMessage(UiMessages.ExportSessionPage1MissingDestination_message);
            setPageComplete(false);
            return;
        }
        final ExportFormat format = getExportFormat();
        if (!format.isFolderOutput())
        {
            // the extension should correspond to the report type
            String exta = Path.fromOSString(getDestination()).getFileExtension();
            String exte = format.getFileExtension();
            if (!exte.equalsIgnoreCase(exta))
            {
                setMessage(NLS.bind(UiMessages.ExportSessionPage1WrongExtension_message, exte), WARNING);
                setPageComplete(true);
                return;
            }
        }
        setErrorMessage(null);
        setMessage(null);
        setPageComplete(true);
    }

}
