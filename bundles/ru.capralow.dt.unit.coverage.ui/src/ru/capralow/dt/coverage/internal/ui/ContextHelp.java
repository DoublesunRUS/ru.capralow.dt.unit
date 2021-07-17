/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal.ui;

import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;

/**
 * Constants and utility methods for context help.
 */
public final class ContextHelp
{

    private static final String PREFIX = CoverageUiPlugin.ID + "."; //$NON-NLS-1$

    public static final String COVERAGE_VIEW = PREFIX + "coverage_view_context"; //$NON-NLS-1$

    public static final String COVERAGE_PROPERTIES = PREFIX + "coverage_properties_context"; //$NON-NLS-1$

    public static final String COVERAGE_PREFERENCES = PREFIX + "coverage_preferences_context"; //$NON-NLS-1$

    public static final String COVERAGE_LAUNCH = PREFIX + "coverage_launch_context"; //$NON-NLS-1$

    public static final String COVERAGE_LAUNCH_TAB = PREFIX + "coverage_launch_tab_context"; //$NON-NLS-1$

    public static final String DUMP_EXECUTION_DATA = PREFIX + "dump_execution_data"; //$NON-NLS-1$

    public static final String SELECT_ACTIVE_SESSION = PREFIX + "select_active_session_context"; //$NON-NLS-1$

    public static final String MERGE_SESSIONS = PREFIX + "merge_sessions_context"; //$NON-NLS-1$

    public static final String SESSION_EXPORT = PREFIX + "session_export_context"; //$NON-NLS-1$

    public static final String SESSION_IMPORT = PREFIX + "session_import_context"; //$NON-NLS-1$

    /**
     * Assigns the given context help id to a SWT control.
     *
     * @param control control for this help context
     * @param id context help id
     */
    public static void setHelp(Control control, String id)
    {
        PlatformUI.getWorkbench().getHelpSystem().setHelp(control, id);
    }

    private ContextHelp()
    {
        // no instances
    }

}
