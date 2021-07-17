/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.actions;

import java.util.Iterator;

import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.texteditor.IEditorStatusLine;
import org.eclipse.xtext.ui.editor.XtextEditor;

import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.ui.util.OpenHelper;

import ru.capralow.dt.coverage.internal.ui.CoverageUiPlugin;

/**
 * This action opens a Java editor on a Java element or file.
 * <p>
 * The action is applicable to selections containing elements of type
 * <code>ICompilationUnit</code>, <code>IMember</code> or <code>IFile</code>.
 *
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 *
 * @since 2.0
 *
 * @noextend This class is not intended to be subclassed by clients.
 */
public class OpenAction
    extends SelectionDispatchAction
{

    private XtextEditor fEditor;

    private IResourceLookup resourceLookup;

    /**
     * Creates a new <code>OpenAction</code>. The action requires that the selection
     * provided by the site's selection provider is of type <code>
     * org.eclipse.jface.viewers.IStructuredSelection</code>.
     *
     * @param site the site providing context information for this action
     */
    public OpenAction(IWorkbenchSite site)
    {
        super(site);
        // setText(ActionMessages.OpenAction_label);
        // setToolTipText(ActionMessages.OpenAction_tooltip);
        // setDescription(ActionMessages.OpenAction_description);
        // PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
        // IJavaHelpContextIds.OPEN_ACTION);

        resourceLookup = CoverageUiPlugin.getInstance().getInjector().getInstance(IResourceLookup.class);
    }

    /**
     * Note: This constructor is for internal use only. Clients should not call this
     * constructor.
     *
     * @param editor the Java editor
     *
     * @noreference This constructor is not intended to be referenced by clients.
     */
    public OpenAction(XtextEditor editor)
    {
        this(editor.getEditorSite());
        fEditor = editor;
        // setText(ActionMessages.OpenAction_declaration_label);
        setEnabled(false);
    }

    @Override
    public void run(IStructuredSelection selection)
    {
        if (!checkEnabled(selection))
            return;
        run(selection.toArray());
    }

    @Override
    public void run(ITextSelection selection)
    {

    }

    /**
     * Note: this method is for internal use only. Clients should not call this
     * method.
     *
     * @param elements the elements to process
     *
     * @noreference This method is not intended to be referenced by clients.
     */
    public void run(Object[] elements)
    {
        if (elements == null)
            return;

        for (int i = 0; i < elements.length; i++)
        {
            Object element = elements[i];
            URI uri = (URI)element;

            OpenHelper openHelper = new OpenHelper();
            openHelper.openEditor(uri, null);
        }
    }

    @Override
    public void selectionChanged(IStructuredSelection selection)
    {
        setEnabled(checkEnabled(selection));
    }

    @Override
    public void selectionChanged(ITextSelection selection)
    {
        // Нечего делать
    }

    private boolean checkEnabled(IStructuredSelection selection)
    {
        if (selection.isEmpty())
            return false;
        for (Iterator<?> iter = selection.iterator(); iter.hasNext();)
        {
            Object element = iter.next();
            if (element instanceof URI)
                continue;
            return false;
        }
        return true;
    }

    private String getDialogTitle()
    {
        return "";
        // return ActionMessages.OpenAction_error_title;
    }

    /**
     * Sets the error message in the status line.
     *
     * @since 3.7
     */
    private void setStatusLineMessage()
    {
        IEditorStatusLine statusLine = fEditor.getAdapter(IEditorStatusLine.class);
        // if (statusLine != null)
        // statusLine.setMessage(true,
        // ActionMessages.OpenAction_error_messageBadSelection, null);
        getShell().getDisplay().beep();
    }
}
