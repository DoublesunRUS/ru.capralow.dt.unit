/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal.analysis;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.analysis.ISourceNode;

import ru.capralow.dt.coverage.CoverageTools;
import ru.capralow.dt.coverage.analysis.IBslModelCoverage;

/**
 * This factory adapts IResource and Method objects to the corresponding
 * coverage information of the current session. The factory is hooked into the
 * workbench through the extension point
 * <code>org.eclipse.core.runtime.adapters</code>.
 */
public class ModuleCoverageAdapterFactory
    implements IAdapterFactory
{

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object getAdapter(Object object, Class adapterType)
    {
        IBslModelCoverage mc = CoverageTools.getBslModelCoverage();
        if (mc == null)
            return null;

        ICoverageNode coverage = mc.getCoverageFor((URI)object);

        if (coverage == null)
            return null;

        if (adapterType.isInstance(coverage))
            return coverage;

        return null;
    }

    @Override
    public Class<?>[] getAdapterList()
    {
        return new Class[] { ICoverageNode.class, ISourceNode.class };
    }

}
