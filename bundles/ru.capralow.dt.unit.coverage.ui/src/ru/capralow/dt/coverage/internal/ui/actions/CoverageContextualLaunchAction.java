/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.actions;

import org.eclipse.debug.ui.actions.ContextualLaunchAction;

import ru.capralow.dt.coverage.CoverageTools;

/**
 * An action delegate for the "Coverage As" context menu entry.
 */
public class CoverageContextualLaunchAction
    extends ContextualLaunchAction
{

    public CoverageContextualLaunchAction()
    {
        super(CoverageTools.LAUNCH_MODE);
    }

}
