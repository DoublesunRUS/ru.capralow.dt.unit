package ru.capralow.dt.coverage.internal.ui;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.xtext.ui.editor.XtextEditor;

import com._1c.g5.v8.dt.md.ui.editor.base.DtGranularEditor;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.ui.editor.input.IDtEditorInput;

public class EditorUtility {

	public static XtextEditor getModuleEditor(DtGranularEditor<CommonModule> editor) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

		for (IEditorReference editorReference : page.getEditorReferences()) {
			final IEditorPart[] editorPart = new IEditorPart[1];
			editorPart[0] = editorReference.getEditor(false);
			if (editorPart[0] == null)
				continue;

			if (editorPart[0] instanceof XtextEditor) {
				// Если же мы идем по первой ветке, то есть у нас нет "formEditor", то берем
				// EditorInput, он должен быть файловым, и по файлу уже определяем, например,
				// при помощи сервиса
				// com._1c.g5.v8.dt.core.filesystem.IQualifiedNameFilePathConverter

				return (XtextEditor) editorPart[0];

			} else if (editorPart[0] instanceof FormEditor) {
				FormEditor formEditor = (FormEditor) editorPart[0];
				if (!(formEditor instanceof DtGranularEditor))
					continue;

				@SuppressWarnings("unchecked")
				IDtEditorInput<CommonModule> editorInput = ((DtGranularEditor<CommonModule>) formEditor)
						.getEditorInput();
				if (!editorInput.exists())
					continue;

				if (!editorInput.getModel().getUuid().equals(editor.getModel().getUuid()))
					continue;

				return formEditor.findPage("editors.pages.module").getAdapter(XtextEditor.class); //$NON-NLS-1$

			} else if (editorPart[0] != null) {
				return editorPart[0].getAdapter(XtextEditor.class);

			}

		}

		return null;
	}

}
