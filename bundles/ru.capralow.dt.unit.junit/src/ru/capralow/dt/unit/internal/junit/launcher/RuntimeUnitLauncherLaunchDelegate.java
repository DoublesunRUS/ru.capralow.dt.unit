/**
 * Copyright (c) 2021, Alexander Kapralov
 */
package ru.capralow.dt.unit.internal.junit.launcher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

import org.antlr.stringtemplate.StringTemplate;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.launching.core.ILaunchConfigurationAttributes;
import com._1c.g5.v8.dt.platform.services.core.runtimes.execution.RuntimeExecutionArguments;
import com._1c.g5.v8.dt.platform.services.model.InfobaseReference;
import com.google.common.base.Strings;
import com.google.common.io.CharSource;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.inject.Inject;

import ru.capralow.dt.unit.internal.junit.JUnitPlugin;
import ru.capralow.dt.unit.junit.frameworks.FrameworkUtils;

public class RuntimeUnitLauncherLaunchDelegate
    extends AbstractUnitTestLaunchDelegate
{

    private static CharSource getFileInputSupplier(URL resourceUrl)
    {
        return Resources.asCharSource(resourceUrl, StandardCharsets.UTF_8);
    }

    private static String readContents(CharSource source)
    {
        try (Reader reader = source.openBufferedStream())
        {
            return CharStreams.toString(reader);

        }
        catch (IOException | NullPointerException e)
        {
            JUnitPlugin.log(JUnitPlugin.createErrorStatus(e.getMessage(), e));
            return ""; //$NON-NLS-1$

        }
    }

    private static boolean saveFrameworkToFile(ILaunchConfiguration configuration)
    {
        var bundle = FrameworkUtils.getFrameworkBundle();
        try
        {
            var frameworkSettings = FrameworkUtils.getFrameworkSettings();
            String frameworkEpfName = FrameworkUtils.getFrameworkEpfName(frameworkSettings);

            var frameworkParamsBundleUrl =
                FileLocator.find(bundle, new Path(FrameworkUtils.FRAMEWORK_FILES_ROOT_PATH + frameworkEpfName), null);
            var frameworkParamsUrl = FileLocator.toFileURL(frameworkParamsBundleUrl);

            if (frameworkParamsUrl == null)
            {
                String msg = MessageFormat.format(
                    Messages.RuntimeUnitLauncherLaunchDelegate_Failed_to_get_framework_from_bundle_0_1,
                    bundle.getSymbolicName(), frameworkEpfName);
                JUnitPlugin.log(JUnitPlugin.createErrorStatus(msg, new IOException()));
                return false;
            }
            var file = URIUtil.toFile(URIUtil.toURI(frameworkParamsUrl));

            if (!file.exists())
            {
                String msg = MessageFormat.format(Messages.RuntimeUnitLauncherLaunchDelegate_Failed_to_read_framework_0,
                    file.toString());
                JUnitPlugin.log(JUnitPlugin.createErrorStatus(msg, new IOException()));
                return false;
            }

            String frameworkFilePathName = FrameworkUtils.getConfigurationFilesPath(configuration);
            Files.copy(file, new File(frameworkFilePathName + FrameworkUtils.FRAMEWORK_FILE_NAME));

        }
        catch (IOException | URISyntaxException e)
        {
            String msg = MessageFormat.format(Messages.RuntimeUnitLauncherLaunchDelegate_Failed_to_save_framework_0,
                FrameworkUtils.FRAMEWORK_FILE_NAME);
            JUnitPlugin.log(JUnitPlugin.createErrorStatus(msg, e));
            return false;

        }

        return true;
    }

    @Inject
    private IV8ProjectManager projectManager;

    @Override
    public void doLaunch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
        throws CoreException
    {

        if (!saveParamsToFile(configuration))
            return;

        if (!saveFrameworkToFile(configuration))
            return;

        super.doLaunch(configuration, mode, launch, monitor);
    }

    private String getFeaturesPath(ILaunchConfiguration configuration)
    {
        var featuresPath = ""; //$NON-NLS-1$
        try
        {
            boolean runExtensionTests =
                configuration.getAttribute(JUnitLaunchConfigurationConstants.RUN_EXTENSION_TESTS, false);
            boolean runModuleTests =
                configuration.getAttribute(JUnitLaunchConfigurationConstants.RUN_MODULE_TESTS, false);
            boolean runTagTests = configuration.getAttribute(JUnitLaunchConfigurationConstants.RUN_TAG_TESTS, false);
            IProject project = FrameworkUtils.getConfigurationProject(configuration, projectManager);
            String commonModule = FrameworkUtils.getModuleFromConfiguration(configuration, projectManager);
            String tag = FrameworkUtils.getTagFromConfiguration(configuration, projectManager);

            featuresPath = project.getLocation() + "/features/"; //$NON-NLS-1$
            if (runExtensionTests)
                featuresPath += "all/"; //$NON-NLS-1$
            else if (runModuleTests)
                featuresPath += "all/" + commonModule + ".feature"; //$NON-NLS-1$ //$NON-NLS-2$
            else if (runTagTests)
                featuresPath += tag + "/"; //$NON-NLS-1$

        }
        catch (CoreException e)
        {
            JUnitPlugin.log(JUnitPlugin
                .createErrorStatus(Messages.RuntimeUnitLauncherLaunchDelegate_Incorrect_launch_configuration, e));

        }

        return featuresPath;
    }

    private String getProjectPath(ILaunchConfiguration configuration)
    {
        var projectPath = ""; //$NON-NLS-1$
        try
        {
            IProject project = FrameworkUtils.getConfigurationProject(configuration, projectManager);

            projectPath = project.getLocation() + File.pathSeparator;

        }
        catch (CoreException e)
        {
            JUnitPlugin.log(JUnitPlugin
                .createErrorStatus(Messages.RuntimeUnitLauncherLaunchDelegate_Incorrect_launch_configuration, e));

        }

        return projectPath;
    }

    private void parseParamsTemplate(URL frameworkParamsUrl, ILaunchConfiguration configuration) throws IOException
    {
        String paramsFilePathName = FrameworkUtils.getConfigurationFilesPath(configuration);
        String projectPath = getProjectPath(configuration);
        String featuresPath = getFeaturesPath(configuration);

        String templateContent = readContents(getFileInputSupplier(frameworkParamsUrl));
        var template = new StringTemplate(templateContent);
        template.setAttribute("ProjectPath", projectPath); //$NON-NLS-1$
        template.setAttribute("FeaturesPath", featuresPath); //$NON-NLS-1$
        template.setAttribute("JUnitPath", paramsFilePathName); //$NON-NLS-1$

        var paramsFilePath = new File(paramsFilePathName);
        if (!paramsFilePath.exists())
            paramsFilePath.mkdirs();

        try (var outputStream = new FileOutputStream(paramsFilePathName + FrameworkUtils.PARAMS_FILE_NAME);
            var outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            var bufferedWriter = new BufferedWriter(outputStreamWriter);)

        {
            bufferedWriter.write(template.toString());

        }

    }

    private boolean saveParamsToFile(ILaunchConfiguration configuration)
    {
        var bundle = FrameworkUtils.getFrameworkBundle();
        try
        {
            var frameworkParamsBundleUrl = FileLocator.find(bundle,
                new Path(FrameworkUtils.FRAMEWORK_FILES_ROOT_PATH + FrameworkUtils.PARAMS_FILE_NAME), null);
            var frameworkParamsUrl = FileLocator.toFileURL(frameworkParamsBundleUrl);

            if (frameworkParamsUrl == null)
            {
                String msg = MessageFormat.format(
                    Messages.RuntimeUnitLauncherLaunchDelegate_Failed_to_get_framework_params_from_bundle_0_1,
                    bundle.getSymbolicName(), FrameworkUtils.PARAMS_FILE_NAME);
                JUnitPlugin.log(JUnitPlugin.createErrorStatus(msg, new IOException()));
                return false;
            }
            var file = URIUtil.toFile(URIUtil.toURI(frameworkParamsUrl));

            if (!file.exists())
            {
                String msg = MessageFormat.format(
                    Messages.RuntimeUnitLauncherLaunchDelegate_Failed_to_read_framework_params_0, file.toString());
                JUnitPlugin.log(JUnitPlugin.createErrorStatus(msg, new IOException()));
                return false;
            }

            parseParamsTemplate(frameworkParamsUrl, configuration);

        }
        catch (IOException | URISyntaxException e)
        {
            String msg =
                MessageFormat.format(Messages.RuntimeUnitLauncherLaunchDelegate_Failed_to_save_framework_params_0,
                    FrameworkUtils.PARAMS_FILE_NAME);
            JUnitPlugin.log(JUnitPlugin.createErrorStatus(msg, e));
            return false;

        }

        return true;
    }

    @Override
    protected RuntimeExecutionArguments buildExecutionArguments(final ILaunchConfiguration configuration,
        final IV8Project v8project, final InfobaseReference infobase, final IProgressMonitor monitor)
        throws CoreException
    {
        var arguments = super.buildExecutionArguments(configuration, v8project, infobase, monitor);

        String paramsFilePathName = FrameworkUtils.getConfigurationFilesPath(configuration);

        var frameworkSettings = FrameworkUtils.getFrameworkSettings();
        String externalObjectStartupOptions =
            FrameworkUtils.getFrameworkStartupOptions(frameworkSettings, paramsFilePathName);

        final String externalObjectDumpPath = paramsFilePathName + "framework.epf"; //$NON-NLS-1$
        var file = new File(externalObjectDumpPath);
        arguments.setExternalObjectDumpPath(file.toPath());

        String startupOptions = configuration.getAttribute(ILaunchConfigurationAttributes.STARTUP_OPTION, (String)null);
        if (Strings.isNullOrEmpty(startupOptions))
            startupOptions = externalObjectStartupOptions;
        else
            startupOptions = externalObjectStartupOptions + ";" + startupOptions; //$NON-NLS-1$
        arguments.setStartupOption(startupOptions);

        return arguments;
    }

    @Override
    protected IStatus isValid(ILaunchConfiguration configuration, String mode) throws CoreException
    {
        IStatus parentStatus = super.isValid(configuration, mode);
        if (parentStatus != Status.OK_STATUS)
            return parentStatus;

        String extensionProjectToTest =
            configuration.getAttribute(JUnitLaunchConfigurationConstants.EXTENSION_PROJECT_TO_TEST, (String)null);
        boolean isExtensionValid = !Strings.isNullOrEmpty(extensionProjectToTest);

        String extensionModuleToTest =
            configuration.getAttribute(JUnitLaunchConfigurationConstants.EXTENSION_MODULE_TO_TEST, (String)null);
        boolean runModuleTests = configuration.getAttribute(JUnitLaunchConfigurationConstants.RUN_MODULE_TESTS, false);
        boolean isModuleValid = !Strings.isNullOrEmpty(extensionModuleToTest) || !runModuleTests;

        String extensionTagToTest =
            configuration.getAttribute(JUnitLaunchConfigurationConstants.EXTENSION_TAG_TO_TEST, (String)null);
        boolean runTagTests = configuration.getAttribute(JUnitLaunchConfigurationConstants.RUN_TAG_TESTS, false);
        boolean isTagValid = !Strings.isNullOrEmpty(extensionTagToTest) || !runTagTests;

        return isExtensionValid && isModuleValid && isTagValid ? Status.OK_STATUS : Status.CANCEL_STATUS;
    }

}
