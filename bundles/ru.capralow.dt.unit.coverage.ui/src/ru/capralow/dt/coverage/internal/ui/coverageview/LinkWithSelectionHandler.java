/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.coverageview;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;

/**
 * Handle to toggle linking of the coverage view's selection with the current
 * selection in the workbench.
 */
class LinkWithSelectionHandler
    extends AbstractHandler
    implements IElementUpdater
{

    public static final String ID = "ru.capralow.dt.coverage.ui.linkWithSelection"; //$NON-NLS-1$

    private final ViewSettings settings;
    private final SelectionTracker tracker;

    LinkWithSelectionHandler(ViewSettings settings, SelectionTracker tracker)
    {
        this.settings = settings;
        this.tracker = tracker;
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        final boolean flag = !settings.isLinked();
        settings.setLinked(flag);
        tracker.setEnabled(flag);
        return null;
    }

    @Override
    public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters)
    {
        element.setChecked(settings.isLinked());
    }

}
