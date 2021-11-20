/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui.viewsupport;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;

/**
 * History support for a view.
 *
 * @param <E> the type of elements managed by this history
 */
public abstract class ViewHistory<E>
{

    /**
     * @param manager
     */
    public abstract void addMenuEntries(MenuManager manager);

    /**
     * Configure the history drop down action.
     * Clients typically want to set a tooltip and an image.
     *
     * @param action the action
     */
    public abstract void configureHistoryDropDownAction(IAction action);

    /**
     * Configure the history List action.
     * Clients typically want to set a text and an image.
     *
     * @param action the action
     */
    public abstract void configureHistoryListAction(IAction action);

    /**
     * @return a history drop down action, ready for inclusion in a view toolbar
     */
    public final IAction createHistoryDropDownAction()
    {
        return new HistoryDropDownAction<>(this);
    }

    /**
     * @return action to clear history entries, or <code>null</code>
     */
    public abstract Action getClearAction();

    /**
     * @return the active entry from the history
     */
    public abstract E getCurrentEntry();

    /**
     * @return An unmodifiable list of history entries, can be empty. The list
     *         is sorted by age, youngest first.
     */
    public abstract List<E> getHistoryEntries();

    /**
     * @return String
     */
    public abstract String getHistoryListDialogMessage();

    /**
     * @return String
     */
    public abstract String getHistoryListDialogTitle();

    /**
     * @param element the element to render
     * @return the image descriptor for the given element, or <code>null</code>
     */
    public abstract ImageDescriptor getImageDescriptor(Object element);

    /**
     * @return int
     */
    public abstract int getMaxEntries();

    /**
     * @return String
     */
    public abstract String getMaxEntriesMessage();

    /**
     * @return Shell
     */
    public abstract Shell getShell();

    /**
     * @param element the element to render
     * @return the label text for the given element
     */
    public abstract String getText(E element);

    /**
     * @param entry the entry to activate, or <code>null</code> if none should be active
     */
    public abstract void setActiveEntry(E entry);

    /**
     * @param remainingEntries all the remaining history entries, can be empty
     * @param activeEntry the entry to activate, or <code>null</code> if none should be active
     */
    public abstract void setHistoryEntries(List<E> remainingEntries, E activeEntry);

    /**
     * @param maxEntries
     */
    public abstract void setMaxEntries(int maxEntries);

}
