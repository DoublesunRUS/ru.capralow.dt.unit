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

import java.text.DecimalFormat;

import org.eclipse.swt.widgets.Event;
import org.jacoco.core.analysis.ICounter;

/**
 * Utility methods to draw red/green bars into table cells.
 */
public final class RedGreenBar {

	private static final int BORDER_LEFT = 2;
	private static final int BORDER_RIGHT = 10;
	private static final int BORDER_TOP = 3;
	private static final int BORDER_BOTTOM = 4;

	private static final String MAX_PERCENTAGE_STRING = new DecimalFormat(UIMessages.CoverageView_columnCoverageValue)
			.format(1.0);

	private RedGreenBar() {
	}

	public static void draw(Event event, int columnWith, ICounter counter) {
		draw(event, columnWith, counter, counter.getTotalCount());
	}

	public static void draw(Event event, int columnWith, ICounter counter, int maxTotal) {
		if (maxTotal == 0) {
			return;
		}
		final int maxWidth = getMaxWidth(event, columnWith);
		final int redLength = maxWidth * counter.getMissedCount() / maxTotal;
		bar(event, CoverageUIPlugin.DGM_REDBAR, 0, redLength);
		final int greenLength = maxWidth * counter.getCoveredCount() / maxTotal;
		bar(event, CoverageUIPlugin.DGM_GREENBAR, redLength, greenLength);
	}

	private static void bar(Event event, String image, int xOffset, int width) {
		final int height = event.getBounds().height - BORDER_TOP - BORDER_BOTTOM;
		event.gc.drawImage(CoverageUIPlugin
				.getImage(image), 0, 0, 1, 10, event.x + xOffset + BORDER_LEFT, event.y + BORDER_TOP, width, height);
	}

	private static int getMaxWidth(Event event, int columnWith) {
		final int textWidth = event.gc.textExtent(MAX_PERCENTAGE_STRING).x;
		final int max = columnWith - BORDER_LEFT - BORDER_RIGHT - textWidth;
		return Math.max(0, max);
	}

}
