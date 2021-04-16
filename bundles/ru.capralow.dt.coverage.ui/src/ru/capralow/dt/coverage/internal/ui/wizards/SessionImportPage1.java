/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.wizards;

import static ru.capralow.dt.coverage.internal.ui.UiMessages.BrowseAction_label;
import static ru.capralow.dt.coverage.internal.ui.UiMessages.ImportSessionPage1BrowseDialog_title;
import static ru.capralow.dt.coverage.internal.ui.UiMessages.ImportSessionPage1Copy_label;
import static ru.capralow.dt.coverage.internal.ui.UiMessages.ImportSessionPage1ExecutionDataFile_label;
import static ru.capralow.dt.coverage.internal.ui.UiMessages.ImportSessionPage1ExecutionDataUrl_label;
import static ru.capralow.dt.coverage.internal.ui.UiMessages.ImportSessionPage1ModeGroup_label;
import static ru.capralow.dt.coverage.internal.ui.UiMessages.ImportSessionPage1NoExecutionDataFile_message;
import static ru.capralow.dt.coverage.internal.ui.UiMessages.ImportSessionPage1Reference_label;
import static ru.capralow.dt.coverage.internal.ui.UiMessages.ImportSessionPage1_description;
import static ru.capralow.dt.coverage.internal.ui.UiMessages.ImportSessionPage1_title;

import java.io.File;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.jacoco.core.runtime.AgentOptions;

import ru.capralow.dt.coverage.IExecutionDataSource;
import ru.capralow.dt.coverage.internal.ui.ContextHelp;

/**
 * This wizard page allows selecting a coverage file and class path entries for
 * import.
 */
public class SessionImportPage1
    extends WizardPage
{

    private static final String ID = "SessionImportPage1"; //$NON-NLS-1$

    private static final String STORE_PREFIX = ID + "."; //$NON-NLS-1$
    private static final String STORE_SOURCE = STORE_PREFIX + "source"; //$NON-NLS-1$
    private static final String STORE_FILES = STORE_PREFIX + "files"; //$NON-NLS-1$
    private static final String STORE_URLS = STORE_PREFIX + "urls"; //$NON-NLS-1$
    private static final String STORE_ADDRESS = STORE_PREFIX + "address"; //$NON-NLS-1$
    private static final String STORE_PORT = STORE_PREFIX + "port"; //$NON-NLS-1$
    private static final String STORE_RESET = STORE_PREFIX + "reset"; //$NON-NLS-1$
    private static final String STORE_COPY = STORE_PREFIX + "copy"; //$NON-NLS-1$

    private Button fileradio;
    private Button urlradio;
    private Button agentradio;
    private Combo filecombo;
    private Button browsebutton;
    private Combo urlcombo;
    private Text addresstext;
    private Text porttext;
    private Button resetcheck;
    private Button referenceradio;
    private Button copyradio;

    private IExecutionDataSource dataSource;

    protected SessionImportPage1()
    {
        super(ID);
        setTitle(ImportSessionPage1_title);
        setDescription(ImportSessionPage1_description);
    }

    @Override
    public void createControl(Composite parent)
    {
        initializeDialogUnits(parent);
        Composite main = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        main.setLayout(layout);
        Composite sourceGroup = new Composite(main, SWT.NONE);
        GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(sourceGroup);
        GridLayoutFactory.swtDefaults().numColumns(5).applyTo(sourceGroup);
        createFileBlock(sourceGroup);
        createUrlBlock(sourceGroup);
        createOptionsBlock(main);
        setControl(main);
        ContextHelp.setHelp(main, ContextHelp.SESSION_IMPORT);
        restoreWidgetValues();
        updateStatus();
    }

    public boolean getCreateCopy()
    {
        return copyradio.getSelection();
    }

    public IExecutionDataSource getExecutionDataSource()
    {
        return dataSource;
    }

    public void saveWidgetValues()
    {
        IDialogSettings settings = getDialogSettings();
        WidgetHistory.saveRadio(settings, STORE_SOURCE, fileradio, urlradio, agentradio);
        WidgetHistory.saveCombo(settings, STORE_FILES, filecombo);
        WidgetHistory.saveCombo(settings, STORE_URLS, urlcombo);
        WidgetHistory.saveText(settings, STORE_ADDRESS, addresstext);
        WidgetHistory.saveText(settings, STORE_PORT, porttext);
        WidgetHistory.saveCheck(settings, STORE_RESET, resetcheck);
        WidgetHistory.saveRadio(settings, STORE_COPY, referenceradio, copyradio);
    }

    private void createFileBlock(Composite parent)
    {
        fileradio = new Button(parent, SWT.RADIO);
        fileradio.setText(ImportSessionPage1ExecutionDataFile_label);
        fileradio.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                updateEnablement();
                updateStatus();
            }
        });
        filecombo = new Combo(parent, SWT.BORDER);
        filecombo.addModifyListener(e -> updateStatus());
        GridDataFactory.swtDefaults()
            .span(3, 1)
            .grab(true, false)
            .align(SWT.FILL, SWT.CENTER)
            .hint(convertHorizontalDLUsToPixels(80), SWT.DEFAULT)
            .applyTo(filecombo);
        browsebutton = new Button(parent, SWT.NONE);
        browsebutton.setText(BrowseAction_label);
        GridDataFactory.swtDefaults()
            .hint(convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH), SWT.DEFAULT)
            .applyTo(browsebutton);
        browsebutton.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                openBrowseDialog();
            }
        });
    }

    private void createOptionsBlock(Composite parent)
    {
        parent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        Group group = new Group(parent, SWT.NONE);
        group.setText(ImportSessionPage1ModeGroup_label);
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        group.setLayout(new GridLayout());
        referenceradio = new Button(group, SWT.RADIO);
        referenceradio.setText(ImportSessionPage1Reference_label);
        copyradio = new Button(group, SWT.RADIO);
        copyradio.setText(ImportSessionPage1Copy_label);
    }

    private void createUrlBlock(Composite parent)
    {
        urlradio = new Button(parent, SWT.RADIO);
        urlradio.setText(ImportSessionPage1ExecutionDataUrl_label);
        urlradio.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                updateEnablement();
                updateStatus();
            }
        });
        urlcombo = new Combo(parent, SWT.BORDER);
        urlcombo.addModifyListener(e -> updateStatus());
        GridDataFactory.swtDefaults().span(4, 1).align(SWT.FILL, SWT.CENTER).applyTo(urlcombo);
    }

    private void openBrowseDialog()
    {
        FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
        fd.setText(ImportSessionPage1BrowseDialog_title);
        fd.setFileName(filecombo.getText());
        fd.setFilterExtensions(new String[] { "*.exec", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
        String file = fd.open();
        if (file != null)
        {
            filecombo.setText(file);
        }
    }

    private void restoreWidgetValues()
    {
        IDialogSettings settings = getDialogSettings();
        WidgetHistory.restoreRadio(settings, STORE_SOURCE, fileradio, urlradio, agentradio);
        WidgetHistory.restoreCombo(settings, STORE_FILES, filecombo);
        WidgetHistory.restoreCombo(settings, STORE_URLS, urlcombo);
        WidgetHistory.restoreText(settings, STORE_ADDRESS, addresstext, "127.0.0.1"); //$NON-NLS-1$
        WidgetHistory.restoreText(settings, STORE_PORT, porttext, String.valueOf(AgentOptions.DEFAULT_PORT));
        WidgetHistory.restoreCheck(settings, STORE_RESET, resetcheck);
        WidgetHistory.restoreRadio(settings, STORE_COPY, referenceradio, copyradio);
        updateEnablement();
    }

    private void updateEnablement()
    {
        filecombo.setEnabled(fileradio.getSelection());
        browsebutton.setEnabled(fileradio.getSelection());
        urlcombo.setEnabled(urlradio.getSelection());
        addresstext.setEnabled(agentradio.getSelection());
        porttext.setEnabled(agentradio.getSelection());
        resetcheck.setEnabled(agentradio.getSelection());
    }

    private void updateStatus()
    {
        dataSource = null;
        if (fileradio.getSelection())
        {
            File execfile = new File(filecombo.getText());
            if (!execfile.exists() || !execfile.isFile())
            {
                setErrorMessage(ImportSessionPage1NoExecutionDataFile_message);
                setPageComplete(false);
                return;
            }
        }
        setErrorMessage(null);
        setPageComplete(true);
    }

}
