/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui;

import java.lang.reflect.Field;

import org.apache.commons.lang.reflect.FieldUtils;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com._1c.g5.wiring.IManagedService;

public class UnitLauncherManager
    implements IManagedService
{

    private ShowJUnitResult showJUnitResult;
//    private TestCaseListener testCaseListener;

    @Override
    public void activate()
    {
        showJUnitResult = new ShowJUnitResult();
        DebugPlugin.getDefault().addDebugEventListener(showJUnitResult);

        Display.getDefault().asyncExec(() -> {
//            testCaseListener = new TestCaseListener();
            IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

            TestRunnerViewPart junitViewPart = (TestRunnerViewPart)activePage.findView(ShowJUnitResult.JUNIT_PANEL_ID);
            try
            {
                Field testViewerField = FieldUtils.getField(TestRunnerViewPart.class, "fTestViewer", true); //$NON-NLS-1$
                TestViewer testViewer = (TestViewer)FieldUtils.readField(testViewerField, junitViewPart, true);

                Field treeViewerField = FieldUtils.getField(TestViewer.class, "fTreeViewer", true); //$NON-NLS-1$
                TreeViewer junitPanelViewer = (TreeViewer)FieldUtils.readField(treeViewerField, testViewer, true);

                Tree junitPanelTree = junitPanelViewer.getTree();
                for (Listener listener : junitPanelTree.getListeners(SWT.DefaultSelection))
                    junitPanelTree.removeListener(SWT.DefaultSelection, listener);

//                junitPanelTree.addSelectionListener(testCaseListener);

            }
            catch (IllegalAccessException | NullPointerException e)
            {
                JUnitUiPlugin.createErrorStatus(Messages.UnitLauncherManager_Unable_to_add_doubleclick_listener, e);

            }
        });
    }

    @Override
    public void deactivate()
    {
        DebugPlugin.getDefault().removeDebugEventListener(showJUnitResult);
    }

}
