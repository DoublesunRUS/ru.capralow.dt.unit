/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui.viewsupport;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;

/*package*/ class HistoryDropDownAction<E>
    extends Action
{

    public static final int RESULTS_IN_DROP_DOWN = 10;

    private ViewHistory<E> fHistory;

    private Menu fMenu;

    HistoryDropDownAction(ViewHistory<E> history)
    {
        fHistory = history;
        fMenu = null;
        setMenuCreator(new HistoryMenuCreator());
        fHistory.configureHistoryDropDownAction(this);
    }

    @Override
    public void run()
    {
        new HistoryListAction<>(fHistory).run();
    }

    private class HistoryAction
        extends Action
    {
        private final E fElement;

        HistoryAction(E element, int accelerator)
        {
            super("", AS_RADIO_BUTTON); //$NON-NLS-1$
            Assert.isNotNull(element);
            fElement = element;

            String label = fHistory.getText(element);
            if (accelerator < 10)
            {
                //add the numerical accelerator
                label = new StringBuilder().append('&').append(accelerator).append(' ').append(label).toString();
            }

            setText(label);
            setImageDescriptor(fHistory.getImageDescriptor(element));
        }

        @Override
        public void run()
        {
            if (isChecked())
            {
                fHistory.setActiveEntry(fElement);
            }
        }
    }

    private class HistoryMenuCreator
        implements IMenuCreator
    {

        @Override
        public void dispose()
        {
            fHistory = null;

            if (fMenu != null)
            {
                fMenu.dispose();
                fMenu = null;
            }
        }

        @Override
        public Menu getMenu(Control parent)
        {
            if (fMenu != null)
            {
                fMenu.dispose();
            }
            final MenuManager manager = new MenuManager();
            manager.setRemoveAllWhenShown(true);
            manager.addMenuListener(new IMenuListener()
            {
                @Override
                public void menuAboutToShow(IMenuManager manager2)
                {
                    if (fHistory == null)
                    {
                        return;
                    }
                    List<E> entries = fHistory.getHistoryEntries();
                    boolean checkOthers = addEntryMenuItems(manager2, entries);

                    manager2.add(new Separator());

                    Action others = new HistoryListAction<>(fHistory);
                    others.setChecked(checkOthers);
                    manager2.add(others);

                    Action clearAction = fHistory.getClearAction();
                    if (clearAction != null)
                    {
                        manager2.add(clearAction);
                    }

                    manager2.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

                    fHistory.addMenuEntries(manager);
                }

                private boolean addEntryMenuItems(IMenuManager manager2, List<E> entries)
                {
                    if (entries.isEmpty())
                    {
                        return false;
                    }

                    boolean checkOthers = true;
                    int min = Math.min(entries.size(), RESULTS_IN_DROP_DOWN);
                    for (int i = 0; i < min; i++)
                    {
                        E entry = entries.get(i);
                        HistoryAction action = new HistoryAction(entry, i + 1);
                        boolean check = entry.equals(fHistory.getCurrentEntry());
                        action.setChecked(check);
                        if (check)
                        {
                            checkOthers = false;
                        }
                        manager2.add(action);
                    }
                    return checkOthers;
                }
            });

            fMenu = manager.createContextMenu(parent);

            //workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=129973
            final Display display = parent.getDisplay();
            fMenu.addMenuListener(new MenuAdapter()
            {
                @Override
                public void menuHidden(final MenuEvent e)
                {
                    display.asyncExec(() -> {
                        manager.removeAll();
                        if (fMenu != null)
                        {
                            fMenu.dispose();
                            fMenu = null;
                        }
                    });
                }
            });
            return fMenu;
        }

        @Override
        public Menu getMenu(Menu parent)
        {
            return null;
        }
    }
}
