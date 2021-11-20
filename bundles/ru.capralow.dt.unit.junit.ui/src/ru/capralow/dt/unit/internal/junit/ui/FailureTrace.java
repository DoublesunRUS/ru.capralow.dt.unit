/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.OpenStrategy;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;

import ru.capralow.dt.unit.internal.junit.JUnitPreferencesConstants;
import ru.capralow.dt.unit.internal.junit.model.TestElement;

/**
 * A panel that shows a stack trace of a failed test.
 */
public class FailureTrace
    implements IMenuListener
{

    private static final int MAX_LABEL_LENGTH = 256;

    private static final String FAILURE_FONT = "ru.capralow.dt.unit.failurePaneFont"; //$NON-NLS-1$
    static final String FRAME_PREFIX = "at "; //$NON-NLS-1$

    private Table fTable;
    private TestRunnerViewPart fTestRunner;
    private String fInputTrace;
    private final Clipboard fClipboard;
    private TestElement fFailure;
    private CompareResultsAction fCompareAction;
    private final FailureTableDisplay fFailureTableDisplay;
    private IPropertyChangeListener fFontPropertyChangeListener;
//    private ShowStackTraceInConsoleViewAction fShowTraceInConsoleAction;

    public FailureTrace(Composite parent, Clipboard clipboard, TestRunnerViewPart testRunner, ToolBar toolBar)
    {
        Assert.isNotNull(clipboard);

        // fill the failure trace viewer toolbar
        ToolBarManager failureToolBarmanager = new ToolBarManager(toolBar);
        // TODO: Реализовать отображение стека EDT
//        fShowTraceInConsoleAction = new ShowStackTraceInConsoleViewAction(this);
//        fShowTraceInConsoleAction.setEnabled(false);
//        failureToolBarmanager.add(fShowTraceInConsoleAction);
        failureToolBarmanager.add(new EnableStackFilterAction(this));
        fCompareAction = new CompareResultsAction(this);
        fCompareAction.setEnabled(false);
        failureToolBarmanager.add(fCompareAction);
        failureToolBarmanager.update(true);

        fTable = new Table(parent, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
        fTable.setFont(JFaceResources.getFont(FAILURE_FONT));
        fTestRunner = testRunner;
        fClipboard = clipboard;

        OpenStrategy handler = new OpenStrategy(fTable);
        handler.addOpenListener(e -> {
            if (fTable.getSelectionIndex() == 0 && fFailure.isComparisonFailure())
            {
                fCompareAction.run();
            }
            if (fTable.getSelection().length != 0)
            {
                Action a = createOpenEditorAction(getSelectedText());
                if (a != null)
                {
                    a.run();
                }
            }
        });

        fFontPropertyChangeListener = new FontPropertyChangeListener();
        JFaceResources.getFontRegistry().addListener(fFontPropertyChangeListener);

        initMenu();

        fFailureTableDisplay = new FailureTableDisplay(fTable);
    }

    /**
     * Clears the non-stack trace info
     */
    public void clear()
    {
        fTable.removeAll();
        fInputTrace = null;
    }

    public void dispose()
    {
        JFaceResources.getFontRegistry().removeListener(fFontPropertyChangeListener);
    }

    public TestElement getFailedTest()
    {
        return fFailure;
    }

    public FailureTableDisplay getFailureTableDisplay()
    {
        return fFailureTableDisplay;
    }

    public Shell getShell()
    {
        return fTable.getShell();
    }

    public String getTrace()
    {
        return fInputTrace;
    }

    @Override
    public void menuAboutToShow(IMenuManager manager)
    {
        if (fTable.getSelectionCount() > 0)
        {
            Action a = createOpenEditorAction(getSelectedText());
            if (a != null)
            {
                manager.add(a);
            }
            manager.add(new JUnitCopyAction(FailureTrace.this, fClipboard));
        }
        if (fFailure != null && fFailure.isComparisonFailure())
        {
            manager.add(fCompareAction);
        }
    }

    /**
     * Refresh the table from the trace.
     */
    public void refresh()
    {
        updateTable(fInputTrace);
    }

    /**
     * Shows other information than a stack trace.
     * @param text the informational message to be shown
     */
    public void setInformation(String text)
    {
        clear();
        TableItem tableItem = fFailureTableDisplay.newTableItem();
        tableItem.setText(text);
    }

    /**
     * Shows a TestFailure
     * @param test the failed test
     */
    public void showFailure(TestElement test)
    {
        fFailure = test;
        String trace = ""; //$NON-NLS-1$
        updateEnablement(test);
        if (test != null)
        {
            trace = test.getTrace();
        }
        if (fInputTrace.equals(trace))
        {
            return;
        }
        fInputTrace = trace;
        updateTable(trace);
    }

    public void updateEnablement(TestElement test)
    {
        boolean enableCompare = test != null && test.isComparisonFailure();
        fCompareAction.setEnabled(enableCompare);
        if (enableCompare)
        {
            fCompareAction.updateOpenDialog(test);
        }

        boolean enableShowTraceInConsole = test != null && test.getFailureTrace() != null;
//        fShowTraceInConsoleAction.setEnabled(enableShowTraceInConsole);
    }

    private Action createOpenEditorAction(String traceLine)
    {
        try
        {
            String testName = traceLine;
            int indexOfFramePrefix = testName.indexOf(FRAME_PREFIX);
            if (indexOfFramePrefix == -1)
            {
                return null;
            }
            testName = testName.substring(indexOfFramePrefix);
            testName = testName.substring(FRAME_PREFIX.length(), testName.lastIndexOf('(')).trim();
            int indexOfModuleSeparator = testName.lastIndexOf('/');
            if (indexOfModuleSeparator != -1)
            {
                testName = testName.substring(indexOfModuleSeparator + 1);
            }
            testName = testName.substring(0, testName.lastIndexOf('.'));
            int innerSeparatorIndex = testName.indexOf('$');
            if (innerSeparatorIndex != -1)
            {
                testName = testName.substring(0, innerSeparatorIndex);
            }

            String lineNumber = traceLine;
            lineNumber = lineNumber.substring(lineNumber.indexOf(':') + 1, lineNumber.lastIndexOf(')'));
            int line = Integer.parseInt(lineNumber);
            return new OpenEditorAtLineAction(fTestRunner, testName, line);
        }
        catch (NumberFormatException | IndexOutOfBoundsException e)
        {
            // Нечего делать
        }
        return null;
    }

    private String[] getFilterPatterns()
    {
        if (JUnitPreferencesConstants.getFilterStack())
        {
            return JUnitPreferencesConstants.getFilterPatterns();
        }
        return new String[0];
    }

    private String getSelectedText()
    {
        return fTable.getSelection()[0].getText();
    }

    private void initMenu()
    {
        MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(this);
        Menu menu = menuMgr.createContextMenu(fTable);
        fTable.setMenu(menu);
    }

    private void updateTable(String trace)
    {
        if (trace == null || trace.trim().isEmpty())
        {
            clear();
            return;
        }
        String traceTrimmed = trace.trim();
        fTable.setRedraw(false);
        fTable.removeAll();
        new TextualTrace(traceTrimmed, getFilterPatterns()).display(fFailureTableDisplay, MAX_LABEL_LENGTH);
        fTable.setRedraw(true);
    }

    /**
     * Returns the composite used to present the trace
     * @return The composite
     */
    Composite getComposite()
    {
        return fTable;
    }

    /**
     * Internal property change listener for handling workbench font changes.
     */
    private class FontPropertyChangeListener
        implements IPropertyChangeListener
    {
        /*
         * @see IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
         */
        @Override
        public void propertyChange(PropertyChangeEvent event)
        {
            if (fTable == null)
            {
                return;
            }

            String property = event.getProperty();

            if (FAILURE_FONT.equals(property))
            {
                fTable.setFont(JFaceResources.getFont(FAILURE_FONT));
            }
        }
    }
}
