/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.launcher;

import org.eclipse.osgi.util.NLS;

/**
 * @author Aleksandr Kapralov
 *
 */
public class Messages
    extends NLS
{
    private static final String BUNDLE_NAME = "ru.capralow.dt.unit.internal.junit.launcher.messages"; //$NON-NLS-1$

    /**
     *
     */
    public static String RuntimeUnitLauncherLaunchDelegate_Failed_to_get_framework_params_from_bundle_0_1;
    /**
     *
     */
    public static String RuntimeUnitLauncherLaunchDelegate_Failed_to_read_framework_params_0;
    /**
     *
     */
    public static String RuntimeUnitLauncherLaunchDelegate_Failed_to_save_framework_params_0;

    /**
     *
     */
    public static String RuntimeUnitLauncherLaunchDelegate_Failed_to_get_framework_from_bundle_0_1;
    /**
     *
     */
    public static String RuntimeUnitLauncherLaunchDelegate_Failed_to_read_framework_0;
    /**
     *
     */
    public static String RuntimeUnitLauncherLaunchDelegate_Failed_to_save_framework_0;

    /**
     *
     */
    public static String RuntimeUnitLauncherLaunchDelegate_Incorrect_launch_configuration;

    /**
     *
     */
    public static String AbstractUnitTestLaunchDelegate_Cannot_get_application_publication_URL;
    /**
     *
     */
    public static String AbstractUnitTestLaunchDelegate_External_object_with_name__0__not_found_in_project__1;
    /**
     *
     */
    public static String AbstractUnitTestLaunchDelegate_External_dump_generation_is_disabled_for_project__0;
    /**
     *
     */
    public static String AbstractUnitTestLaunchDelegate_Debug_session_already_started;
    /**
     *
     */
    public static String AbstractUnitTestLaunchDelegate_Incorrect_project;
    /**
     *
     */
    public static String AbstractUnitTestLaunchDelegate_Incorrect_external_object;
    /**
     *
     */
    public static String AbstractUnitTestLaunchDelegate_1C_Enterprise__0__has_no__1__installed;

    static
    {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
