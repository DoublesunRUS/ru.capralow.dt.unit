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
package ru.capralow.dt.coverage.internal.ui.coverageview;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ICoverageNode.ElementType;

import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;

import ru.capralow.dt.coverage.core.CoverageTools;
import ru.capralow.dt.coverage.internal.ui.CoverageUIPlugin;
import ru.capralow.dt.coverage.internal.ui.UIMessages;

/**
 * Internal converter to create textual representations for table cells.
 */
class CellTextConverter {

	private static final NumberFormat COVERAGE_VALUE = new DecimalFormat(UIMessages.CoverageView_columnCoverageValue);

	private static final NumberFormat COUNTER_VALUE = NumberFormat.getIntegerInstance();

	private final ViewSettings settings;
	private final ILabelProvider workbenchLabelProvider;

	private IV8ProjectManager v8ProjectManager;

	CellTextConverter(ViewSettings settings) {
		this.settings = settings;
		this.workbenchLabelProvider = new WorkbenchLabelProvider();
		this.v8ProjectManager = CoverageUIPlugin.getInstance().getInjector().getInstance(IV8ProjectManager.class);
	}

	private ICounter getCounter(Object element) {
		return CoverageTools.getCoverageInfo(element).getCounter(settings.getCounters());
	}

	private String getSimpleTextForModuleElement(URI element) {
		element.fragment();

		if (element instanceof Module) {
			final Module root = (Module) element;

			return ((CommonModule) root.getOwner()).getName();

		}

		return workbenchLabelProvider.getText(element);
	}

	String getCovered(Object element) {
		return COUNTER_VALUE.format(getCounter(element).getCoveredCount());
	}

	String getElementName(Object element) {
		String text = getSimpleTextForModuleElement((URI) element);
		if (element instanceof Module && ElementType.BUNDLE.equals(settings.getRootType())) {
			IV8Project project = v8ProjectManager.getProject((EObject) element);
			text += " - " //$NON-NLS-1$
					+ getElementName(project.getProject().getName());
		}
		return text;
	}

	String getMissed(Object element) {
		return COUNTER_VALUE.format(getCounter(element).getMissedCount());
	}

	String getRatio(Object element) {
		ICounter counter = getCounter(element);
		if (counter.getTotalCount() == 0)
			return ""; //$NON-NLS-1$

		return COVERAGE_VALUE.format(counter.getCoveredRatio());
	}

	String getTotal(Object element) {
		return COUNTER_VALUE.format(getCounter(element).getTotalCount());
	}

}
