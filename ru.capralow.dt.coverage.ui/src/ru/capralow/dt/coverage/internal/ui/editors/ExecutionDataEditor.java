/*******************************************************************************
 * Copyright (c) 2006, 2019 Mountainminds GmbH & Co. KG and Contributors
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Brock Janiczak - initial API and implementation
 *
 ******************************************************************************/
package ru.capralow.dt.coverage.internal.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import ru.capralow.dt.coverage.internal.ui.CoverageUIPlugin;

/**
 * Editor implementation for JaCoCo execution data files.
 */
public class ExecutionDataEditor extends FormEditor {

	public static final String ID = "ru.capralow.dt.coverage.ui.editors.executiondata"; //$NON-NLS-1$

	private final ExecutionDataContent content = new ExecutionDataContent();

	@Override
	protected void addPages() {
		try {
			addPage(new ExecutedClassesPage(this, content));
			addPage(new SessionDataPage(this, content));
		} catch (PartInitException e) {
			CoverageUIPlugin.log(e);
		}
	}

	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		setPartName(getEditorInput().getName());
		content.load(getEditorInput());
		firePropertyChange(PROP_INPUT);
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	@Override
	public void doSaveAs() {
	}

}
