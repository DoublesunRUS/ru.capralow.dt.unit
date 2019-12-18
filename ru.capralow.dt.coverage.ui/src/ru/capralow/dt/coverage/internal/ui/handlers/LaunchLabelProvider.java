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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import ru.capralow.dt.coverage.internal.ui.CoverageUIPlugin;
import ru.capralow.dt.coverage.internal.ui.UIMessages;

/**
 * Internal label provider for {@link ILaunch} objects.
 */
class LaunchLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		return getLaunchText((ILaunch) element);
	}

	@Override
	public Image getImage(Object element) {
		return CoverageUIPlugin.getImage(CoverageUIPlugin.ELCL_DUMP);
	}

	public static String getLaunchText(ILaunch launch) {
		// new launch configuration
		final ILaunchConfiguration config = launch.getLaunchConfiguration();
		if (config == null) {
			return UIMessages.DumpExecutionDataUnknownLaunch_value;
		}
		StringBuilder sb = new StringBuilder(config.getName());
		sb.append(" ["); //$NON-NLS-1$
		try {
			sb.append(config.getType().getName());
		} catch (CoreException e) {
			CoverageUIPlugin.log(e);
		}
		sb.append("]"); //$NON-NLS-1$
		return sb.toString();
	}

}
