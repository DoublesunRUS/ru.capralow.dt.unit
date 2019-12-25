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
 * Adapted by Alexander Kapralov
 *
 ******************************************************************************/
package ru.capralow.dt.coverage.internal.ui.coverageview;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;
import org.jacoco.core.analysis.ICoverageNode.CounterEntity;

/**
 * Handler to selects the counter entities shown in the coverage tree.
 */
class SelectCountersHandler extends AbstractHandler implements IElementUpdater {

	public static final String ID = "ru.capralow.dt.coverage.ui.selectCounters"; //$NON-NLS-1$

	private static final String TYPE_PARAMETER = "type"; //$NON-NLS-1$

	private final ViewSettings settings;
	private final CoverageView view;

	public SelectCountersHandler(ViewSettings settings, CoverageView view) {
		this.settings = settings;
		this.view = view;
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		final CounterEntity type = getType(event.getParameters());
		settings.setCounters(type);
		view.refreshViewer();
		return null;
	}

	public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
		final CounterEntity type = getType(parameters);
		element.setChecked(settings.getCounters().equals(type));
	}

	private static CounterEntity getType(Map<?, ?> parameters) {
		return CounterEntity.valueOf((String) parameters.get(TYPE_PARAMETER));
	}

}
