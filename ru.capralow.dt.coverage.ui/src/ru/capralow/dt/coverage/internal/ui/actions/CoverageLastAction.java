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
package ru.capralow.dt.coverage.internal.ui.actions;

import org.eclipse.debug.ui.actions.RelaunchLastAction;

import ru.capralow.dt.coverage.core.CoverageTools;
import ru.capralow.dt.coverage.internal.ui.CoverageUIPlugin;
import ru.capralow.dt.coverage.internal.ui.UIMessages;

/**
 * Action to re-launch the last launch in coverage mode.
 */
public class CoverageLastAction extends RelaunchLastAction {

	@Override
	public String getMode() {
		return CoverageTools.LAUNCH_MODE;
	}

	@Override
	public String getLaunchGroupId() {
		return CoverageUIPlugin.ID_COVERAGE_LAUNCH_GROUP;
	}

	@Override
	protected String getText() {
		return UIMessages.CoverageLastAction_label;
	}

	@Override
	protected String getTooltipText() {
		return UIMessages.CoverageLastAction_label;
	}

	@Override
	protected String getDescription() {
		return UIMessages.CoverageLastAction_label;
	}

	@Override
	protected String getCommandId() {
		return "ru.capralow.dt.coverage.ui.commands.CoverageLast"; //$NON-NLS-1$
	}

}
