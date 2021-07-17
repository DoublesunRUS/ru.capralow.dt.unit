package ru.capralow.dt.unit.internal.junit.ui;

/**
 * Help context ids for the JUnit UI.
 */
public interface IJUnitHelpContextIds
{
    String PREFIX = JUnitUiPlugin.ID + '.';

    // Actions
    String COPYTRACE_ACTION = PREFIX + "copy_trace_action_context"; //$NON-NLS-1$
    String COPYFAILURELIST_ACTION = PREFIX + "copy_failure_list_action_context"; //$NON-NLS-1$
    String ENABLEFILTER_ACTION = PREFIX + "enable_filter_action_context"; //$NON-NLS-1$
    String OPENEDITORATLINE_ACTION = PREFIX + "open_editor_atline_action_context"; //$NON-NLS-1$
    String OPENTEST_ACTION = PREFIX + "open_test_action_context"; //$NON-NLS-1$
    String RERUN_ACTION = PREFIX + "rerun_test_action_context"; //$NON-NLS-1$
    String GOTO_REFERENCED_TEST_ACTION_CONTEXT = PREFIX + "goto_referenced_test_action_context"; //$NON-NLS-1$
    String OUTPUT_SCROLL_LOCK_ACTION = PREFIX + "scroll_lock"; //$NON-NLS-1$

    // view parts
    String RESULTS_VIEW = PREFIX + "results_view_context"; //$NON-NLS-1$
    String RESULTS_VIEW_TOGGLE_ORIENTATION_ACTION = PREFIX + "results_view_toggle_call_mode_action_context"; //$NON-NLS-1$

    // Preference/Property pages
    String JUNIT_PREFERENCE_PAGE = PREFIX + "junit_preference_page_context"; //$NON-NLS-1$

    // Wizard pages
    String NEW_TESTCASE_WIZARD_PAGE = PREFIX + "new_testcase_wizard_page_context"; //$NON-NLS-1$
    String NEW_TESTCASE_WIZARD_PAGE2 = PREFIX + "new_testcase_wizard_page2_context"; //$NON-NLS-1$
    String NEW_TESTSUITE_WIZARD_PAGE = PREFIX + "new_testsuite_wizard_page2_context"; //$NON-NLS-1$
    String LAUNCH_CONFIGURATION_DIALOG_JUNIT_MAIN_TAB = PREFIX + "launch_configuration_dialog_junit_main_tab"; //$NON-NLS-1$

    // Dialogs
    String TEST_SELECTION_DIALOG = PREFIX + "test_selection_context"; //$NON-NLS-1$
    String RESULT_COMPARE_DIALOG = PREFIX + "result_compare_context"; //$NON-NLS-1$

}
