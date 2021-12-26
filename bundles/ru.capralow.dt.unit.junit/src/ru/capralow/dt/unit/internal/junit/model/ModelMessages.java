/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.model;

import org.eclipse.osgi.util.NLS;

/**
 * @author Aleksandr Kapralov
 *
 */
public class ModelMessages
    extends NLS
{
    private static final String BUNDLE_NAME = "ru.capralow.dt.unit.internal.junit.model.messages"; //$NON-NLS-1$

    /**
     *
     */
    public static String JUnitModel_could_not_import;
    /**
     *
     */
    public static String JUnitModel_could_not_export;
    /**
     *
     */
    public static String JUnitModel_could_not_read;
    /**
     *
     */
    public static String JUnitModel_could_not_write;

    /**
     *
     */
    public static String JUnitModel_importing_from_url;

    /**
     *
     */
    public static String TestRunHandler_lines_read;

    /**
     *
     */
    public static String TestRunSession_unrootedTests;

    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ModelMessages.class);
    }
}