/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui.xtextbuilder;

import org.eclipse.osgi.util.NLS;

final class Messages
    extends NLS
{
    private static final String BUNDLE_NAME = "ru.capralow.dt.unit.internal.junit.ui.xtextbuilder.messages"; //$NON-NLS-1$

    public static String OpenFeaturesDirectoryHandler_Unit_tests_caption;
    public static String OpenFeaturesDirectoryHandler_Selected_project_doesnt_have_unit_tests;

    public static String UnitLauncherXtextBuilderParticipant_Error_while_saving_feature_file_0;
    public static String UnitLauncherXtextBuilderParticipant_Unable_to_find_configuration_object_0;
    public static String UnitLauncherXtextBuilderParticipant_Unable_to_get_configuration_from_base_project_0;
    public static String UnitLauncherXtextBuilderParticipant_Unable_to_delete_empty_folder_0;
    public static String UnitLauncherXtextBuilderParticipant_Unable_to_delete_empty_folders_for_project_0;
    public static String UnitLauncherXtextBuilderParticipant_Unable_to_delete_feature_file_0;
    public static String UnitLauncherXtextBuilderParticipant_Unable_to_delete_feature_files_for_module_0;

    static
    {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
