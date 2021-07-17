/**
 * Copyright (c) 2020, Aleksandr Kapralov
 */
package ru.capralow.dt.coverage.internal.ui;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.xtext.ui.editor.XtextEditor;

import com._1c.g5.v8.dt.md.ui.editor.base.DtGranularEditor;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.ui.editor.input.IDtEditorInput;

public final class EditorUtility
{

    public static XtextEditor getModuleEditor(DtGranularEditor<EObject> editor)
    {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

        for (IEditorReference editorReference : page.getEditorReferences())
        {
            final IEditorPart[] editorPart = new IEditorPart[1];
            editorPart[0] = editorReference.getEditor(false);

            XtextEditor xtextEditor = getXtextEditorFromEditorPart(editorPart[0], editor);
            if (xtextEditor == null)
                continue;

            return xtextEditor;
        }

        return null;
    }

    private static XtextEditor getXtextEditorFromEditorPart(IEditorPart editorPart, DtGranularEditor<EObject> editor)
    {
        if (editorPart == null)
            return null;

        if (editorPart instanceof XtextEditor)
        {
            // Если же мы идем по первой ветке, то есть у нас нет "formEditor", то берем
            // EditorInput, он должен быть файловым, и по файлу уже определяем, например,
            // при помощи сервиса
            // com._1c.g5.v8.dt.core.filesystem.IQualifiedNameFilePathConverter

            return (XtextEditor)editorPart;

        }
        else if (editorPart instanceof FormEditor)
        {
            FormEditor formEditor = (FormEditor)editorPart;
            if (!(formEditor instanceof DtGranularEditor))
                return null;

            @SuppressWarnings("unchecked")
            IDtEditorInput<CommonModule> editorInput = ((DtGranularEditor<CommonModule>)formEditor).getEditorInput();
            if (!editorInput.exists())
                return null;

            EObject editorModel = editor.getModel();
            if (!(editorModel instanceof CommonModule))
                return null;

            if (!editorInput.getModel().getUuid().equals(((CommonModule)editorModel).getUuid()))
                return null;

            return formEditor.findPage("editors.pages.module").getAdapter(XtextEditor.class); //$NON-NLS-1$

        }
        else
        {
            return editorPart.getAdapter(XtextEditor.class);

        }
    }

    private EditorUtility()
    {
        // Нечего делать
    }

}
