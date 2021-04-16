/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.annotation;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.osgi.util.NLS;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ILine;

import ru.capralow.dt.coverage.internal.ui.UiMessages;

/**
 * Annotation object that includes its position information to avoid internal
 * mappings.
 */
public class CoverageAnnotation
    extends Annotation
{

    private static final String FULL_COVERAGE = "ru.capralow.dt.coverage.ui.fullCoverageAnnotation"; //$NON-NLS-1$
    private static final String PARTIAL_COVERAGE = "ru.capralow.dt.coverage.ui.partialCoverageAnnotation"; //$NON-NLS-1$
    private static final String NO_COVERAGE = "ru.capralow.dt.coverage.ui.noCoverageAnnotation"; //$NON-NLS-1$

    private static String getAnnotationID(ILine line)
    {
        switch (line.getStatus())
        {
        case ICounter.FULLY_COVERED:
            return FULL_COVERAGE;
        case ICounter.PARTLY_COVERED:
            return PARTIAL_COVERAGE;
        case ICounter.NOT_COVERED:
            return NO_COVERAGE;
        default:
            throw new AssertionError(line.getStatus());
        }
    }

    private final Position position;

    private final ILine line;

    public CoverageAnnotation(int offset, int length, ILine line)
    {
        super(getAnnotationID(line), false, null);
        this.line = line;
        position = new Position(offset, length);
    }

    public ILine getLine()
    {
        return line;
    }

    public Position getPosition()
    {
        return position;
    }

    @Override
    public String getText()
    {
        final ICounter branches = line.getBranchCounter();
        switch (branches.getStatus())
        {
        case ICounter.NOT_COVERED:
            return NLS.bind(UiMessages.AnnotationTextAllBranchesMissed_message,
                Integer.valueOf(branches.getMissedCount()));
        case ICounter.FULLY_COVERED:
            return NLS.bind(UiMessages.AnnotationTextAllBranchesCovered_message,
                Integer.valueOf(branches.getTotalCount()));
        case ICounter.PARTLY_COVERED:
            return NLS.bind(UiMessages.AnnotationTextSomeBranchesMissed_message,
                Integer.valueOf(branches.getMissedCount()), Integer.valueOf(branches.getTotalCount()));
        default:
            return null;
        }
    }

}
