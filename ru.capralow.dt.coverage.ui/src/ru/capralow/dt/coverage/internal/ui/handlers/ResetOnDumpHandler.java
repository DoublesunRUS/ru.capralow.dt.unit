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

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;

import ru.capralow.dt.coverage.internal.ui.CoverageUIPlugin;
import ru.capralow.dt.coverage.internal.ui.UIPreferences;

/**
 * Handler to toggle the "reset on dump" option.
 */
public class ResetOnDumpHandler extends AbstractHandler implements IElementUpdater {

	private final IPreferenceStore preferenceStore;

	public ResetOnDumpHandler() {
		preferenceStore = CoverageUIPlugin.getInstance().getPreferenceStore();
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		final boolean flag = preferenceStore.getBoolean(UIPreferences.PREF_RESET_ON_DUMP);
		preferenceStore.setValue(UIPreferences.PREF_RESET_ON_DUMP, !flag);
		return null;
	}

	public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
		element.setChecked(preferenceStore.getBoolean(UIPreferences.PREF_RESET_ON_DUMP));
	}

}
