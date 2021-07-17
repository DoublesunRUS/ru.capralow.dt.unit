/**
 * Copyright (c) 2020, Aleksandr Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.dialogs;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferenceLinkArea;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import ru.capralow.dt.coverage.internal.ui.ContextHelp;
import ru.capralow.dt.coverage.internal.ui.CoverageUiPlugin;
import ru.capralow.dt.coverage.internal.ui.UiMessages;
import ru.capralow.dt.coverage.internal.ui.UiPreferences;

/**
 * Implementation of the "Code Coverage" preferences page.
 */
public class CoveragePreferencePage
    extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage
{

    private static final String DECORATORS_PAGE = "org.eclipse.ui.preferencePages.Decorators"; //$NON-NLS-1$
    private static final String ANNOTATIONS_PAGE = "org.eclipse.ui.editors.preferencePages.Annotations"; //$NON-NLS-1$

    private static void adjustGroupLayout(final Group group)
    {
        // Unlike the top level controls we need margins for a control in a group:
        GridLayout layout = (GridLayout)group.getLayout();
        layout.marginWidth = 5;
        layout.marginHeight = 5;
    }

    private static Group createGroup(final Composite parent, final String text)
    {
        final Group group = new Group(parent, SWT.NONE);
        group.setText(text);
        group.setFont(parent.getFont());
        GridDataFactory.fillDefaults().applyTo(group);
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        return group;
    }

    public CoveragePreferencePage()
    {
        super();
        setPreferenceStore(CoverageUiPlugin.getInstance().getPreferenceStore());
    }

    @Override
    public void init(IWorkbench workbench)
    {
        // nothing to do here
    }

    private void createCoverageRuntimeGroup(final Composite parent)
    {
        FieldEditor editor;
        final Group group = createGroup(parent, UiMessages.CoveragePreferencesCoverageRuntime_title);
        editor = new StringFieldEditor(UiPreferences.PREF_AGENT_INCLUDES, UiMessages.CoveragePreferencesIncludes_label,
            group);
        addField(editor);
        editor.fillIntoGrid(group, 2);
        editor = new StringFieldEditor(UiPreferences.PREF_AGENT_EXCLUDES, UiMessages.CoveragePreferencesExcludes_label,
            group);
        addField(editor);
        editor.fillIntoGrid(group, 2);
        editor = new StringFieldEditor(UiPreferences.PREF_AGENT_EXCLCLASSLOADER,
            UiMessages.CoveragePreferencesExcludeClassloaders_label, group);
        addField(editor);
        editor.fillIntoGrid(group, 2);
        Label hint = new Label(group, SWT.WRAP);
        GridDataFactory.fillDefaults().span(2, 1).applyTo(hint);
        hint.setText(UiMessages.CoveragePreferencesCoverageRuntime_message);
        adjustGroupLayout(group);
    }

    private void createDefaultScopeGroup(final Composite parent)
    {
        FieldEditor editor;
        final Group group = createGroup(parent, UiMessages.CoveragePreferencesDefaultScope_title);
        editor = new BooleanFieldEditor(UiPreferences.PREF_DEFAULT_SCOPE_SOURCE_FOLDERS_ONLY,
            UiMessages.CoveragePreferencesSourceFoldersOnly_label, group);
        addField(editor);
        editor.fillIntoGrid(group, 2);
        editor = new BooleanFieldEditor(UiPreferences.PREF_DEFAULT_SCOPE_SAME_PROJECT_ONLY,
            UiMessages.CoveragePreferencesSameProjectOnly_label, group);
        addField(editor);
        editor.fillIntoGrid(group, 2);
        editor = new StringFieldEditor(UiPreferences.PREF_DEFAULT_SCOPE_FILTER,
            UiMessages.CoveragePreferencesClasspathFilter_label, group);
        addField(editor);
        editor.fillIntoGrid(group, 2);
        adjustGroupLayout(group);
    }

    private void createLink(final Composite parent, final String text, String target)
    {
        final PreferenceLinkArea link =
            new PreferenceLinkArea(parent, SWT.NONE, target, text, (IWorkbenchPreferenceContainer)getContainer(), null);
        link.getControl().setLayoutData(new GridData());
    }

    private void createSessionManagementGroup(final Composite parent)
    {
        FieldEditor editor;
        final Group group = createGroup(parent, UiMessages.CoverageSessionManagement_titel);
        editor = new BooleanFieldEditor(UiPreferences.PREF_SHOW_COVERAGE_VIEW,
            UiMessages.CoveragePreferencesShowCoverageView_label, group);
        addField(editor);
        editor.fillIntoGrid(group, 2);
        editor = new BooleanFieldEditor(UiPreferences.PREF_ACTICATE_NEW_SESSIONS,
            UiMessages.CoveragePreferencesActivateNewSessions_label, group);
        addField(editor);
        editor.fillIntoGrid(group, 2);
        editor = new BooleanFieldEditor(UiPreferences.PREF_AUTO_REMOVE_SESSIONS,
            UiMessages.CoveragePreferencesAutoRemoveSessions_label, group);
        addField(editor);
        editor = new BooleanFieldEditor(UiPreferences.PREF_RESET_ON_DUMP,
            UiMessages.CoveragePreferencesResetOnDump_label, group);
        addField(editor);
        editor.fillIntoGrid(group, 2);
        adjustGroupLayout(group);
    }

    @Override
    protected Control createContents(final Composite parent)
    {
        ContextHelp.setHelp(parent, ContextHelp.COVERAGE_PREFERENCES);

        final Composite result = new Composite(parent, SWT.NONE);
        GridLayoutFactory.swtDefaults().margins(0, 0).applyTo(result);

        createSessionManagementGroup(result);
        createDefaultScopeGroup(result);
        createCoverageRuntimeGroup(result);

        // Links:
        createLink(result, UiMessages.CoveragePreferencesDecoratorsLink_label, DECORATORS_PAGE);
        createLink(result, UiMessages.CoveragePreferencesAnnotationsLink_label, ANNOTATIONS_PAGE);

        initialize();
        checkState();
        return result;
    }

    @Override
    protected void createFieldEditors()
    {
        // we override createContents()
    }

}
