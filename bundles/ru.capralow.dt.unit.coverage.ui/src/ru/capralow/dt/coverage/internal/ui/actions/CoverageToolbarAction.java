/**
 * Copyright (c) 2020, Aleksandr Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.actions;

import org.eclipse.debug.ui.actions.AbstractLaunchToolbarAction;

import ru.capralow.dt.coverage.internal.ui.CoverageUiPlugin;

/**
 * Action for the coverage mode in the toolbar.
 */
public class CoverageToolbarAction
    extends AbstractLaunchToolbarAction
{

    public CoverageToolbarAction()
    {
        super(CoverageUiPlugin.ID_COVERAGE_LAUNCH_GROUP);
    }

}
