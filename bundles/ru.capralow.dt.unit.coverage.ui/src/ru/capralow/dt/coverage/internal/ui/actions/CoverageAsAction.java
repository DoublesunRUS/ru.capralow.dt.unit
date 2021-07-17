/**
 * Copyright (c) 2020, Aleksandr Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.actions;

import org.eclipse.debug.ui.actions.LaunchShortcutsAction;

import ru.capralow.dt.coverage.internal.ui.CoverageUiPlugin;

/**
 * Action implementation for "Coverage as" menu.
 */
public class CoverageAsAction
    extends LaunchShortcutsAction
{

    public CoverageAsAction()
    {
        super(CoverageUiPlugin.ID_COVERAGE_LAUNCH_GROUP);
    }

}
