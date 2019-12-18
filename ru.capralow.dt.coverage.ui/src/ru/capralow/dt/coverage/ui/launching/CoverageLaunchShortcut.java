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
package ru.capralow.dt.coverage.ui.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;

import ru.capralow.dt.coverage.core.CoverageTools;
import ru.capralow.dt.coverage.internal.ui.CoverageUIPlugin;

/**
 * Generic ILaunchShortcut implementation that delegates to another
 * ILaunchShortcut with a given id. The id is specified via the executable
 * extension attribute "class":
 *
 * <pre>
 *   class="ru.capralow.dt.coverage.internal.ui.launching.CoverageLaunchShortcut:org.eclipse.jdt.debug.ui.localJavaShortcut"
 * </pre>
 */
public class CoverageLaunchShortcut implements ILaunchShortcut, IExecutableExtension {

	private String delegateId;
	private ILaunchShortcut delegate;

	private ILaunchShortcut getDelegate() {
		if (delegate == null) {
			IExtensionPoint extensionPoint = Platform.getExtensionRegistry()
					.getExtensionPoint(IDebugUIConstants.PLUGIN_ID, IDebugUIConstants.EXTENSION_POINT_LAUNCH_SHORTCUTS);
			for (final IConfigurationElement config : extensionPoint.getConfigurationElements()) {
				if (delegateId.equals(config.getAttribute("id"))) { //$NON-NLS-1$
					try {
						delegate = (ILaunchShortcut) config.createExecutableExtension("class"); //$NON-NLS-1$
					} catch (CoreException e) {
						CoverageUIPlugin.log(e);
					}
					break;
				}
			}
			if (delegate == null) {
				String msg = "ILaunchShortcut declaration not found: " + delegateId; //$NON-NLS-1$
				CoverageUIPlugin.getInstance().getLog().log(CoverageUIPlugin.errorStatus(msg, null));
			}
		}
		return delegate;
	}

	// IExecutableExtension interface:

	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException {
		delegateId = String.valueOf(data);
	}

	// ILaunchShortcut interface:

	public void launch(ISelection selection, String mode) {
		ILaunchShortcut launchDelegate = getDelegate();
		if (launchDelegate != null) {
			launchDelegate.launch(selection, CoverageTools.LAUNCH_MODE);
		}
	}

	public void launch(IEditorPart editor, String mode) {
		ILaunchShortcut launchDelegate = getDelegate();
		if (launchDelegate != null) {
			launchDelegate.launch(editor, CoverageTools.LAUNCH_MODE);
		}
	}

}
