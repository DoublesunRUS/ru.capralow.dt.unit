/**
 * Copyright (c) 2020, Aleksandr Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.actions;

import org.eclipse.debug.ui.actions.AbstractLaunchHistoryAction;

import ru.capralow.dt.coverage.internal.ui.CoverageUiPlugin;

/**
 * History pull-down menu for recent coverage launches.
 */
public class CoverageHistoryAction
    extends AbstractLaunchHistoryAction
{

    public CoverageHistoryAction()
    {
        super(CoverageUiPlugin.ID_COVERAGE_LAUNCH_GROUP);
    }

}
