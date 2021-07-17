/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui.launcher;

import org.eclipse.osgi.util.NLS;

final class Messages
    extends NLS
{
    private static final String BUNDLE_NAME = "ru.capralow.dt.unit.internal.junit.ui.launcher.messages"; //$NON-NLS-1$

    public static String UnitTestLaunchTab_Tab_message;

    public static String UnitTestLaunchTab_ExtensionModule_to_Test;
    public static String UnitTestLaunchTab_RunExtensionTests;
    public static String UnitTestLaunchTab_RunModuleTests;
    public static String UnitTestLaunchTab_RunTagTests;
    public static String UnitTestLaunchTab_ExtensionProject_to_Test;
    public static String UnitTestLaunchTab_ExtensionTag_to_Test;

    public static String UnitTestLaunchShortcut_Title;
    public static String UnitTestLaunchShortcut_Name_suffix;

    public static String UnitTestLaunchShortcut_Wrong_project_exception;

    public static String AbstractRuntimeClientLaunchShortcut_External_Object_Selection;
    public static String AbstractRuntimeClientLaunchShortcut_Select_an_external_object_to_launch;

    static
    {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
