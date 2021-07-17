/**
 * Copyright (c) 2020, Aleksandr Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.coverageview;

import org.eclipse.ui.model.WorkbenchContentProvider;

import ru.capralow.dt.coverage.analysis.IBslModelCoverage;

/**
 * Specialized workbench content provider that selects entry elements depending
 * on the view setting (projects, package roots, packages or types).
 */
class CoveredElementsContentProvider
    extends WorkbenchContentProvider
{

    private final ViewSettings settings;

    CoveredElementsContentProvider(ViewSettings settings)
    {
        this.settings = settings;
    }

    @Override
    public Object[] getElements(Object element)
    {
        IBslModelCoverage coverage = (IBslModelCoverage)element;
        if (coverage == IBslModelCoverage.LOADING)
            return new Object[] { CoverageView.LOADING_ELEMENT };

        if (coverage != null)
        {
            switch (settings.getRootType())
            {
            case GROUP:
                return coverage.getProjects();
            case BUNDLE:
                return coverage.getSubsystems();
            case CLASS:
                return coverage.getModules();
            case METHOD:
                break;
            case PACKAGE:
                break;
            case SOURCEFILE:
                break;
            default:
                break;
            }
        }
        return new Object[0];
    }

}
