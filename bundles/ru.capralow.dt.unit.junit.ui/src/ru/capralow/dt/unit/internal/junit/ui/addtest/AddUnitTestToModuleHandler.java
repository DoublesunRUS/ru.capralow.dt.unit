/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui.addtest;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.osgi.framework.Bundle;

import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.ui.menu.BslHandlerUtil;
import com._1c.g5.v8.dt.core.platform.IConfigurationProject;
import com._1c.g5.v8.dt.core.platform.IExtensionProject;
import com._1c.g5.v8.dt.core.platform.IExternalObjectProject;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.lcore.nodemodel.util.CustomNodeModelUtils;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com.google.common.io.CharSource;
import com.google.common.io.CharStreams;
import com.google.common.io.Resources;
import com.google.inject.Inject;

import ru.capralow.dt.unit.internal.junit.ui.JUnitUiPlugin;

/**
 * @author Aleksandr Kapralov
 *
 */
public class AddUnitTestToModuleHandler
    extends AbstractHandler
{

    private static Configuration getConfigurationFromProject(IV8Project v8Project)
    {
        Configuration configuration = null;
        if (v8Project instanceof IConfigurationProject)
        {
            configuration = ((IConfigurationProject)v8Project).getConfiguration();

        }
        else if (v8Project instanceof IExtensionProject)
        {
            configuration = ((IExtensionProject)v8Project).getConfiguration();

        }
        else if (v8Project instanceof IExternalObjectProject)
        {
            IConfigurationProject parent = ((IExternalObjectProject)v8Project).getParent();
            if (parent == null)
            {
                return null;
            }
            configuration = parent.getConfiguration();
        }

        return configuration;
    }

    private static CharSource getFileInputSupplier(String partName, String bundleName)
    {
        Bundle bundle = Platform.getBundle(bundleName);

        return Resources.asCharSource(bundle.getResource(partName), StandardCharsets.UTF_8);
    }

    private static String readContents(CharSource source)
    {
        try (Reader reader = source.openBufferedStream())
        {
            return CharStreams.toString(reader);

        }
        catch (IOException | NullPointerException e)
        {
            return ""; //$NON-NLS-1$

        }
    }

    @Inject
    private IV8ProjectManager projectManager;

    @Override
    public final Object execute(ExecutionEvent event) throws ExecutionException
    {
        IWorkbenchPart part = HandlerUtil.getActivePart(event);
        XtextEditor target = BslHandlerUtil.extractXtextEditor(part);
        if (target != null)
        {
            this.execute(target);
        }

        return null;
    }

    /**
     * @param target
     */
    public void execute(XtextEditor target)
    {
        Shell shell = target.getShell();
        IFileEditorInput input = (IFileEditorInput)target.getEditorInput();
        IFile file = input.getFile();
        if (file == null)
        {
            return;
        }
        IProject project = file.getProject();
        if (project == null)
        {
            return;
        }

        IXtextDocument doc = target.getDocument();

        EObject moduleOwner = getModuleOwner(doc);
        if (!(moduleOwner instanceof CommonModule))
        {
            showErrorMessage(shell);
            return;
        }

        IV8Project v8Project = projectManager.getProject(project);

        Configuration configuration = getConfigurationFromProject(v8Project);

        if (configuration == null)
        {
            String msg =
                MessageFormat.format(Messages.AddUnitTest_Unable_to_get_configuration_from_base_project_0, v8Project);
            JUnitUiPlugin.log(JUnitUiPlugin.createErrorStatus(msg));
            return;
        }
        String lang = configuration.getScriptVariant().equals(ScriptVariant.RUSSIAN) ? "Ru" : "En"; //$NON-NLS-1$ //$NON-NLS-2$

        String testMethodContent =
            readContents(getFileInputSupplier("/resources/unitTestMethod" + lang + ".txt", JUnitUiPlugin.ID)); //$NON-NLS-1$ //$NON-NLS-2$

        ITextViewer viewer = BslHandlerUtil.getTextViewer(target);

        try
        {
            int insertPosition = getInsertHandlerPosition(doc, viewer);
            doc.replace(insertPosition, 0, testMethodContent);

        }
        catch (BadLocationException e)
        {
            JUnitUiPlugin.log(JUnitUiPlugin.createErrorStatus(e.getMessage(), e));

        }
    }

    private int getInsertHandlerPosition(IXtextDocument doc, ITextViewer viewer)
    {
        return doc.readOnly(new IUnitOfWork<Integer, XtextResource>()
        {
            @Override
            public Integer exec(XtextResource res) throws Exception
            {
                if (res.getContents() != null && !res.getContents().isEmpty())
                {
                    EObject obj = res.getContents().get(0);
                    if (obj instanceof Module)
                    {
                        Module module = (Module)obj;

                        Method method = getNearestMethod(module, viewer.getSelectedRange().x);
                        if (method != null)
                        {
                            return NodeModelUtils.findActualNodeFor(method).getTotalEndOffset();
                        }
                    }
                }
                return viewer.getSelectedRange().x;
            }
        });
    }

    private EObject getModuleOwner(IXtextDocument doc)
    {
        return doc.readOnly(new IUnitOfWork<EObject, XtextResource>()
        {
            @Override
            public EObject exec(XtextResource res) throws Exception
            {
                if (res.getContents() != null && !res.getContents().isEmpty())
                {
                    EObject obj = res.getContents().get(0);
                    if (obj instanceof Module)
                    {
                        if (((Module)obj).getModuleType() != ModuleType.COMMON_MODULE)
                        {
                            return null;
                        }
                        Module module = (Module)obj;
                        return EcoreUtil.resolve(module.getOwner(), module);
                    }
                }
                return null;
            }
        });
    }

    private Method getNearestMethod(Module module, int offset)
    {
        ICompositeNode moduleNode = NodeModelUtils.findActualNodeFor(module);
        ILeafNode node = CustomNodeModelUtils.findLeafNodeAtOffset(moduleNode, offset);
        EObject actualObject = NodeModelUtils.findActualSemanticObjectFor(node);
        if (actualObject instanceof Method)
        {
            return (Method)actualObject;
        }

        return EcoreUtil2.getContainerOfType(actualObject, Method.class);
    }

    private void showErrorMessage(Shell shell)
    {
        MessageBox messageBox = new MessageBox(shell, SWT.OK | SWT.ERROR);
        messageBox.setMessage(Messages.AddUnitTest_Error_message);
        messageBox.setText(Messages.AddUnitTest_Error_caption);
        messageBox.open();
    }
}
