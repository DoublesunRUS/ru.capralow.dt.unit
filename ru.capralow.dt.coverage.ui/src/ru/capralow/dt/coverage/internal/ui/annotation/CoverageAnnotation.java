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

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.osgi.util.NLS;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ILine;

import ru.capralow.dt.coverage.internal.ui.UIMessages;

/**
 * Annotation object that includes its position information to avoid internal
 * mappings.
 */
public class CoverageAnnotation extends Annotation {

	private static final String FULL_COVERAGE = "ru.capralow.dt.coverage.ui.fullCoverageAnnotation"; //$NON-NLS-1$
	private static final String PARTIAL_COVERAGE = "ru.capralow.dt.coverage.ui.partialCoverageAnnotation"; //$NON-NLS-1$
	private static final String NO_COVERAGE = "ru.capralow.dt.coverage.ui.noCoverageAnnotation"; //$NON-NLS-1$

	private final Position position;
	private final ILine line;

	public CoverageAnnotation(int offset, int length, ILine line) {
		super(getAnnotationID(line), false, null);
		this.line = line;
		position = new Position(offset, length);
	}

	public Position getPosition() {
		return position;
	}

	public ILine getLine() {
		return line;
	}

	@Override
	public String getText() {
		final ICounter branches = line.getBranchCounter();
		switch (branches.getStatus()) {
		case ICounter.NOT_COVERED:
			return NLS.bind(UIMessages.AnnotationTextAllBranchesMissed_message,
					Integer.valueOf(branches.getMissedCount()));
		case ICounter.FULLY_COVERED:
			return NLS.bind(UIMessages.AnnotationTextAllBranchesCovered_message,
					Integer.valueOf(branches.getTotalCount()));
		case ICounter.PARTLY_COVERED:
			return NLS.bind(UIMessages.AnnotationTextSomeBranchesMissed_message,
					Integer.valueOf(branches.getMissedCount()),
					Integer.valueOf(branches.getTotalCount()));
		default:
			return null;
		}
	}

	private static String getAnnotationID(ILine line) {
		switch (line.getStatus()) {
		case ICounter.FULLY_COVERED:
			return FULL_COVERAGE;
		case ICounter.PARTLY_COVERED:
			return PARTIAL_COVERAGE;
		case ICounter.NOT_COVERED:
			return NO_COVERAGE;
		}
		throw new AssertionError(line.getStatus());
	}

}
