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
package ru.capralow.dt.coverage.internal.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

import ru.capralow.dt.coverage.core.CoverageTools;
import ru.capralow.dt.coverage.core.ICoverageSession;
import ru.capralow.dt.coverage.core.ISessionExporter;
import ru.capralow.dt.coverage.internal.ui.CoverageUIPlugin;
import ru.capralow.dt.coverage.internal.ui.UIMessages;

/**
 * The export wizard for coverage sessions.
 */
public class SessionExportWizard extends Wizard implements IExportWizard {

	public static final String ID = "ru.capralow.dt.coverage.ui.sessionExportWizard"; //$NON-NLS-1$

	private static final String SETTINGSID = "SessionExportWizard"; //$NON-NLS-1$

	private SessionExportPage1 page1;

	public SessionExportWizard() {
		super();
		IDialogSettings pluginsettings = CoverageUIPlugin.getInstance().getDialogSettings();
		IDialogSettings wizardsettings = pluginsettings.getSection(SETTINGSID);
		if (wizardsettings == null) {
			wizardsettings = pluginsettings.addNewSection(SETTINGSID);
		}
		setDialogSettings(wizardsettings);
		setWindowTitle(UIMessages.ExportSession_title);
		setDefaultPageImageDescriptor(CoverageUIPlugin.getImageDescriptor(CoverageUIPlugin.WIZBAN_EXPORT_SESSION));
		setNeedsProgressMonitor(true);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// Нечего делать
	}

	@Override
	public void addPages() {
		page1 = new SessionExportPage1();
		addPage(page1);
	}

	@Override
	public boolean performFinish() {
		page1.saveWidgetValues();
		return createReport();
	}

	private boolean createReport() {
		final ICoverageSession session = page1.getSelectedSession();
		final ISessionExporter exporter = CoverageTools.getExporter(session);
		exporter.setFormat(page1.getExportFormat());
		exporter.setDestination(page1.getDestination());
		final IRunnableWithProgress op = new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				try {
					exporter.export(monitor);
				} catch (Exception e) {
					throw new InvocationTargetException(e);
				}
			}
		};
		try {
			getContainer().run(true, true, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException ite) {
			final Throwable ex = ite.getTargetException();
			CoverageUIPlugin.log(ex);
			final String title = UIMessages.ExportSessionErrorDialog_title;
			String msg = UIMessages.ExportSessionErrorDialog_message;
			msg = NLS.bind(msg, session.getDescription());
			final IStatus status = CoverageUIPlugin.errorStatus(String.valueOf(ex.getMessage()), ex);
			ErrorDialog.openError(getShell(), title, msg, status);
			return false;
		}
		return true;
	}

}
