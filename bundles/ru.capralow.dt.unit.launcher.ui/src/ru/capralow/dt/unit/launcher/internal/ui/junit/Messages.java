/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.unit.launcher.internal.ui.junit;

import org.eclipse.osgi.util.NLS;

final class Messages
    extends NLS
{
    private static final String BUNDLE_NAME = "ru.capralow.dt.unit.launcher.internal.ui.junit.messages"; //$NON-NLS-1$

    public static String UnitTestLaunch_Unable_to_find_junit_xml_file_0;
    public static String UnitTestLaunch_Unable_to_show_panel_0;
    public static String UnitTestLaunch_Unable_to_read_junit_xml_file;

    public static String JUnit_Internal_class;

    static
    {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
