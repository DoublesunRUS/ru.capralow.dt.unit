/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui.addtest;

import org.eclipse.osgi.util.NLS;

public final class Messages
    extends NLS
{

    private static final String BUNDLE_NAME = "ru.capralow.dt.unit.internal.junit.ui.addtest.messages"; //$NON-NLS-1$

    public static String AddUnitTest_Unable_to_get_configuration_from_base_project_0;
    public static String AddUnitTest_Error_caption;
    public static String AddUnitTest_Error_message;

    static
    {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
    }

}
