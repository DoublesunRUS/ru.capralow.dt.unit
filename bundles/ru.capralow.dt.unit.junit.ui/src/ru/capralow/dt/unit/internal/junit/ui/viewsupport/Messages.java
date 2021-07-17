/**
 * Copyright (c) 2021, Alexander Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui.viewsupport;

import org.eclipse.osgi.util.NLS;

final class Messages
    extends NLS
{
    private static final String BUNDLE_NAME = "ru.capralow.dt.unit.internal.junit.ui.viewsupport.messages"; //$NON-NLS-1$

    public static String HistoryListAction_max_entries_constraint;
    public static String HistoryListAction_remove;
    public static String HistoryListAction_remove_all;

    static
    {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
