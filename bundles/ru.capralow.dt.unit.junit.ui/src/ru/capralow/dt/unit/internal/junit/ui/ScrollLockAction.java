/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;

/**
 * Toggles console auto-scroll
 */
public class ScrollLockAction
    extends Action
{

    private TestRunnerViewPart fRunnerViewPart;

    /**
     * @param viewer
     */
    public ScrollLockAction(TestRunnerViewPart viewer)
    {
        super(Messages.ScrollLockAction_action_label);
        fRunnerViewPart = viewer;
        setToolTipText(Messages.ScrollLockAction_action_tooltip);
        setDisabledImageDescriptor(JUnitUiPlugin.getImageDescriptor("dlcl16/lock.png")); //$NON-NLS-1$
        setHoverImageDescriptor(JUnitUiPlugin.getImageDescriptor("elcl16/lock.png")); //$NON-NLS-1$
        setImageDescriptor(JUnitUiPlugin.getImageDescriptor("elcl16/lock.png")); //$NON-NLS-1$
        PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJunitHelpContextIds.OUTPUT_SCROLL_LOCK_ACTION);
        setChecked(false);
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    public void run()
    {
        fRunnerViewPart.setAutoScroll(!isChecked());
    }
}
