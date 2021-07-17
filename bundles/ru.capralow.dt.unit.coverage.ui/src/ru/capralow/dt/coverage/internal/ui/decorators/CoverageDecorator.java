/**
 * Copyright (c) 2020, Alexander Kapralov
 */
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

import ru.capralow.dt.coverage.CoverageTools;
import ru.capralow.dt.coverage.analysis.IBslCoverageListener;
import ru.capralow.dt.coverage.internal.ui.CoverageUiPlugin;
import ru.capralow.dt.coverage.internal.ui.UiMessages;

/**
 * Decorator to show code coverage for Java elements.
 */
public class CoverageDecorator
    extends BaseLabelProvider
    implements ILightweightLabelDecorator
{

    private static final Format SUFFIX_FORMAT = new DecimalFormat(UiMessages.CoverageDecoratorSuffix_label);

    private final IBslCoverageListener coverageListener;

    public CoverageDecorator()
    {
        super();
        coverageListener = () -> {
            final Display display = CoverageUiPlugin.getInstance().getWorkbench().getDisplay();
            display.asyncExec(() -> fireLabelProviderChanged(new LabelProviderChangedEvent(CoverageDecorator.this)));
        };
        CoverageTools.addBslCoverageListener(coverageListener);
    }

    @Override
    public void decorate(Object element, IDecoration decoration)
    {
        final ICoverageNode coverage = CoverageTools.getCoverageInfo(element);
        if (coverage == null)
        {
            // no coverage data
            return;
        }
        // TODO obtain counter from preferences
        ICounter counter = coverage.getInstructionCounter();
        ImageDescriptor overlay = CoverageUiPlugin.getCoverageOverlay(counter.getCoveredRatio());
        decoration.addOverlay(overlay, IDecoration.TOP_LEFT);
        decoration.addSuffix(SUFFIX_FORMAT.format(Double.valueOf(counter.getCoveredRatio())));
    }

    @Override
    public void dispose()
    {
        CoverageTools.removeBslCoverageListener(coverageListener);
    }

    @Override
    public boolean isLabelProperty(Object element, String property)
    {
        // coverage does not depend on IJavaElement properties
        return false;
    }

}
