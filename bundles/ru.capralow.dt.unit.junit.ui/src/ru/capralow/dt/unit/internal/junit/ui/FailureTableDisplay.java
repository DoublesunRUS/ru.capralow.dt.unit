/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class FailureTableDisplay
    implements ITraceDisplay
{
    private final Table fTable;

    private final Image fExceptionIcon = JUnitUiPlugin.createImage("obj16/exc_catch.png"); //$NON-NLS-1$

    private final Image fStackIcon = JUnitUiPlugin.createImage("obj16/stkfrm_obj.png"); //$NON-NLS-1$

    public FailureTableDisplay(Table table)
    {
        fTable = table;
        fTable.getParent().addDisposeListener(e -> disposeIcons());
    }

    @Override
    public void addTraceLine(int lineType, String label)
    {
        TableItem tableItem = newTableItem();
        switch (lineType)
        {
        case TextualTrace.LINE_TYPE_EXCEPTION:
            tableItem.setImage(fExceptionIcon);
            break;
        case TextualTrace.LINE_TYPE_STACKFRAME:
            tableItem.setImage(fStackIcon);
            break;
        case TextualTrace.LINE_TYPE_NORMAL:
        default:
            break;
        }
        tableItem.setText(label);
    }

    public Image getExceptionIcon()
    {
        return fExceptionIcon;
    }

    public Image getStackIcon()
    {
        return fStackIcon;
    }

    public Table getTable()
    {
        return fTable;
    }

    private void disposeIcons()
    {
        if (fExceptionIcon != null && !fExceptionIcon.isDisposed())
            fExceptionIcon.dispose();
        if (fStackIcon != null && !fStackIcon.isDisposed())
            fStackIcon.dispose();
    }

    TableItem newTableItem()
    {
        return new TableItem(fTable, SWT.NONE);
    }
}
