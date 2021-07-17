/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage;

import org.eclipse.osgi.util.NLS;

/**
 * Text messages for the core plug-in.
 */
public class CoreMessages
    extends NLS
{

    private static final String BUNDLE_NAME = "ru.capralow.dt.coverage.coremessages"; //$NON-NLS-1$

    public static String Internal_class;

    static
    {
        NLS.initializeMessages(BUNDLE_NAME, CoreMessages.class);
    }

}
