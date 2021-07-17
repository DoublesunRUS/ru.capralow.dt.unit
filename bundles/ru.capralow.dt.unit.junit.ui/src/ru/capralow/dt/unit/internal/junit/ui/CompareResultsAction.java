/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui;

import org.eclipse.jface.action.Action;

import ru.capralow.dt.unit.internal.junit.model.TestElement;

/**
 * Action to enable/disable stack trace filtering.
 */
public class CompareResultsAction
    extends Action
{

    private FailureTrace fView;
    private CompareResultDialog fOpenDialog;

    public CompareResultsAction(FailureTrace view)
    {
        super(Messages.CompareResultsAction_label);
        setDescription(Messages.CompareResultsAction_description);
        setToolTipText(Messages.CompareResultsAction_tooltip);

        setDisabledImageDescriptor(JUnitUiPlugin.getImageDescriptor("dlcl16/compare.png")); //$NON-NLS-1$
        setHoverImageDescriptor(JUnitUiPlugin.getImageDescriptor("elcl16/compare.png")); //$NON-NLS-1$
        setImageDescriptor(JUnitUiPlugin.getImageDescriptor("elcl16/compare.png")); //$NON-NLS-1$
        fView = view;
    }

    /*
     * @see Action#actionPerformed
     */
    @Override
    public void run()
    {
        TestElement failedTest = fView.getFailedTest();
        if (fOpenDialog != null)
        {
            fOpenDialog.setInput(failedTest);
            fOpenDialog.getShell().setActive();

        }
        else
        {
            fOpenDialog = new CompareResultDialog(fView.getShell(), failedTest);
            fOpenDialog.create();
            fOpenDialog.getShell().addDisposeListener(e -> fOpenDialog = null);
            fOpenDialog.setBlockOnOpen(false);
            fOpenDialog.open();
        }
    }

    public void updateOpenDialog(TestElement failedTest)
    {
        if (fOpenDialog != null)
        {
            fOpenDialog.setInput(failedTest);
        }
    }
}
