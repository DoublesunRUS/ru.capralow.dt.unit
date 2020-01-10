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
package ru.capralow.dt.coverage.internal.ui.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import ru.capralow.dt.coverage.internal.ui.CoverageUIPlugin;
import ru.capralow.dt.coverage.internal.ui.UIMessages;

/**
 * Status handler that issues an error message when no coverage data has been
 * found.
 */
public class NoCoverageDataHandler implements IStatusHandler {

	@Override
	public Object handleStatus(IStatus status, Object source) throws CoreException {
		Shell parent = CoverageUIPlugin.getInstance().getShell();
		String title = UIMessages.NoCoverageDataError_title;
		String message = UIMessages.NoCoverageDataError_message;

		MessageDialog.openError(parent, title, message);
		return Boolean.FALSE;
	}

}
