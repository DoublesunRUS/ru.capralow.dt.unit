/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui.xtextbuilder;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.handlers.HandlerUtil;

import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com.google.inject.Inject;

/**
 * @author Aleksandr Kapralov
 *
 */
public class OpenFeaturesFolderHandler
    extends AbstractHandler
{

    @Inject
    private IV8ProjectManager projectManager;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        TreeSelection treeSelection = (TreeSelection)HandlerUtil.getCurrentSelection(event);

        Configuration configuration = (Configuration)treeSelection.getFirstElement();

        IProject project = projectManager.getProject(configuration).getProject();

        String featuresLocation = UnitLauncherXtextBuilderParticipant.getFeaturesLocation(project.getLocation());

        if (!(new File(featuresLocation).exists()))
        {
            MessageDialog.openInformation(null, Messages.OpenFeaturesDirectoryHandler_Unit_tests_caption,
                Messages.OpenFeaturesDirectoryHandler_Selected_project_doesnt_have_unit_tests);

            return null;
        }

        return Program.launch(featuresLocation);
    }

}
