/*******************************************************************************
 * Copyright (c) 2006, 2019 Mountainminds GmbH & Co. KG and Contributors
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Benjamin Muskalla - initial API and implementation
 *
 ******************************************************************************/
package ru.capralow.dt.coverage.internal.ui.decorators;

import java.text.DecimalFormat;
import java.text.Format;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.widgets.Display;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ICoverageNode;

import ru.capralow.dt.coverage.core.CoverageTools;
import ru.capralow.dt.coverage.core.analysis.IBslCoverageListener;
import ru.capralow.dt.coverage.internal.ui.CoverageUIPlugin;
import ru.capralow.dt.coverage.internal.ui.UIMessages;

/**
 * Decorator to show code coverage for Java elements.
 */
public class CoverageDecorator extends BaseLabelProvider implements ILightweightLabelDecorator {

	private static final Format SUFFIX_FORMAT = new DecimalFormat(UIMessages.CoverageDecoratorSuffix_label);

	private final IBslCoverageListener coverageListener;

	public CoverageDecorator() {
		super();
		coverageListener = new IBslCoverageListener() {
			public void coverageChanged() {
				final Display display = CoverageUIPlugin.getInstance().getWorkbench().getDisplay();
				display.asyncExec(new Runnable() {
					public void run() {
						fireLabelProviderChanged(new LabelProviderChangedEvent(CoverageDecorator.this));
					}
				});
			}
		};
		CoverageTools.addJavaCoverageListener(coverageListener);
	}

	public void decorate(Object element, IDecoration decoration) {
		final ICoverageNode coverage = CoverageTools.getCoverageInfo(element);
		if (coverage == null) {
			// no coverage data
			return;
		}
		// TODO obtain counter from preferences
		ICounter counter = coverage.getInstructionCounter();
		ImageDescriptor overlay = CoverageUIPlugin.getCoverageOverlay(counter.getCoveredRatio());
		decoration.addOverlay(overlay, IDecoration.TOP_LEFT);
		decoration.addSuffix(SUFFIX_FORMAT.format(Double.valueOf(counter.getCoveredRatio())));
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// coverage does not depend on IJavaElement properties
		return false;
	}

	@Override
	public void dispose() {
		CoverageTools.removeJavaCoverageListener(coverageListener);
	}

}
