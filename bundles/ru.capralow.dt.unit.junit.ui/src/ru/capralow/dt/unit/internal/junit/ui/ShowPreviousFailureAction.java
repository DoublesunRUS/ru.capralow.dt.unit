package ru.capralow.dt.unit.internal.junit.ui;

import org.eclipse.jface.action.Action;

class ShowPreviousFailureAction
    extends Action
{

    private TestRunnerViewPart fPart;

    public ShowPreviousFailureAction(TestRunnerViewPart part)
    {
        super(Messages.ShowPreviousFailureAction_label);
        setDisabledImageDescriptor(JUnitUiPlugin.getImageDescriptor("dlcl16/select_prev.png")); //$NON-NLS-1$
        setHoverImageDescriptor(JUnitUiPlugin.getImageDescriptor("elcl16/select_prev.png")); //$NON-NLS-1$
        setImageDescriptor(JUnitUiPlugin.getImageDescriptor("elcl16/select_prev.png")); //$NON-NLS-1$
        setToolTipText(Messages.ShowPreviousFailureAction_tooltip);
        fPart = part;
    }

    @Override
    public void run()
    {
        fPart.selectPreviousFailure();
    }
}
