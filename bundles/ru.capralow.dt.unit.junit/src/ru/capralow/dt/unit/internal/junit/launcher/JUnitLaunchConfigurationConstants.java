/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.launcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.debug.core.IDebugConfigurationAttributes;

import ru.capralow.dt.unit.internal.junit.JUnitPlugin;

/**
 * Attribute keys used by the IJUnitLaunchConfiguration. Note that these constants are not API and
 * might change in the future.
 */
public final class JUnitLaunchConfigurationConstants
{

    public static final String RUN_EXTENSION_TESTS = JUnitPlugin.ID + ".ATTR_RUN_EXTENSION_TESTS"; //$NON-NLS-1$
    public static final String RUN_MODULE_TESTS = JUnitPlugin.ID + ".ATTR_RUN_MODULE_TESTS"; //$NON-NLS-1$
    public static final String RUN_TAG_TESTS = JUnitPlugin.ID + ".ATTR_RUN_TAG_TESTS"; //$NON-NLS-1$

    public static final String EXTENSION_PROJECT_TO_TEST = JUnitPlugin.ID + ".ATTR_EXTENSION_PROJECT_TO_TEST"; //$NON-NLS-1$
    public static final String EXTENSION_MODULE_TO_TEST = JUnitPlugin.ID + ".ATTR_EXTENSION_MODULE_TO_TEST"; //$NON-NLS-1$
    public static final String EXTENSION_TAG_TO_TEST = JUnitPlugin.ID + ".ATTR_EXTENSION_TAG_TO_TEST"; //$NON-NLS-1$

    public static final String MODE_RUN_QUIETLY_MODE = "runQuietly"; //$NON-NLS-1$

    public static IV8Project getV8Project(ILaunchConfiguration configuration)
    {
        try
        {
            String projectName = configuration.getAttribute(IDebugConfigurationAttributes.PROJECT_NAME, (String)null);
            if (projectName != null && projectName.length() > 0)
            {
                IV8ProjectManager projectManager =
                    JUnitPlugin.getInstance().getInjector().getInstance(IV8ProjectManager.class);

                return projectManager.getProject(projectName);
            }
        }
        catch (CoreException e)
        {
            // Нечего делать
        }
        return null;
    }

    private JUnitLaunchConfigurationConstants()
    {
    }

}
