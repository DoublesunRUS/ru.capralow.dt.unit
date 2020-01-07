package ru.capralow.dt.unit.launcher.plugin.internal.ui.junit;

import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import ru.capralow.dt.unit.launcher.plugin.internal.ui.UnitLauncherUiPlugin;

public class TestCaseListener extends SelectionAdapter {

	OpenTestCaseAction action;

	public TestCaseListener() {
		action = UnitLauncherUiPlugin.getInstance().getInjector().getInstance(OpenTestCaseAction.class);
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
