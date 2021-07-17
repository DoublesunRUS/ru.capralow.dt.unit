/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.coverageview;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ICoverageNode.ElementType;

import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;

import ru.capralow.dt.coverage.CoverageTools;
import ru.capralow.dt.coverage.MdUtils;
import ru.capralow.dt.coverage.internal.ui.CoverageUiPlugin;
import ru.capralow.dt.coverage.internal.ui.UiMessages;

/**
 * Internal converter to create textual representations for table cells.
 */
class CellTextConverter
{

    private static final NumberFormat COVERAGE_VALUE = new DecimalFormat(UiMessages.CoverageView_columnCoverageValue);

    private static final NumberFormat COUNTER_VALUE = NumberFormat.getIntegerInstance();

    private final ViewSettings settings;
    private final ILabelProvider workbenchLabelProvider;

    private IV8ProjectManager projectManager;

    CellTextConverter(ViewSettings settings)
    {
        this.settings = settings;
        this.workbenchLabelProvider = new WorkbenchLabelProvider();
        this.projectManager = CoverageUiPlugin.getInstance().getInjector().getInstance(IV8ProjectManager.class);
    }

    private ICounter getCounter(Object element)
    {
        return CoverageTools.getCoverageInfo(element).getCounter(settings.getCounters());
    }

    private String getSimpleTextForModuleElement(URI element)
    {
        IV8Project v8Project = projectManager.getProject(element);
        if (v8Project == null)
            return workbenchLabelProvider.getText(element);

        EObject eObject = MdUtils.getEObjectByUri(element);

        if (eObject instanceof Configuration)
            return ((Configuration)eObject).getName();

        else if (eObject instanceof Module)
            return ((CommonModule)((Module)eObject).getOwner()).getName();

        return v8Project.getProject().getName();
    }

    String getCovered(Object element)
    {
        return COUNTER_VALUE.format(getCounter(element).getCoveredCount());
    }

    String getElementName(Object element)
    {
        String text = getSimpleTextForModuleElement((URI)element);
        if (element instanceof Module && ElementType.BUNDLE.equals(settings.getRootType()))
        {
            IV8Project project = projectManager.getProject((EObject)element);
            text += " - " //$NON-NLS-1$
                + getElementName(project.getProject().getName());
        }
        return text;
    }

    String getMissed(Object element)
    {
        return COUNTER_VALUE.format(getCounter(element).getMissedCount());
    }

    String getRatio(Object element)
    {
        ICounter counter = getCounter(element);
        if (counter.getTotalCount() == 0)
            return ""; //$NON-NLS-1$

        return COVERAGE_VALUE.format(counter.getCoveredRatio());
    }

    String getTotal(Object element)
    {
        return COUNTER_VALUE.format(getCounter(element).getTotalCount());
    }

}
