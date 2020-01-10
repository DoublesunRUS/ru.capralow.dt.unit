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
package ru.capralow.dt.coverage.internal.ui;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

import ru.capralow.dt.coverage.core.ICoverageSession;

/**
 * Factory for <code>IWorkbenchAdapter</code>s for coverage model elements.
 */
public class WorkbenchAdapterFactory implements IAdapterFactory {

	private static final IWorkbenchAdapter SESSIONADAPTER = new IWorkbenchAdapter() {

		@Override
		public ImageDescriptor getImageDescriptor(Object object) {
			return CoverageUIPlugin.getImageDescriptor(CoverageUIPlugin.OBJ_SESSION);
		}

		@Override
		public String getLabel(Object o) {
			return ((ICoverageSession) o).getDescription();
		}

		@Override
		public Object[] getChildren(Object o) {
			return new Object[0];
		}

		@Override
		public Object getParent(Object o) {
			return null;
		}

	};

	@Override
	public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType) {
		if (adaptableObject instanceof ICoverageSession) {
			return SESSIONADAPTER;
		}
		return null;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList() {
		return new Class[] { IWorkbenchAdapter.class };
	}

}
