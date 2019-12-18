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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import ru.capralow.dt.coverage.core.CoverageTools;
import ru.capralow.dt.coverage.core.ICoverageSession;
import ru.capralow.dt.coverage.internal.ui.UIMessages;
import ru.capralow.dt.coverage.internal.ui.editors.CoverageSessionInput;
import ru.capralow.dt.coverage.internal.ui.editors.ExecutionDataEditor;

/**
 * Handler to open the execution data of the current session in an editor.
 */
public class OpenSessionExecutionDataHandler extends AbstractSessionManagerHandler {

	public OpenSessionExecutionDataHandler() {
		super(CoverageTools.getSessionManager());
	}

	@Override
	public boolean isEnabled() {
		return sessionManager.getActiveSession() != null;
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		final ICoverageSession session = sessionManager.getActiveSession();
		final IEditorInput input = new CoverageSessionInput(session);
		final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		try {
			window.getActivePage().openEditor(input, ExecutionDataEditor.ID);
		} catch (PartInitException e) {
			throw new ExecutionException(UIMessages.ExecutionDataEditorOpeningError_message, e);
		}
		return null;
	}
}
