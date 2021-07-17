/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui;

import org.eclipse.osgi.util.NLS;

final class Messages
    extends NLS
{
    private static final String BUNDLE_NAME = "ru.capralow.dt.unit.internal.junit.ui.junit.messages"; //$NON-NLS-1$

    public static String Internal_class;

    public static String JUnitUiPlugin_Failed_to_create_injector_for_0;
    public static String JUnitUiPlugin_Internal_error;

    public static String UnitLauncherManager_Unable_to_add_doubleclick_listener;

    public static String UnitTestLaunch_Unable_to_find_junit_xml_file_0;
    public static String UnitTestLaunch_Unable_to_show_panel_0;
    public static String UnitTestLaunch_Unable_to_read_junit_xml_file;

    public static String CompareResultDialog_actualLabel;
    public static String CompareResultDialog_expectedLabel;
    public static String CompareResultDialog_labelOK;
    public static String CompareResultDialog_title;
    public static String CompareResultsAction_description;
    public static String CompareResultsAction_label;
    public static String CompareResultsAction_tooltip;

    public static String CollapseAllAction_text;
    public static String CollapseAllAction_tooltip;

    public static String CopyTrace_action_label;
    public static String CopyTraceAction_clipboard_busy;
    public static String CopyTraceAction_problem;

    public static String CopyFailureList_action_label;
    public static String CopyFailureList_clipboard_busy;
    public static String CopyFailureList_problem;

    public static String CounterPanel_label_errors;
    public static String CounterPanel_label_failures;
    public static String CounterPanel_label_runs;
    public static String CounterPanel_runcount;
    public static String CounterPanel_runcount_assumptionsFailed;
    public static String CounterPanel_runcount_ignored;
    public static String CounterPanel_runcount_skipped;
    public static String CounterPanel_runcount_ignored_assumptionsFailed;

    public static String EnableStackFilterAction_action_description;
    public static String EnableStackFilterAction_action_label;
    public static String EnableStackFilterAction_action_tooltip;

    public static String ExpandAllAction_text;
    public static String ExpandAllAction_tooltip;

    public static String OpenEditorAction_action_label;
    public static String OpenEditorAction_error_cannotopen_message;
    public static String OpenEditorAction_error_cannotopen_title;
    public static String OpenEditorAction_error_dialog_message;
    public static String OpenEditorAction_error_dialog_title;
    public static String OpenEditorAction_message_cannotopen;

    public static String RerunAction_label_run;
    public static String RerunAction_label_debug;

    public static String ScrollLockAction_action_label;
    public static String ScrollLockAction_action_tooltip;

    public static String ShowNextFailureAction_label;
    public static String ShowNextFailureAction_tooltip;
    public static String ShowPreviousFailureAction_label;
    public static String ShowPreviousFailureAction_tooltip;

    public static String TestRunnerViewPart_activate_on_failure_only;
    public static String TestRunnerViewPart_cannotrerun_title;
    public static String TestRunnerViewPart_cannotrerurn_message;
    public static String TestRunnerViewPart_configName;

    public static String TestRunnerViewPart__error_cannotrun;
    public static String TestRunnerViewPart_error_cannotrerun;

    public static String TestRunnerViewPart_error_notests_kind;

    public static String TestRunnerViewPart_ExportTestRunSessionAction_error_title;

    public static String TestRunnerViewPart_ExportTestRunSessionAction_name;

    public static String TestRunnerViewPart_ExportTestRunSessionAction_title;

    public static String TestRunnerViewPart_ImportTestRunSessionAction_error_title;

    public static String TestRunnerViewPart_ImportTestRunSessionAction_name;

    public static String TestRunnerViewPart_ImportTestRunSessionAction_title;
    public static String TestRunnerViewPart_ImportTestRunSessionFromURLAction_import_from_url;

    public static String TestRunnerViewPart_ImportTestRunSessionFromURLAction_invalid_url;

    public static String TestRunnerViewPart_ImportTestRunSessionFromURLAction_url;

    public static String TestRunnerViewPart_jobName;
    public static String TestRunnerViewPart_label_failure;
    public static String TestRunnerViewPart_Launching;
    public static String TestRunnerViewPart_message_finish;
    public static String TestRunnerViewPart_message_started;
    public static String TestRunnerViewPart_message_stopped;
    public static String TestRunnerViewPart_message_terminated;
    public static String TestRunnerViewPart_rerunaction_label;
    public static String TestRunnerViewPart_rerunaction_tooltip;
    public static String TestRunnerViewPart_rerunfailuresaction_label;
    public static String TestRunnerViewPart_rerunfailuresaction_tooltip;
    public static String TestRunnerViewPart_rerunFailedFirstLaunchConfigName;
    public static String TestRunnerViewPart_stopaction_text;
    public static String TestRunnerViewPart_stopaction_tooltip;
    public static String TestRunnerViewPart_terminate_message;
    public static String TestRunnerViewPart_terminate_title;
    public static String TestRunnerViewPart_toggle_automatic_label;
    public static String TestRunnerViewPart_toggle_horizontal_label;
    public static String TestRunnerViewPart_toggle_vertical_label;
    public static String TestRunnerViewPart_titleToolTip;
    public static String TestRunnerViewPart_wrapperJobName;
    public static String TestRunnerViewPart_history;
    public static String TestRunnerViewPart_test_run_history;
    public static String TestRunnerViewPart_test_runs;
    public static String TestRunnerViewPart_select_test_run;
    public static String TestRunnerViewPart_testName_startTime;
    public static String TestRunnerViewPart_max_remembered;
    public static String TestRunnerViewPart_show_execution_time;

    public static String TestRunnerViewPart_show_failures_only;
    public static String TestRunnerViewPart_show_ignored_only;
    public static String TestRunnerViewPart_hierarchical_layout;

    public static String TestRunnerViewPart_sort_by_menu;
    public static String TestRunnerViewPart_toggle_name_label;
    public static String TestRunnerViewPart_toggle_execution_order_label;
    public static String TestRunnerViewPart_toggle_execution_time_label;

    public static String TestRunnerViewPart_JUnitPasteAction_cannotpaste_title;
    public static String TestRunnerViewPart_JUnitPasteAction_cannotpaste_message;
    public static String TestRunnerViewPart_JUnitPasteAction_label;

    public static String TestRunnerViewPart_clear_history_label;
    public static String TestRunnerViewPart_layout_menu;
    public static String TestRunnerViewPart_message_stopping;

    public static String TestSessionLabelProvider_testName_elapsedTimeInSeconds;
    public static String TestSessionLabelProvider_testMethodName_className;

    static
    {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
