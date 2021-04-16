/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.annotation;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.xtext.ui.editor.XtextEditor;

import com._1c.g5.v8.dt.md.ui.editor.base.DtGranularEditor;

import ru.capralow.dt.coverage.internal.ui.EditorUtility;

/**
 * Tracks the workbench editors and to attach coverage annotation models.
 */
public class EditorTracker
{

    private static void annotateEditor(IWorkbenchPartReference partref)
    {
        IWorkbenchPart part = partref.getPart(false);
        if (part instanceof DtGranularEditor)
        {
            @SuppressWarnings("unchecked")
            XtextEditor editor = EditorUtility.getModuleEditor((DtGranularEditor<EObject>)part);

            if (editor == null)
                return;

            CoverageAnnotationModel.attach(editor);
        }
    }

    private final IWorkbench workbench;

    private IWindowListener windowListener = new IWindowListener()
    {
        @Override
        public void windowActivated(IWorkbenchWindow window)
        {
            // Нечего делать
        }

        @Override
        public void windowClosed(IWorkbenchWindow window)
        {
            window.getPartService().removePartListener(partListener);
        }

        @Override
        public void windowDeactivated(IWorkbenchWindow window)
        {
            // Нечего делать
        }

        @Override
        public void windowOpened(IWorkbenchWindow window)
        {
            window.getPartService().addPartListener(partListener);
        }
    };

    private IPartListener2 partListener = new IPartListener2()
    {
        @Override
        public void partActivated(IWorkbenchPartReference partref)
        {
            // Нечего делать
        }

        @Override
        public void partBroughtToTop(IWorkbenchPartReference partref)
        {
            // Нечего делать
        }

        @Override
        public void partClosed(IWorkbenchPartReference partref)
        {
            // Нечего делать
        }

        @Override
        public void partDeactivated(IWorkbenchPartReference partref)
        {
            // Нечего делать
        }

        @Override
        public void partHidden(IWorkbenchPartReference partref)
        {
            // Нечего делать
        }

        @Override
        public void partInputChanged(IWorkbenchPartReference partref)
        {
            // Нечего делать
        }

        @Override
        public void partOpened(IWorkbenchPartReference partref)
        {
            annotateEditor(partref);
        }

        @Override
        public void partVisible(IWorkbenchPartReference partref)
        {
            // Нечего делать
        }
    };

    public EditorTracker(IWorkbench workbench)
    {
        this.workbench = workbench;
        for (final IWorkbenchWindow w : workbench.getWorkbenchWindows())
        {
            w.getPartService().addPartListener(partListener);
        }
        workbench.addWindowListener(windowListener);
        annotateAllEditors();
    }

    public void dispose()
    {
        workbench.removeWindowListener(windowListener);
        for (final IWorkbenchWindow w : workbench.getWorkbenchWindows())
        {
            w.getPartService().removePartListener(partListener);
        }
    }

    private void annotateAllEditors()
    {
        for (final IWorkbenchWindow w : workbench.getWorkbenchWindows())
        {
            for (final IWorkbenchPage p : w.getPages())
            {
                for (final IEditorReference e : p.getEditorReferences())
                {
                    annotateEditor(e);
                }
            }
        }
    }

}
