/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Open a test in the EDT editor and reveal a given line
 */
public class OpenEditorAtLineAction
    extends OpenEditorAction
{

    private int fLineNumber;

    public OpenEditorAtLineAction(TestRunnerViewPart testRunner, String className, int line)
    {
        super(testRunner, className);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJunitHelpContextIds.OPENEDITORATLINE_ACTION);
        fLineNumber = line;
    }

    @Override
    protected void reveal(ITextEditor textEditor)
    {
        if (fLineNumber >= 0)
        {
            try
            {
                IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
                textEditor.selectAndReveal(document.getLineOffset(fLineNumber - 1),
                    document.getLineLength(fLineNumber - 1));
            }
            catch (BadLocationException x)
            {
                // marker refers to invalid text position -> do nothing
            }
        }
    }

}
