/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.actions;

import org.eclipse.debug.ui.actions.RelaunchLastAction;

import ru.capralow.dt.coverage.CoverageTools;
import ru.capralow.dt.coverage.internal.ui.CoverageUiPlugin;
import ru.capralow.dt.coverage.internal.ui.UiMessages;

/**
 * Action to re-launch the last launch in coverage mode.
 */
public class CoverageLastAction
    extends RelaunchLastAction
{

    @Override
    public String getLaunchGroupId()
    {
        return CoverageUiPlugin.ID_COVERAGE_LAUNCH_GROUP;
    }

    @Override
    public String getMode()
    {
        return CoverageTools.LAUNCH_MODE;
    }

    @Override
    protected String getCommandId()
    {
        return "ru.capralow.dt.coverage.ui.commands.CoverageLast"; //$NON-NLS-1$
    }

    @Override
    protected String getDescription()
    {
        return UiMessages.CoverageLastAction_label;
    }

    @Override
    protected String getText()
    {
        return UiMessages.CoverageLastAction_label;
    }

    @Override
    protected String getTooltipText()
    {
        return UiMessages.CoverageLastAction_label;
    }

}
