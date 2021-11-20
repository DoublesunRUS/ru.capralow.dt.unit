/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.PlatformUI;

import ru.capralow.dt.unit.internal.junit.model.TestElement;

/**
 * Copies the names of the methods that failed and their traces to the clipboard.
 */
public class CopyFailureListAction
    extends Action
{

    private final Clipboard fClipboard;
    private final TestRunnerViewPart fRunner;

    public CopyFailureListAction(TestRunnerViewPart runner, Clipboard clipboard)
    {
        super(Messages.CopyFailureList_action_label);
        fRunner = runner;
        fClipboard = clipboard;
        PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJunitHelpContextIds.COPYFAILURELIST_ACTION);
    }

    public String getAllFailureTraces()
    {
        StringBuilder buf = new StringBuilder();
        TestElement[] failures = fRunner.getAllFailures();

        String lineDelim = System.getProperty("line.separator", "\n"); //$NON-NLS-1$//$NON-NLS-2$
        for (TestElement failure : failures)
        {
            buf.append(failure.getTestName()).append(lineDelim);
            String failureTrace = failure.getTrace();
            if (failureTrace != null)
            {
                int start = 0;
                while (start < failureTrace.length())
                {
                    int idx = failureTrace.indexOf('\n', start);
                    if (idx != -1)
                    {
                        String line = failureTrace.substring(start, idx);
                        buf.append(line).append(lineDelim);
                        start = idx + 1;
                    }
                    else
                    {
                        start = Integer.MAX_VALUE;
                    }
                }
            }
        }
        return buf.toString();
    }

    /*
     * @see IAction#run()
     */
    @Override
    public void run()
    {
        TextTransfer plainTextTransfer = TextTransfer.getInstance();

        try
        {
            fClipboard.setContents(new String[] { getAllFailureTraces() }, new Transfer[] { plainTextTransfer });
        }
        catch (SWTError e)
        {
            if (e.code != DND.ERROR_CANNOT_SET_CLIPBOARD)
            {
                throw e;
            }
            if (MessageDialog.openQuestion(JUnitUiPlugin.getActiveWorkbenchShell(), Messages.CopyFailureList_problem,
                Messages.CopyFailureList_clipboard_busy))
            {
                run();
            }
        }
    }

}
