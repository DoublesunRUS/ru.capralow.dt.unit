/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.wizards;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com.google.inject.Inject;

import ru.capralow.dt.coverage.ScopeUtils;
import ru.capralow.dt.coverage.internal.ui.ContextHelp;
import ru.capralow.dt.coverage.internal.ui.ScopeViewer;
import ru.capralow.dt.coverage.internal.ui.UiMessages;

/**
 * This wizard page allows selecting a coverage file and class path entries for
 * import.
 */
public class SessionImportPage2
    extends WizardPage
{

    private static final String ID = "SessionImportPage2"; //$NON-NLS-1$

    private static final String STORE_PREFIX = ID + "."; //$NON-NLS-1$
    private static final String STORE_SCOPE = STORE_PREFIX + "scope"; //$NON-NLS-1$
    private static final String STORE_BINARIES = STORE_PREFIX + "binaries"; //$NON-NLS-1$

    private Text descriptiontext;
    private ScopeViewer scopeviewer;
    private Button binariescheck;

    @Inject
    private IV8ProjectManager projectManager;
    @Inject
    private IResourceLookup resourceLookup;

    protected SessionImportPage2()
    {
        super(ID);
        setTitle(UiMessages.ImportSessionPage1_title);
        setDescription(UiMessages.ImportSessionPage1_description);
    }

    @Override
    public void createControl(Composite parent)
    {
        initializeDialogUnits(parent);
        Composite main = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        main.setLayout(layout);
        createNameBlock(main);
        createScopeBlock(main);
        createButtonsBlock(main);
        setControl(main);
        ContextHelp.setHelp(main, ContextHelp.SESSION_IMPORT);
        restoreWidgetValues();
        update();
    }

    public Set<URI> getScope()
    {
        return scopeviewer.getSelectedScope();
    }

    public String getSessionDescription()
    {
        return descriptiontext.getText().trim();
    }

    public void saveWidgetValues()
    {
        IDialogSettings settings = getDialogSettings();
        settings.put(STORE_SCOPE, ScopeUtils.writeScope(scopeviewer.getSelectedScope()).toArray(new String[0]));
        settings.put(STORE_BINARIES, binariescheck.getSelection());
    }

    private void createButtonsBlock(Composite parent)
    {
        Composite main = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        main.setLayout(layout);
        main.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        binariescheck = new Button(main, SWT.CHECK);
        binariescheck.setText(UiMessages.ImportSessionPage1Binaries_label);
        binariescheck.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                // scopeviewer.setIncludeBinaries(binariescheck.getSelection());
                update();
            }
        });
        binariescheck.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL));
        Button buttonSelectAll = new Button(main, SWT.PUSH);
        buttonSelectAll.setText(UiMessages.SelectAllAction_label);
        buttonSelectAll.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                scopeviewer.selectAll();
                update();
            }
        });
        setButtonLayoutData(buttonSelectAll);
        Button buttonDeselectAll = new Button(main, SWT.PUSH);
        buttonDeselectAll.setText(UiMessages.DeselectAllAction_label);
        buttonDeselectAll.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                scopeviewer.deselectAll();
                update();
            }
        });
        setButtonLayoutData(buttonDeselectAll);
    }

    private void createNameBlock(Composite parent)
    {
        Composite main = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        main.setLayout(layout);
        main.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        new Label(main, SWT.NONE).setText(UiMessages.ImportSessionPage1Description_label);
        descriptiontext = new Text(main, SWT.BORDER);
        descriptiontext.addModifyListener(e -> update());
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        descriptiontext.setLayoutData(gd);
    }

    private void createScopeBlock(Composite parent)
    {
        scopeviewer = new ScopeViewer(parent, SWT.BORDER, projectManager, resourceLookup);
        scopeviewer.setInput(ScopeUtils.getWorkspaceScope());
        scopeviewer.addSelectionChangedListener(event -> update());
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.widthHint = convertHorizontalDLUsToPixels(120);
        gd.heightHint = convertHeightInCharsToPixels(8);
        scopeviewer.getTable().setLayoutData(gd);
    }

    private void restoreWidgetValues()
    {
        String descr = UiMessages.ImportSessionPage1Description_value;
        Object[] arg = new Object[] { new Date() };
        descriptiontext.setText(MessageFormat.format(descr, arg));
        IDialogSettings settings = getDialogSettings();
        boolean binaries = settings.getBoolean(STORE_BINARIES);
        // scopeviewer.setIncludeBinaries(binaries);
        binariescheck.setSelection(binaries);
        String[] classes = settings.getArray(STORE_SCOPE);
        if (classes != null)
        {
            // scopeviewer.setSelectedScope(ScopeUtils.readScope(Arrays.asList(classes),
            // bmEmfIndexManager));
        }
    }

    private void update()
    {
        if (getSessionDescription().length() == 0)
        {
            setErrorMessage(UiMessages.ImportSessionPage1NoDescription_message);
            setPageComplete(false);
            return;
        }
        if (getScope().isEmpty())
        {
            setErrorMessage(UiMessages.ImportSessionPage1NoClassFiles_message);
            setPageComplete(false);
            return;
        }
        setErrorMessage(null);
        setPageComplete(true);
    }

}
