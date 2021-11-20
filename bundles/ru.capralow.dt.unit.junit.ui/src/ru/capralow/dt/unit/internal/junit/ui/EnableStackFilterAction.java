/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;

import ru.capralow.dt.unit.internal.junit.JUnitPreferencesConstants;

/**
 * Action to enable/disable stack trace filtering.
 */
public class EnableStackFilterAction
    extends Action
{

    private FailureTrace fView;

    /**
     * @param view
     */
    public EnableStackFilterAction(FailureTrace view)
    {
        super(Messages.EnableStackFilterAction_action_label);
        setDescription(Messages.EnableStackFilterAction_action_description);
        setToolTipText(Messages.EnableStackFilterAction_action_tooltip);

        setDisabledImageDescriptor(JUnitUiPlugin.getImageDescriptor("dlcl16/cfilter.png")); //$NON-NLS-1$
        setHoverImageDescriptor(JUnitUiPlugin.getImageDescriptor("elcl16/cfilter.png")); //$NON-NLS-1$
        setImageDescriptor(JUnitUiPlugin.getImageDescriptor("elcl16/cfilter.png")); //$NON-NLS-1$
        PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJunitHelpContextIds.ENABLEFILTER_ACTION);

        fView = view;
        setChecked(JUnitPreferencesConstants.getFilterStack());
    }

    /*
     * @see Action#actionPerformed
     */
    @Override
    public void run()
    {
        JUnitPreferencesConstants.setFilterStack(isChecked());
        fView.refresh();
    }
}
