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

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import ru.capralow.dt.coverage.core.ICoverageSession;
import ru.capralow.dt.coverage.internal.ui.CoverageUIPlugin;

/**
 * Wrapper for a {@link ICoverageSession} instance to serve as an
 * {@link IEditorInput}.
 */
public class CoverageSessionInput extends PlatformObject implements IEditorInput {

	private final ICoverageSession session;

	public CoverageSessionInput(ICoverageSession session) {
		this.session = session;
	}

	public ICoverageSession getSession() {
		return session;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return CoverageUIPlugin.getImageDescriptor(CoverageUIPlugin.EVIEW_EXEC);
	}

	@Override
	public String getName() {
		return session.getDescription();
	}

	@Override
	public String getToolTipText() {
		return session.getDescription();
	}

	@Override
	public int hashCode() {
		return session.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CoverageSessionInput)) {
			return false;
		}
		final CoverageSessionInput other = (CoverageSessionInput) obj;
		return session.equals(other.session);
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

}
