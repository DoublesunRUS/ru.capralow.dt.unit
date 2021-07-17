/**
 * Copyright (c) 2020, Aleksandr Kapralov
 */
package ru.capralow.dt.coverage.internal.ui;

import org.eclipse.osgi.util.NLS;

/**
 * Text messages for the UI plug-in.
 */
public class UiMessages
    extends NLS
{

    private static final String BUNDLE_NAME = "ru.capralow.dt.coverage.internal.ui.uimessages"; //$NON-NLS-1$

    public static String BrowseAction_label;
    public static String SelectAllAction_label;
    public static String DeselectAllAction_label;
    public static String CoverageLastAction_label;

    public static String CoverageViewSelectSessionMenu_label;
    public static String CoverageViewColumnElement_label;
    public static String CoverageViewColumnCoverage_label;
    public static String CoverageViewColumnCoveredInstructions_label;
    public static String CoverageViewColumnCoveredBranches_label;
    public static String CoverageViewColumnCoveredLines_label;
    public static String CoverageViewColumnCoveredMethods_label;
    public static String CoverageViewColumnCoveredTypes_label;
    public static String CoverageViewColumnCoveredComplexity_label;
    public static String CoverageViewColumnMissedInstructions_label;
    public static String CoverageViewColumnMissedBranches_label;
    public static String CoverageViewColumnMissedLines_label;
    public static String CoverageViewColumnMissedMethods_label;
    public static String CoverageViewColumnMissedTypes_label;
    public static String CoverageViewColumnMissedComplexity_label;
    public static String CoverageViewColumnTotalInstructions_label;
    public static String CoverageViewColumnTotalBranches_label;
    public static String CoverageViewColumnTotalLines_label;
    public static String CoverageViewColumnTotalMethods_label;
    public static String CoverageViewColumnTotalTypes_label;
    public static String CoverageViewColumnTotalComplexity_label;
    public static String CoverageView_columnCoverageValue;
    public static String CoverageView_loadingMessage;

    public static String AnnotationTextAllBranchesMissed_message;
    public static String AnnotationTextAllBranchesCovered_message;
    public static String AnnotationTextSomeBranchesMissed_message;

    public static String SelectActiveSessionDialog_title;
    public static String SelectActiveSessionDialog_message;

    public static String DumpExecutionDataUnknownLaunch_value;
    public static String DumpExecutionData_task;
    public static String DumpExecutionDataDialog_title;
    public static String DumpExecutionDataDialog_message;

    public static String MergeSessionsDialog_title;
    public static String MergeSessionsDialogDescription_label;
    public static String MergeSessionsDialogDescriptionDefault_value;
    public static String MergeSessionsDialogSelection_label;
    public static String MergingSessions_task;

    public static String CoverageTab_title;
    public static String CoverageTabAnalysisScopeGroup_label;
    public static String CoverageTabEmptyAnalysisScope_message;

    public static String NoCoverageDataError_title;
    public static String NoCoverageDataError_message;

    public static String ExportSession_title;
    public static String ExportSessionErrorDialog_title;
    public static String ExportSessionErrorDialog_message;
    public static String ExportSessionPage1_title;
    public static String ExportSessionPage1_description;
    public static String ExportSessionPage1NoSession_message;
    public static String ExportSessionPage1MissingDestination_message;
    public static String ExportSessionPage1WrongExtension_message;
    public static String ExportSessionPage1Sessions_label;
    public static String ExportSessionPage1DestinationGroup_label;
    public static String ExportSessionPage1Format_label;
    public static String ExportSessionPage1Destination_label;
    public static String ExportSessionPage1BrowseDialog_title;

    public static String ImportSession_title;
    public static String ImportSessionPage1_title;
    public static String ImportSessionErrorDialog_title;
    public static String ImportSessionErrorDialog_message;
    public static String ImportSessionPage1_description;
    public static String ImportSessionPage1NoDescription_message;
    public static String ImportSessionPage1NoExecutionDataFile_message;
    public static String ImportSessionPage1NoExecutionDataUrl_message;
    public static String ImportSessionPage1NoExecutionDataAddress_message;
    public static String ImportSessionPage1NoExecutionDataPort_message;
    public static String ImportSessionPage1NoClassFiles_message;
    public static String ImportSessionPage1Description_label;
    public static String ImportSessionPage1Description_value;
    public static String ImportSessionPage1ExecutionDataFile_label;
    public static String ImportSessionPage1ExecutionDataUrl_label;
    public static String ImportSessionPage1ExecutionDataAddress_label;
    public static String ImportSessionPage1ExecutionDataPort_label;
    public static String ImportSessionPage1ExecutionDataReset_label;
    public static String ImportSessionPage1BrowseDialog_title;
    public static String ImportSessionPage1Binaries_label;
    public static String ImportSessionPage1ModeGroup_label;
    public static String ImportSessionPage1Reference_label;
    public static String ImportSessionPage1Copy_label;

    public static String CoveragePropertyPageSession_label;
    public static String CoveragePropertyPageNoSession_value;
    public static String CoveragePropertyPageColumnCounter_label;
    public static String CoveragePropertyPageColumnCoverage_label;
    public static String CoveragePropertyPageColumnCoverage_value;
    public static String CoveragePropertyPageColumnCovered_label;
    public static String CoveragePropertyPageColumnMissed_label;
    public static String CoveragePropertyPageColumnTotal_label;
    public static String CoveragePropertyPageInstructions_label;
    public static String CoveragePropertyPageBranches_label;
    public static String CoveragePropertyPageLines_label;
    public static String CoveragePropertyPageMethods_label;
    public static String CoveragePropertyPageTypes_label;
    public static String CoveragePropertyPageComplexity_label;

    public static String CoverageDecoratorSuffix_label;

    public static String CoverageSessionManagement_titel;
    public static String CoveragePreferencesShowCoverageView_label;
    public static String CoveragePreferencesActivateNewSessions_label;
    public static String CoveragePreferencesAutoRemoveSessions_label;
    public static String CoveragePreferencesResetOnDump_label;
    public static String CoveragePreferencesDefaultScope_title;
    public static String CoveragePreferencesSourceFoldersOnly_label;
    public static String CoveragePreferencesSameProjectOnly_label;
    public static String CoveragePreferencesClasspathFilter_label;
    public static String CoveragePreferencesCoverageRuntime_title;
    public static String CoveragePreferencesIncludes_label;
    public static String CoveragePreferencesExcludes_label;
    public static String CoveragePreferencesExcludeClassloaders_label;
    public static String CoveragePreferencesCoverageRuntime_message;
    public static String CoveragePreferencesDecoratorsLink_label;
    public static String CoveragePreferencesAnnotationsLink_label;

    public static String ClassesViewerEntry_label;

    public static String ExecutionDataEditorSessionsPage_title;
    public static String ExecutionDataEditorSessionsPageColumnSessionId_label;
    public static String ExecutionDataEditorSessionsPageColumnStartTime_label;
    public static String ExecutionDataEditorSessionsPageColumnDumpTime_label;
    public static String ExecutionDataEditorExecutedClassesPage_title;
    public static String ExecutionDataEditorExecutedClassesPageColumnId_label;
    public static String ExecutionDataEditorExecutedClassesPageColumnName_label;
    public static String ExecutionDataEditorExecutedClassesPageColumnTotalProbes_label;
    public static String ExecutionDataEditorExecutedClassesPageColumnExecutedProbes_label;
    public static String ExecutionDataEditorExecutedClassesPageFilter_message;
    public static String ExecutionDataEditorExecutedClassesPageRefreshing_task;
    public static String ExecutionDataEditorOpeningError_message;

    public static String Failed_to_create_injector_for_0;

    static
    {
        NLS.initializeMessages(BUNDLE_NAME, UiMessages.class);
    }

}
