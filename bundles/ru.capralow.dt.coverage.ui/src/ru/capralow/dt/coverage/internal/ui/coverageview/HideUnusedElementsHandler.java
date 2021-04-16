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
 * Handler to toggle hide unused types in the coverage tree.
 */
class HideUnusedElementsHandler
    extends AbstractHandler
    implements IElementUpdater
{

    public static final String ID = "ru.capralow.dt.coverage.ui.hideUnusedElements"; //$NON-NLS-1$

    private final ViewSettings settings;
    private final CoverageView view;

    HideUnusedElementsHandler(ViewSettings settings, CoverageView view)
    {
        this.settings = settings;
        this.view = view;
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        settings.setHideUnusedElements(!settings.getHideUnusedElements());
        view.refreshViewer();
        return null;
    }

    @Override
    public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters)
    {
        element.setChecked(settings.getHideUnusedElements());
    }

}
