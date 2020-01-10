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
package ru.capralow.dt.coverage.internal.ui.editors;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IPropertyListener;

/**
 * Base class for content providers for {@link ExecutionDataContent}. It handles
 * the viewer update.
 */
abstract class AbstractExecutionDataContentProvider implements IStructuredContentProvider, IPropertyListener {

	private Viewer viewer;

	@Override
	public final Object[] getElements(Object inputElement) {
		final ExecutionDataContent content = (ExecutionDataContent) inputElement;
		return getElements(content);
	}

	@Override
	public final void inputChanged(Viewer viewer2, Object oldInput, Object newInput) {
		this.viewer = viewer2;
		if (oldInput != null) {
			((ExecutionDataContent) oldInput).removePropertyListener(this);
		}
		if (newInput != null) {
			((ExecutionDataContent) newInput).addPropertyListener(this);
		}
	}

	@Override
	public final void dispose() {
	}

	@Override
	public final void propertyChanged(Object source, int propId) {
		viewer.refresh();
	}

	protected abstract Object[] getElements(ExecutionDataContent content);

}
