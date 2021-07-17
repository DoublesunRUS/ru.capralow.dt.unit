/**
 * Copyright (c) 2020, Aleksandr Kapralov
 */
package ru.capralow.dt.coverage.internal.ui;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

import ru.capralow.dt.coverage.ICoverageSession;

/**
 * Factory for <code>IWorkbenchAdapter</code>s for coverage model elements.
 */
public class WorkbenchAdapterFactory
    implements IAdapterFactory
{

    private static final IWorkbenchAdapter SESSIONADAPTER = new IWorkbenchAdapter()
    {

        @Override
        public Object[] getChildren(Object o)
        {
            return new Object[0];
        }

        @Override
        public ImageDescriptor getImageDescriptor(Object object)
        {
            return CoverageUiPlugin.getImageDescriptor(CoverageUiPlugin.OBJ_SESSION);
        }

        @Override
        public String getLabel(Object o)
        {
            return ((ICoverageSession)o).getDescription();
        }

        @Override
        public Object getParent(Object o)
        {
            return null;
        }

    };

    @Override
    public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType)
    {
        if (adaptableObject instanceof ICoverageSession)
        {
            return SESSIONADAPTER;
        }
        return null;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Class[] getAdapterList()
    {
        return new Class[] { IWorkbenchAdapter.class };
    }

}
