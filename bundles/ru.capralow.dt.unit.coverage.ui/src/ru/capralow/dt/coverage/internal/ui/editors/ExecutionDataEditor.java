/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import ru.capralow.dt.coverage.internal.ui.CoverageUiPlugin;

/**
 * Editor implementation for JaCoCo execution data files.
 */
public class ExecutionDataEditor
    extends FormEditor
{

    public static final String ID = "ru.capralow.dt.coverage.ui.editors.executiondata"; //$NON-NLS-1$

    private final ExecutionDataContent content = new ExecutionDataContent();

    @Override
    public void doSave(IProgressMonitor monitor)
    {
    }

    @Override
    public void doSaveAs()
    {
    }

    @Override
    public boolean isSaveAsAllowed()
    {
        return false;
    }

    @Override
    protected void addPages()
    {
        try
        {
            addPage(new ExecutedClassesPage(this, content));
            addPage(new SessionDataPage(this, content));
        }
        catch (PartInitException e)
        {
            CoverageUiPlugin.log(e);
        }
    }

    @Override
    protected void setInput(IEditorInput input)
    {
        super.setInput(input);
        setPartName(getEditorInput().getName());
        content.load(getEditorInput());
        firePropertyChange(PROP_INPUT);
    }

}
