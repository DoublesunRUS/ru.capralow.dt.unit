package ru.capralow.dt.unit.launcher.plugin.internal.ui.junit;

import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com._1c.g5.v8.dt.bm.index.emf.IBmEmfIndexManager;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;

public class TestCaseListener extends SelectionAdapter {

	OpenTestCaseAction action;

	public TestCaseListener(IBmEmfIndexManager bmEmfIndexManager, IResourceLookup resourceLookup,
			IV8ProjectManager projectManager) {
		action = new OpenTestCaseAction(bmEmfIndexManager, resourceLookup, projectManager);
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent event) {
		Tree tree = (Tree) event.getSource();
		TreeItem treeItem = tree.getSelection()[0];
		Object selectedObject = treeItem.getData();
		if (!(selectedObject instanceof ITestCaseElement))
			return;

		ITestCaseElement testCase = (ITestCaseElement) selectedObject;

		action.run(testCase);

	}

}
