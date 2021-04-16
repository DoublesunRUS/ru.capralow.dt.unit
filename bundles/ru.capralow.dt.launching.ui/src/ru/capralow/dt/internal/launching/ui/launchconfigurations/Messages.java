/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.internal.launching.ui.launchconfigurations;

import org.eclipse.osgi.util.NLS;

final class Messages
    extends NLS
{
    private static final String BUNDLE_NAME = "ru.capralow.dt.internal.launching.ui.launchconfigurations.messages"; //$NON-NLS-1$

    public static String UnitTestLaunchTab_Tab_message;

    public static String UnitTestLaunchTab_ExtensionModule_to_Test;
    public static String UnitTestLaunchTab_RunExtensionTests;
    public static String UnitTestLaunchTab_RunModuleTests;
    public static String UnitTestLaunchTab_RunTagTests;
    public static String UnitTestLaunchTab_ExtensionProject_to_Test;
    public static String UnitTestLaunchTab_ExtensionTag_to_Test;

    static
    {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
