/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.junit.frameworks;

import org.eclipse.osgi.util.NLS;

final class Messages
    extends NLS
{
    private static final String BUNDLE_NAME = "ru.capralow.dt.unit.junit.frameworks.messages"; //$NON-NLS-1$

    public static String FrameworkUtils_Unable_to_delete_framework_file_0;

    public static String FrameworkUtils_Wrong_project_class_0;

    public static String FrameworkUtils_Internal_class;

    public static String FrameworkUtils_Extension_project_not_found_0;

    static
    {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
