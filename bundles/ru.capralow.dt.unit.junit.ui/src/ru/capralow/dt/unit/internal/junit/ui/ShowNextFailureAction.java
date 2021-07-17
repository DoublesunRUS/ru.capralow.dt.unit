package ru.capralow.dt.unit.internal.junit.ui;

import org.eclipse.jface.action.Action;

class ShowNextFailureAction
    extends Action
{

    private TestRunnerViewPart fPart;

    public ShowNextFailureAction(TestRunnerViewPart part)
    {
        super(Messages.ShowNextFailureAction_label);
        setDisabledImageDescriptor(JUnitUiPlugin.getImageDescriptor("dlcl16/select_next.png")); //$NON-NLS-1$
        setHoverImageDescriptor(JUnitUiPlugin.getImageDescriptor("elcl16/select_next.png")); //$NON-NLS-1$
        setImageDescriptor(JUnitUiPlugin.getImageDescriptor("elcl16/select_next.png")); //$NON-NLS-1$
        setToolTipText(Messages.ShowNextFailureAction_tooltip);
        fPart = part;
    }

    @Override
    public void run()
    {
        fPart.selectNextFailure();
    }
}
