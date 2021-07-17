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
import org.jacoco.core.analysis.ICoverageNode.CounterEntity;

/**
 * Handler to selects the counter entities shown in the coverage tree.
 */
class SelectCountersHandler
    extends AbstractHandler
    implements IElementUpdater
{

    public static final String ID = "ru.capralow.dt.coverage.ui.selectCounters"; //$NON-NLS-1$

    private static final String TYPE_PARAMETER = "type"; //$NON-NLS-1$

    private static CounterEntity getType(Map<?, ?> parameters)
    {
        return CounterEntity.valueOf((String)parameters.get(TYPE_PARAMETER));
    }

    private final ViewSettings settings;

    private final CoverageView view;

    SelectCountersHandler(ViewSettings settings, CoverageView view)
    {
        this.settings = settings;
        this.view = view;
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        final CounterEntity type = getType(event.getParameters());
        settings.setCounters(type);
        view.refreshViewer();
        return null;
    }

    @Override
    public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters)
    {
        final CounterEntity type = getType(parameters);
        element.setChecked(settings.getCounters().equals(type));
    }

}
