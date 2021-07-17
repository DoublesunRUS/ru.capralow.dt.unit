/*******************************************************************************
 * Copyright (c) 2006, 2019 Mountainminds GmbH & Co. KG and Contributors
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Evgeny Mandrikov - initial API and implementation
 *
 ******************************************************************************/
package ru.capralow.dt.coverage.ui;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.ui.IEditorPart;
import org.junit.After;
import org.junit.Test;

import ru.capralow.dt.coverage.internal.ui.CoverageUIPlugin;

public class ContextualLaunchableTesterTest {

	public static class FakeLaunchShortcut implements ILaunchShortcut {
		public void launch(IEditorPart editor, String mode) {
		}

		public void launch(ISelection selection, String mode) {
		}
	}

	private static class LogListener implements ILogListener {
		final List<IStatus> statuses = new ArrayList<>();

		public synchronized void logging(IStatus status, String plugin) {
			if (status.getSeverity() == IStatus.ERROR) {
				statuses.add(status);
			}
		}
	}

	private static final SWTWorkbenchBot bot = new SWTWorkbenchBot();

	@Test
	public void error_message_should_contain_delegate_shortcut_id() throws Exception {
		final LogListener logListener = new LogListener();
		Platform.addLogListener(logListener);

		final String projectName = "ContextualLaunchableTesterTest";
		new JavaProjectKit(projectName);

		final SWTBotView view = bot.viewByTitle("Project Explorer");
		view.show();
		final SWTBotTree tree = view.bot().tree();
		tree.setFocus();
		tree.select(projectName).contextMenu("Coverage As").click();

		Platform.removeLogListener(logListener);

		final IStatus actualStatus = logListener.statuses.get(1);
		assertEquals(CoverageUIPlugin.ID, actualStatus.getPlugin());
		assertEquals(
				"Launch shortcut 'ru.capralow.dt.coverage.ui.ContextualLaunchableTesterTest.fakeShortcut' enablement expression caused exception.",
				actualStatus.getMessage());
		assertEquals(
				"No property tester contributes a property ru.capralow.dt.coverage.unknownProperty to type class org.eclipse.core.internal.resources.Project",
				actualStatus.getException().getMessage());
	}

	@After
	public void resetWorkbench() {
		bot.resetWorkbench();
	}

}
