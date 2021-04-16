/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.internal.launching.core;

import org.eclipse.osgi.util.NLS;

final class Messages
    extends NLS
{
    private static final String BUNDLE_NAME = "ru.capralow.dt.internal.launching.core.messages"; //$NON-NLS-1$

    public static String RuntimeUnitLauncherLaunchDelegate_Failed_to_get_framework_params_from_bundle_0_1;
    public static String RuntimeUnitLauncherLaunchDelegate_Failed_to_read_framework_params_0;
    public static String RuntimeUnitLauncherLaunchDelegate_Failed_to_save_framework_params_0;

    public static String RuntimeUnitLauncherLaunchDelegate_Failed_to_get_framework_from_bundle_0_1;
    public static String RuntimeUnitLauncherLaunchDelegate_Failed_to_read_framework_0;
    public static String RuntimeUnitLauncherLaunchDelegate_Failed_to_save_framework_0;

    public static String RuntimeUnitLauncherLaunchDelegate_Incorrect_launch_configuration;

    public static String RuntimeClientLaunchDelegate_1C_Enterprise__0__has_no__1__installed;
    public static String RuntimeClientLaunchDelegate_Debug_session_already_started;
    public static String RuntimeClientLaunchDelegate_Error_while_launching;
    public static String RuntimeClientLaunchDelegate_External_dump_generation_is_disabled_for_project__0;
    public static String RuntimeClientLaunchDelegate_External_object_with_name__0__not_found_in_project__1;
    public static String RuntimeClientLaunchDelegate_Incorrect_external_object;
    public static String RuntimeClientLaunchDelegate_Incorrect_infobase;
    public static String RuntimeClientLaunchDelegate_Incorrect_project;
    public static String RuntimeClientLaunchDelegate_Incorrect_runtime;
    public static String RuntimeClientLaunchDelegate_Platform__0__has_no__1__component_installed;

    static
    {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
