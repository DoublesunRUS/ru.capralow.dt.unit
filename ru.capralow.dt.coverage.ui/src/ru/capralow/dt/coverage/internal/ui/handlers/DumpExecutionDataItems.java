/*******************************************************************************
 * Copyright (c) 2006, 2019 Mountainminds GmbH & Co. KG and Contributors
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *
 ******************************************************************************/
package ru.capralow.dt.coverage.internal.ui.handlers;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import ru.capralow.dt.coverage.core.CoverageTools;
import ru.capralow.dt.coverage.core.launching.ICoverageLaunch;
import ru.capralow.dt.coverage.internal.ui.CoverageUIPlugin;

/**
 * Dynamically created menu items for selecting the coverage launch to dump
 * execution data from.
 */
public class DumpExecutionDataItems extends ContributionItem {

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public void fill(final Menu menu, int index) {
		for (ICoverageLaunch launch : CoverageTools.getRunningCoverageLaunches()) {
			createItem(menu, index++, launch);
		}
	}

	private void createItem(final Menu parent, final int index, final ICoverageLaunch launch) {
		final MenuItem item = new MenuItem(parent, SWT.PUSH, index);
		item.setImage(CoverageUIPlugin.getImage(CoverageUIPlugin.ELCL_DUMP));
		item.setText(LaunchLabelProvider.getLaunchText(launch));
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DumpExecutionDataHandler.requestDump(launch);
			}
		});
	}

}
