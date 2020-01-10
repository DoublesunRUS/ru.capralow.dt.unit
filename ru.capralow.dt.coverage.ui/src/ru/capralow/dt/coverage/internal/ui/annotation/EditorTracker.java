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
package ru.capralow.dt.coverage.internal.ui.annotation;

import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Tracks the workbench editors and to attach coverage annotation models.
 */
public class EditorTracker {

	private final IWorkbench workbench;

	private IWindowListener windowListener = new IWindowListener() {
		@Override
		public void windowOpened(IWorkbenchWindow window) {
			window.getPartService().addPartListener(partListener);
		}

		@Override
		public void windowClosed(IWorkbenchWindow window) {
			window.getPartService().removePartListener(partListener);
		}

		@Override
		public void windowActivated(IWorkbenchWindow window) {
			// Нечего делать
		}

		@Override
		public void windowDeactivated(IWorkbenchWindow window) {
			// Нечего делать
		}
	};

	private IPartListener2 partListener = new IPartListener2() {
		@Override
		public void partOpened(IWorkbenchPartReference partref) {
			annotateEditor(partref);
		}

		@Override
		public void partActivated(IWorkbenchPartReference partref) {
			// Нечего делать
		}

		@Override
		public void partBroughtToTop(IWorkbenchPartReference partref) {
			// Нечего делать
		}

		@Override
		public void partVisible(IWorkbenchPartReference partref) {
			// Нечего делать
		}

		@Override
		public void partInputChanged(IWorkbenchPartReference partref) {
			// Нечего делать
		}

		@Override
		public void partClosed(IWorkbenchPartReference partref) {
			// Нечего делать
		}

		@Override
		public void partDeactivated(IWorkbenchPartReference partref) {
			// Нечего делать
		}

		@Override
		public void partHidden(IWorkbenchPartReference partref) {
			// Нечего делать
		}
	};

	public EditorTracker(IWorkbench workbench) {
		this.workbench = workbench;
		for (final IWorkbenchWindow w : workbench.getWorkbenchWindows()) {
			w.getPartService().addPartListener(partListener);
		}
		workbench.addWindowListener(windowListener);
		annotateAllEditors();
	}

	public void dispose() {
		workbench.removeWindowListener(windowListener);
		for (final IWorkbenchWindow w : workbench.getWorkbenchWindows()) {
			w.getPartService().removePartListener(partListener);
		}
	}

	private void annotateAllEditors() {
		for (final IWorkbenchWindow w : workbench.getWorkbenchWindows()) {
			for (final IWorkbenchPage p : w.getPages()) {
				for (final IEditorReference e : p.getEditorReferences()) {
					annotateEditor(e);
				}
			}
		}
	}

	private static void annotateEditor(IWorkbenchPartReference partref) {
		IWorkbenchPart part = partref.getPart(false);
		if (part instanceof ITextEditor) {
			CoverageAnnotationModel.attach((ITextEditor) part);
		}
	}

}
