/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui;

/**
 * Help context ids for the JUnit UI.
 */
public final class IJunitHelpContextIds
{
    /**
     *
     */
    public static final String PREFIX = JUnitUiPlugin.ID + '.';

    // Actions
    /**
     *
     */
    public static final String COPYTRACE_ACTION = PREFIX + "copy_trace_action_context"; //$NON-NLS-1$
    /**
     *
     */
    public static final String COPYFAILURELIST_ACTION = PREFIX + "copy_failure_list_action_context"; //$NON-NLS-1$
    /**
     *
     */
    public static final String ENABLEFILTER_ACTION = PREFIX + "enable_filter_action_context"; //$NON-NLS-1$
    /**
     *
     */
    public static final String OPENEDITORATLINE_ACTION = PREFIX + "open_editor_atline_action_context"; //$NON-NLS-1$
    /**
     *
     */
    public static final String OPENTEST_ACTION = PREFIX + "open_test_action_context"; //$NON-NLS-1$
    /**
     *
     */
    public static final String RERUN_ACTION = PREFIX + "rerun_test_action_context"; //$NON-NLS-1$
    /**
     *
     */
    public static final String GOTO_REFERENCED_TEST_ACTION_CONTEXT = PREFIX + "goto_referenced_test_action_context"; //$NON-NLS-1$
    /**
     *
     */
    public static final String OUTPUT_SCROLL_LOCK_ACTION = PREFIX + "scroll_lock"; //$NON-NLS-1$

    // view parts
    /**
     *
     */
    public static final String RESULTS_VIEW = PREFIX + "results_view_context"; //$NON-NLS-1$
    /**
     *
     */
    public static final String RESULTS_VIEW_TOGGLE_ORIENTATION_ACTION =
        PREFIX + "results_view_toggle_call_mode_action_context"; //$NON-NLS-1$

    // Preference/Property pages
    /**
     *
     */
    public static final String JUNIT_PREFERENCE_PAGE = PREFIX + "junit_preference_page_context"; //$NON-NLS-1$

    // Wizard pages
    /**
     *
     */
    public static final String NEW_TESTCASE_WIZARD_PAGE = PREFIX + "new_testcase_wizard_page_context"; //$NON-NLS-1$
    /**
     *
     */
    public static final String NEW_TESTCASE_WIZARD_PAGE2 = PREFIX + "new_testcase_wizard_page2_context"; //$NON-NLS-1$
    /**
     *
     */
    public static final String NEW_TESTSUITE_WIZARD_PAGE = PREFIX + "new_testsuite_wizard_page2_context"; //$NON-NLS-1$
    /**
     *
     */
    public static final String LAUNCH_CONFIGURATION_DIALOG_JUNIT_MAIN_TAB =
        PREFIX + "launch_configuration_dialog_junit_main_tab"; //$NON-NLS-1$

    // Dialogs
    /**
     *
     */
    public static final String TEST_SELECTION_DIALOG = PREFIX + "test_selection_context"; //$NON-NLS-1$
    /**
     *
     */
    public static final String RESULT_COMPARE_DIALOG = PREFIX + "result_compare_context"; //$NON-NLS-1$

    private IJunitHelpContextIds()
    {

    }
}
