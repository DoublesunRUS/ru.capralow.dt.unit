/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.coverageview;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.text.IMarkSelection;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Tracks the current selection of Java elements in a workbench page and
 * synchronizes the selection of a target view. The selection is either taken
 * from a structured viewer of from an editor input.
 */
class SelectionTracker
{

    private final IViewPart targetview;
    private final StructuredViewer target;

    private boolean enabled = false;
    private URI currentSelection = null;

    private final ISelectionListener listener = new ISelectionListener()
    {
        @Override
        public void selectionChanged(IWorkbenchPart part, ISelection selection)
        {
            if (part != targetview)
            {
                if (selection instanceof IStructuredSelection)
                {
                    IStructuredSelection ssel = (IStructuredSelection)selection;
                    if (ssel.size() == 1)
                    {
                        URI element = getJavaElement(ssel.getFirstElement());
                        if (element != null)
                            applySelection(element, false);
                    }
                    return;
                }
                if (part instanceof IEditorPart)
                {
                    URI element = getJavaElement(((IEditorPart)part).getEditorInput());
                    if (element != null)
                    {
                        element = findElementAtCursor(element, selection);
                        applySelection(element, false);
                    }
                }
            }
        }

        /**
         * Try to identify a nested java element of the given element from a textual
         * selection in its source code. This might be possible if the given element is
         * a compilation unit or class file.
         *
         * @param unit unit to search
         * @param selection selection within this unit
         * @return nested element or the unit itself
         */
        private URI findElementAtCursor(URI unit, ISelection selection)
        {
            int pos = -1;
            if (selection instanceof ITextSelection)
            {
                pos = ((ITextSelection)selection).getOffset();
            }
            if (selection instanceof IMarkSelection)
            {
                pos = ((IMarkSelection)selection).getOffset();
            }
            if (pos == -1)
                return unit;
            URI element = null;
            // try {
            // switch (unit.getElementType()) {
            // case IJavaElement.COMPILATION_UNIT:
            // element = ((ICompilationUnit) unit).getElementAt(pos);
            // break;
            // case IJavaElement.CLASS_FILE:
            // element = ((IClassFile) unit).getElementAt(pos);
            // break;
            // }
            // }
            return element == null ? unit : element;
        }

        /**
         * Try to derive a java element handle from the given object.
         *
         * @param object base object
         * @return java element handle or <code>null</code>
         */
        private URI getJavaElement(Object object)
        {
            if (object instanceof URI)
            {
                return (URI)object;
            }
            if (object instanceof IAdaptable)
            {
                IAdaptable a = (IAdaptable)object;
                return a.getAdapter(URI.class);
            }
            return null;
        }
    };

    /**
     * Creates a new tracker for the given target view and viewer. The tracker
     * registers itself with the workbench and must be {@link #dispose()}d when it
     * is no longer used.
     *
     * @param targetview view to wich the workbench selections are applied
     * @param target viewer to wich the workbench selections are applied
     */
    SelectionTracker(IViewPart targetview, StructuredViewer target)
    {
        this.targetview = targetview;
        this.target = target;
        targetview.getSite().getPage().addPostSelectionListener(listener);
    }

    /**
     * Disposes the tracking functionality. This must be called before the target
     * view and viewer get destroyed.
     */
    public void dispose()
    {
        targetview.getSite().getPage().removePostSelectionListener(listener);
    }

    /**
     * Enables or disables the tracker. If the tracker becomes enabled the last
     * workbench selection is immediately applied.
     *
     * @param enabled flag whether the tracker should become enabled
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        if (enabled && currentSelection != null)
        {
            applySelection(currentSelection, true);
        }
    }

    /**
     * Selects the given element in the taget viewer when the tracker is enabled and
     * the target view is not active. This conditions can be overruled by the
     * <code>force</code> parameter.
     *
     * @param element element to select
     * @param force if <code>true</code> the selection is applied in any case
     */
    private void applySelection(URI element, boolean force)
    {
        currentSelection = element;
        if (force || (enabled && targetview != targetview.getSite().getPage().getActivePart()))
        {
            target.setSelection(new StructuredSelection(element), true);
        }
    }

}
