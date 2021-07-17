/**
 * Copyright (c) 2020, Aleksandr Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.editors;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import ru.capralow.dt.coverage.ICoverageSession;
import ru.capralow.dt.coverage.internal.ui.CoverageUiPlugin;

/**
 * Wrapper for a {@link ICoverageSession} instance to serve as an
 * {@link IEditorInput}.
 */
public class CoverageSessionInput
    extends PlatformObject
    implements IEditorInput
{

    private final ICoverageSession session;

    public CoverageSessionInput(ICoverageSession session)
    {
        this.session = session;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof CoverageSessionInput))
        {
            return false;
        }
        final CoverageSessionInput other = (CoverageSessionInput)obj;
        return session.equals(other.session);
    }

    @Override
    public boolean exists()
    {
        return false;
    }

    @Override
    public ImageDescriptor getImageDescriptor()
    {
        return CoverageUiPlugin.getImageDescriptor(CoverageUiPlugin.EVIEW_EXEC);
    }

    @Override
    public String getName()
    {
        return session.getDescription();
    }

    @Override
    public IPersistableElement getPersistable()
    {
        return null;
    }

    public ICoverageSession getSession()
    {
        return session;
    }

    @Override
    public String getToolTipText()
    {
        return session.getDescription();
    }

    @Override
    public int hashCode()
    {
        return session.hashCode();
    }

}
