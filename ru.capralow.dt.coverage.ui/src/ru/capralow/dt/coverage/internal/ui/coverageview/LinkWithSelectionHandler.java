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
 *    Brock Janiczak - link with selection option (SF #1774547)
 *
 ******************************************************************************/
package ru.capralow.dt.coverage.internal.ui.coverageview;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;

/**
 * Handle to toggle linking of the coverage view's selection with the current
 * selection in the workbench.
 */
class LinkWithSelectionHandler extends AbstractHandler implements IElementUpdater {

	public static final String ID = "ru.capralow.dt.coverage.ui.linkWithSelection"; //$NON-NLS-1$

	private final ViewSettings settings;
	private final SelectionTracker tracker;

	LinkWithSelectionHandler(ViewSettings settings, SelectionTracker tracker) {
		this.settings = settings;
		this.tracker = tracker;
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		final boolean flag = !settings.isLinked();
		settings.setLinked(flag);
		tracker.setEnabled(flag);
		return null;
	}

	public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
		element.setChecked(settings.isLinked());
	}

}
