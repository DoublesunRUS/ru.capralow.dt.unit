package ru.capralow.dt.unit.internal.junit.ui.wizards.dialogfields;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog field containing a label and a text control.
 */
public class StringDialogField
    extends DialogField
{

    protected static GridData gridDataForText(int span)
    {
        GridData gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = false;
        gd.horizontalSpan = span;
        return gd;
    }

    private String fText;
    private Text fTextControl;
    private ModifyListener fModifyListener;

    public StringDialogField()
    {
        super();
        fText = ""; //$NON-NLS-1$
    }

    /*
     * @see DialogField#doFillIntoGrid
     */
    @Override
    public Control[] doFillIntoGrid(Composite parent, int nColumns)
    {
        assertEnoughColumns(nColumns);

        Label label = getLabelControl(parent);
        label.setLayoutData(gridDataForLabel(1));
        Text text = getTextControl(parent);
        text.setLayoutData(gridDataForText(nColumns - 1));

        return new Control[] { label, text };
    }

    // ------- layout helpers

    /*
     * @see DialogField#getNumberOfControls
     */
    @Override
    public int getNumberOfControls()
    {
        return 2;
    }

    /**
     * @return the text, can not be <code>null</code>
     */
    public String getText()
    {
        return fText;
    }

    // ------- focus methods

    /**
     * Creates or returns the created text control.
     * @param parent The parent composite or <code>null</code> when the widget has
     * already been created.
     * @return the text control
     */
    public Text getTextControl(Composite parent)
    {
        if (fTextControl == null)
        {
            assertCompositeNotNull(parent);
            fModifyListener = e -> doModifyText();

            fTextControl = createTextControl(parent);
            // moved up due to 1GEUNW2
            fTextControl.setText(fText);
            fTextControl.setFont(parent.getFont());
            fTextControl.addModifyListener(fModifyListener);

            fTextControl.setEnabled(isEnabled());
        }
        return fTextControl;
    }

    @Override
    public void refresh()
    {
        super.refresh();
        if (isOkToUse(fTextControl))
        {
            setTextWithoutUpdate(fText);
        }
    }

    // ------- ui creation

    /**
     * @see StringDialogField#setFocus(boolean)
     */
    @Override
    public boolean setFocus()
    {
        return setFocus(true);
    }

    /**
     * Tries to set the focus to the string dialog field.
     *
     * @param selectText <code>true</code> if the text should be selected in the string dialog
     *            field. Otherwise, the text is left unselected and the caret is placed at the end
     *            of the text
     * @return <code>true</code> if the dialog field can take focus
     * @see StringDialogField#setFocus()
     */
    public boolean setFocus(boolean selectText)
    {
        if (isOkToUse(fTextControl))
        {
            fTextControl.setFocus();
            if (selectText)
            {
                fTextControl.setSelection(0, fTextControl.getText().length());
            }
            else
            {
                fTextControl.setSelection(fTextControl.getText().length());
            }
        }
        return true;
    }

    // ------ enable / disable management

    /**
     * Sets the text. Triggers a dialog-changed event.
     * @param text the new text
     */
    public void setText(String text)
    {
        fText = text;
        if (isOkToUse(fTextControl))
        {
            fTextControl.setText(text);
        }
        else
        {
            dialogFieldChanged();
        }
    }

    // ------ text access

    /**
     * Sets the text without triggering a dialog-changed event.
     * @param text the new text
     */
    public void setTextWithoutUpdate(String text)
    {
        fText = text;
        if (isOkToUse(fTextControl))
        {
            fTextControl.removeModifyListener(fModifyListener);
            fTextControl.setText(text);
            fTextControl.addModifyListener(fModifyListener);
        }
    }

    private void doModifyText()
    {
        if (isOkToUse(fTextControl))
        {
            fText = fTextControl.getText();
        }
        dialogFieldChanged();
    }

    /**
     * Creates and returns a new text control.
     *
     * @param parent the parent
     * @return the text control
     * @since 3.6
     */
    protected Text createTextControl(Composite parent)
    {
        return new Text(parent, SWT.SINGLE | SWT.BORDER);
    }

    /*
     * @see DialogField#updateEnableState
     */
    @Override
    protected void updateEnableState()
    {
        super.updateEnableState();
        if (isOkToUse(fTextControl))
        {
            fTextControl.setEnabled(isEnabled());
        }
    }

}
