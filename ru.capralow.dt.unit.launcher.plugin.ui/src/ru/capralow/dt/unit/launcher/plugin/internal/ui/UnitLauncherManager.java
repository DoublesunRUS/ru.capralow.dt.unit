package ru.capralow.dt.unit.launcher.plugin.internal.ui;

import java.lang.reflect.Field;

import org.apache.commons.lang.reflect.FieldUtils;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.jdt.internal.junit.ui.TestRunnerViewPart;
import org.eclipse.jdt.internal.junit.ui.TestViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com._1c.g5.v8.dt.bm.index.emf.IBmEmfIndexManager;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.profiling.core.IProfilingService;
import com._1c.g5.wiring.IManagedService;
import com.google.inject.Inject;

import ru.capralow.dt.unit.launcher.plugin.internal.ui.junit.ShowCoverageResult;
import ru.capralow.dt.unit.launcher.plugin.internal.ui.junit.ShowJUnitResult;
import ru.capralow.dt.unit.launcher.plugin.internal.ui.junit.TestCaseListener;

public class UnitLauncherManager implements IManagedService {

	@Inject
	private IBmEmfIndexManager bmEmfIndexManager;
	@Inject
	private IProfilingService profilingService;
	@Inject
	private IResourceLookup resourceLookup;

	@Inject
	private IV8ProjectManager projectManager;

	private ShowJUnitResult showJUnitResult;
	private TestCaseListener testCaseListener;

	private ShowCoverageResult showCoverageResult;

	@Override
	public void activate() {
		showJUnitResult = new ShowJUnitResult();
		DebugPlugin.getDefault().addDebugEventListener(showJUnitResult);

		showCoverageResult = new ShowCoverageResult();
		DebugPlugin.getDefault().addDebugEventListener(showCoverageResult);

		Display.getDefault().asyncExec(() -> {
			testCaseListener = new TestCaseListener(bmEmfIndexManager, resourceLookup, projectManager);
			IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

			TestRunnerViewPart junitViewPart = (TestRunnerViewPart) activePage.findView(ShowJUnitResult.JUNIT_PANEL_ID);
			try {
				Field testViewerField = FieldUtils.getField(TestRunnerViewPart.class, "fTestViewer", true); //$NON-NLS-1$
				TestViewer testViewer = (TestViewer) FieldUtils.readField(testViewerField, junitViewPart, true);

				Field treeViewerField = FieldUtils.getField(TestViewer.class, "fTreeViewer", true);
				TreeViewer junitPanelViewer = (TreeViewer) FieldUtils.readField(treeViewerField, testViewer, true);

				Tree junitPanelTree = junitPanelViewer.getTree();
				for (Listener listener : junitPanelTree.getListeners(SWT.DefaultSelection))
					junitPanelTree.removeListener(SWT.DefaultSelection, listener);

				junitPanelTree.addSelectionListener(testCaseListener);

			} catch (IllegalAccessException e) {
				UnitLauncherUiPlugin.createErrorStatus(Messages.UnitLauncherManager_Unable_to_add_doubleclick_listener,
						e);

			}
		});

		// profilingService.toggleTargetWaitingState(true);
	}

	@Override
	public void deactivate() {
		// profilingService.toggleTargetWaitingState(false);

		DebugPlugin.getDefault().removeDebugEventListener(showCoverageResult);

		DebugPlugin.getDefault().removeDebugEventListener(showJUnitResult);
	}

}
