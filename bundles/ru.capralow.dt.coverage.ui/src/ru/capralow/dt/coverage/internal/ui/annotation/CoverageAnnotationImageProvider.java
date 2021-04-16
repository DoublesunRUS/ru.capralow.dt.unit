/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.annotation;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.texteditor.IAnnotationImageProvider;
import org.jacoco.core.analysis.ICounter;

import ru.capralow.dt.coverage.internal.ui.CoverageUiPlugin;

/**
 * The annotation image is calculated dynamically as it depends on the branch
 * coverage status.
 */
public class CoverageAnnotationImageProvider
    implements IAnnotationImageProvider
{

    @Override
    public ImageDescriptor getImageDescriptor(String imageDescritporId)
    {
        return CoverageUiPlugin.getImageDescriptor(imageDescritporId);
    }

    @Override
    public String getImageDescriptorId(Annotation annotation)
    {
        if (annotation instanceof CoverageAnnotation)
        {
            final ICounter branches = ((CoverageAnnotation)annotation).getLine().getBranchCounter();
            switch (branches.getStatus())
            {
            case ICounter.FULLY_COVERED:
                return CoverageUiPlugin.OBJ_MARKERFULL;
            case ICounter.PARTLY_COVERED:
                return CoverageUiPlugin.OBJ_MARKERPARTIAL;
            case ICounter.NOT_COVERED:
                return CoverageUiPlugin.OBJ_MARKERNO;
            default:
                return null;
            }
        }
        return null;
    }

    @Override
    public Image getManagedImage(Annotation annotation)
    {
        // we don't manage images ourself
        return null;
    }

}
