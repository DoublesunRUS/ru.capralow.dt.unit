/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import com._1c.g5.v8.dt.bm.index.emf.IBmEmfIndexManager;
import com._1c.g5.v8.dt.bm.index.emf.IBmEmfIndexProvider;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.ui.util.OpenHelper;
import com.google.inject.Inject;

import ru.capralow.dt.unit.junit.MdUtils;

/**
 * Abstract Action for opening a Java editor.
 */
public abstract class OpenEditorAction
    extends Action
{
    protected String fProjectAndModuleName;
    protected TestRunnerViewPart fTestRunner;
    private final boolean fActivate;

    @Inject
    private IBmEmfIndexManager bmEmfIndexManager;

    @Inject
    private IResourceLookup resourceLookup;

    @Inject
    private IV8ProjectManager projectManager;

    protected OpenEditorAction(TestRunnerViewPart testRunner, String testProjectAndModuleName)
    {
        this(testRunner, testProjectAndModuleName, true);
    }

    protected OpenEditorAction(TestRunnerViewPart testRunner, String projectAndModuleName, boolean activate)
    {
        super(Messages.OpenEditorAction_action_label);
        fProjectAndModuleName = projectAndModuleName;
        fTestRunner = testRunner;
        fActivate = activate;
    }

    /*
     * @see IAction#run()
     */
    @Override
    public void run()
    {
        int moduleNameIndex = fProjectAndModuleName.lastIndexOf('.');
        if (moduleNameIndex == -1)
        {
            return;
        }

        String projectName = fProjectAndModuleName.substring(0, moduleNameIndex);
        String moduleName = fProjectAndModuleName.substring(moduleNameIndex + 1);
        String methodName = ""; // TODO: Получить имя метода

        IV8Project v8Project = projectManager.getProject(projectName);
        if (v8Project == null)
        {
            return;
        }
        IProject project = v8Project.getProject();
        IBmEmfIndexProvider bmEmfIndexProvider = bmEmfIndexManager.getEmfIndexProvider(project);

        CommonModule testCommonModule = (CommonModule)MdUtils.getConfigurationObject("ОбщийМодуль." + moduleName, //$NON-NLS-1$
            bmEmfIndexProvider);
        if (testCommonModule == null)
        {
            return;
        }

        Method testMethod = null;
        for (Method method : testCommonModule.getModule().allMethods())
        {
            if (method.getName().equalsIgnoreCase(methodName))
            {
                testMethod = method;
                break;
            }
        }
        if (testMethod == null)
        {
            return;
        }

        URI uri = resourceLookup.getPlatformResourceUri(testCommonModule);

        ICompositeNode testNode = NodeModelUtils.findActualNodeFor(testMethod);

        TextSelection selection = new TextSelection(testNode.getOffset(), 0);

        OpenHelper openHelper = new OpenHelper();
        openHelper.openEditor(uri, selection);

        IEditorPart editor = null;
        if (!(editor instanceof ITextEditor))
        {
            fTestRunner.registerInfoMessage(Messages.OpenEditorAction_message_cannotopen);
            return;
        }
        reveal((ITextEditor)editor);
    }

    protected String getClassName()
    {
        return fProjectAndModuleName;
    }

    /**
     * @return the V8 project, or <code>null</code>
     */
    protected IV8Project getLaunchedProject()
    {
        return fTestRunner.getLaunchedProject();
    }

    protected Shell getShell()
    {
        return fTestRunner.getSite().getShell();
    }

    protected abstract void reveal(ITextEditor editor);

}
