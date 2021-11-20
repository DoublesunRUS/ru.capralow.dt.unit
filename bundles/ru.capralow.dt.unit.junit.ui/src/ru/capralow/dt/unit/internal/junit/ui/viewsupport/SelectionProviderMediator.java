/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui.viewsupport;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

/**
 * A selection provider for view parts with more that one viewer.
 * Tracks the focus of the viewers to provide the correct selection.
 */
public class SelectionProviderMediator
    implements IPostSelectionProvider
{

    private StructuredViewer[] fViewers;

    private StructuredViewer fViewerInFocus;

    private ListenerList<ISelectionChangedListener> fSelectionChangedListeners;

    private ListenerList<ISelectionChangedListener> fPostSelectionChangedListeners;

    /**
     * @param viewers All viewers that can provide a selection
     * @param viewerInFocus the viewer currently in focus or <code>null</code>
     */
    public SelectionProviderMediator(StructuredViewer[] viewers, StructuredViewer viewerInFocus)
    {
        Assert.isNotNull(viewers);
        fViewers = viewers;
        InternalListener listener = new InternalListener();
        fSelectionChangedListeners = new ListenerList<>();
        fPostSelectionChangedListeners = new ListenerList<>();
        fViewerInFocus = viewerInFocus;

        for (StructuredViewer viewer : fViewers)
        {
            viewer.addSelectionChangedListener(listener);
            viewer.addPostSelectionChangedListener(new InternalPostSelectionListener());
            Control control = viewer.getControl();
            control.addFocusListener(listener);
        }
    }

    @Override
    public void addPostSelectionChangedListener(ISelectionChangedListener listener)
    {
        fPostSelectionChangedListeners.add(listener);
    }

    /*
     * @see ISelectionProvider#addSelectionChangedListener
     */
    @Override
    public void addSelectionChangedListener(ISelectionChangedListener listener)
    {
        fSelectionChangedListeners.add(listener);
    }

    /*
     * @see ISelectionProvider#getSelection
     */
    @Override
    public ISelection getSelection()
    {
        if (fViewerInFocus != null)
        {
            return fViewerInFocus.getSelection();
        }
        return StructuredSelection.EMPTY;
    }

    /**
     * Returns the viewer in focus or null if no viewer has the focus
     * @return returns the current viewer in focus
     */
    public StructuredViewer getViewerInFocus()
    {
        return fViewerInFocus;
    }

    @Override
    public void removePostSelectionChangedListener(ISelectionChangedListener listener)
    {
        fPostSelectionChangedListeners.remove(listener);
    }

    /*
     * @see ISelectionProvider#removeSelectionChangedListener
     */
    @Override
    public void removeSelectionChangedListener(ISelectionChangedListener listener)
    {
        fSelectionChangedListeners.remove(listener);
    }

    /*
     * @see ISelectionProvider#setSelection
     */
    @Override
    public void setSelection(ISelection selection)
    {
        if (fViewerInFocus != null)
        {
            fViewerInFocus.setSelection(selection);
        }
    }

    /**
     * @param selection
     * @param reveal
     */
    public void setSelection(ISelection selection, boolean reveal)
    {
        if (fViewerInFocus != null)
        {
            fViewerInFocus.setSelection(selection, reveal);
        }
    }

    private void doFocusChanged(Widget control)
    {
        for (StructuredViewer viewer : fViewers)
        {
            if (viewer.getControl() == control)
            {
                propagateFocusChanged(viewer);
                return;
            }
        }
    }

    private void firePostSelectionChanged()
    {
        if (fPostSelectionChangedListeners != null)
        {
            SelectionChangedEvent event = new SelectionChangedEvent(this, getSelection());

            for (ISelectionChangedListener listener : fPostSelectionChangedListeners)
            {
                listener.selectionChanged(event);
            }
        }
    }

    private void fireSelectionChanged()
    {
        if (fSelectionChangedListeners != null)
        {
            SelectionChangedEvent event = new SelectionChangedEvent(this, getSelection());

            for (ISelectionChangedListener listener : fSelectionChangedListeners)
            {
                listener.selectionChanged(event);
            }
        }
    }

    final void doPostSelectionChanged(SelectionChangedEvent event)
    {
        ISelectionProvider provider = event.getSelectionProvider();
        if (provider == fViewerInFocus)
        {
            firePostSelectionChanged();
        }
    }

    final void doSelectionChanged(SelectionChangedEvent event)
    {
        ISelectionProvider provider = event.getSelectionProvider();
        if (provider == fViewerInFocus)
        {
            fireSelectionChanged();
        }
    }

    final void propagateFocusChanged(StructuredViewer viewer)
    {
        if (viewer != fViewerInFocus)
        { // OK to compare by identity
            fViewerInFocus = viewer;
            fireSelectionChanged();
            firePostSelectionChanged();
        }
    }

    private class InternalListener
        implements ISelectionChangedListener, FocusListener
    {
        /*
         * @see FocusListener#focusGained
         */
        @Override
        public void focusGained(FocusEvent e)
        {
            doFocusChanged(e.widget);
        }

        /*
         * @see FocusListener#focusLost
         */
        @Override
        public void focusLost(FocusEvent e)
        {
            // do not reset due to focus behavior on GTK
            //fViewerInFocus= null;
        }

        /*
         * @see ISelectionChangedListener#selectionChanged
         */
        @Override
        public void selectionChanged(SelectionChangedEvent event)
        {
            doSelectionChanged(event);
        }
    }

    private class InternalPostSelectionListener
        implements ISelectionChangedListener
    {
        @Override
        public void selectionChanged(SelectionChangedEvent event)
        {
            doPostSelectionChanged(event);
        }

    }
}
