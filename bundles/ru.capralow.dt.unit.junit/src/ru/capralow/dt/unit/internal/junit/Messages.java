/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit;

import org.eclipse.osgi.util.NLS;

final class Messages
    extends NLS
{
    private static final String BUNDLE_NAME = "ru.capralow.dt.unit.internal.junit.messages"; //$NON-NLS-1$

    public static String Internal_class;

    public static String JUnitPlugin_Failed_to_create_injector_for_0;
    public static String JUnitPlugin_Internal_error;

    static
    {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
