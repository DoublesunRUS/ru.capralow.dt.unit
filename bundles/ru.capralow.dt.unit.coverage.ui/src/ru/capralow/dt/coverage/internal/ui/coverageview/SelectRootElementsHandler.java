/**
 * Copyright (c) 2020, Aleksandr Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.coverageview;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;
import org.jacoco.core.analysis.ICoverageNode.ElementType;

/**
 * Handler to selects the root elements shown in the coverage tree.
 */
class SelectRootElementsHandler
    extends AbstractHandler
    implements IElementUpdater
{

    public static final String ID = "ru.capralow.dt.coverage.ui.selectRootElements"; //$NON-NLS-1$

    private static final String TYPE_PARAMETER = "type"; //$NON-NLS-1$

    private static ElementType getType(Map<?, ?> parameters)
    {
        return ElementType.valueOf((String)parameters.get(TYPE_PARAMETER));
    }

    private final ViewSettings settings;

    private final CoverageView view;

    SelectRootElementsHandler(ViewSettings settings, CoverageView view)
    {
        this.settings = settings;
        this.view = view;
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        final ElementType type = getType(event.getParameters());
        settings.setRootType(type);
        view.refreshViewer();
        return null;
    }

    @Override
    public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters)
    {
        final ElementType type = getType(parameters);
        element.setChecked(settings.getRootType().equals(type));
    }

}
