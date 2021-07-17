/**
 * Copyright (c) 2020, Aleksandr Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.handlers;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import ru.capralow.dt.coverage.CoverageTools;
import ru.capralow.dt.coverage.ICoverageSession;
import ru.capralow.dt.coverage.ISessionManager;
import ru.capralow.dt.coverage.internal.ui.CoverageUiPlugin;
import ru.capralow.dt.coverage.internal.ui.UiMessages;

/**
 * Dynamically created menu items for selecting the current coverage session.
 */
public class SelectActiveSessionsItems
    extends ContributionItem
{

    private static void createItem(final Menu parent, final int index, final ICoverageSession session,
        final boolean selected, final int position, final ISessionManager sm)
    {
        final MenuItem item = new MenuItem(parent, SWT.RADIO, index);
        item.setImage(CoverageUiPlugin.getImage(CoverageUiPlugin.ELCL_SESSION));
        item.setText(getLabel(session, position));
        item.setSelection(selected);
        item.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                sm.activateSession(session);
            }
        });
    }

    private static String getLabel(ICoverageSession session, int idx)
    {
        return NLS.bind(UiMessages.CoverageViewSelectSessionMenu_label, Integer.valueOf(idx), session.getDescription());
    }

    @Override
    public void fill(final Menu menu, int index)
    {
        final ISessionManager sm = CoverageTools.getSessionManager();
        final ICoverageSession activeSession = sm.getActiveSession();
        int position = 1;
        for (ICoverageSession session : sm.getSessions())
        {
            createItem(menu, index + 1, session, session == activeSession, position++, sm);
        }
    }

    @Override
    public boolean isDynamic()
    {
        return true;
    }

}
