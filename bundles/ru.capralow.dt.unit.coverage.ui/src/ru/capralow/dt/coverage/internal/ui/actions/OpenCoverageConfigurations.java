/**
 * Copyright (c) 2020, Aleksandr Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.actions;

import org.eclipse.debug.ui.actions.OpenLaunchDialogAction;

import ru.capralow.dt.coverage.internal.ui.CoverageUiPlugin;

/**
 * Action to open the coverage launch configuration.
 */
public class OpenCoverageConfigurations
    extends OpenLaunchDialogAction
{

    public OpenCoverageConfigurations()
    {
        super(CoverageUiPlugin.ID_COVERAGE_LAUNCH_GROUP);
    }

}
