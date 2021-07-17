/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com._1c.g5.v8.dt.metadata.mdclass.ExternalDataProcessor;
import com._1c.g5.v8.dt.metadata.mdclass.MdObject;
import com._1c.g5.wiring.InjectorAwareServiceRegistrator;
import com._1c.g5.wiring.ServiceInitialization;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class JUnitUiPlugin
    extends AbstractUIPlugin
{
    public static final String ID = "ru.capralow.dt.unit.junit.ui"; //$NON-NLS-1$

    private static final IPath ICONS_PATH = new Path("$nl$/icons"); //$NON-NLS-1$

    private static JUnitUiPlugin instance;

    public static Collection<MdObject> availableForLaunch(Collection<MdObject> externalObjects)
    {
        return externalObjects.stream().filter(JUnitUiPlugin::availableForLaunch).collect(Collectors.toList());
    }

    public static boolean availableForLaunch(MdObject externalObject)
    {
        return externalObject instanceof ExternalDataProcessor;
    }

    public static IStatus createErrorStatus(String message)
    {
        return new Status(IStatus.ERROR, ID, 0, message, (Throwable)null);
    }

    public static IStatus createErrorStatus(String message, int code)
    {
        return new Status(IStatus.ERROR, ID, code, message, (Throwable)null);
    }

    public static IStatus createErrorStatus(String message, int code, Throwable throwable)
    {
        return new Status(IStatus.ERROR, ID, code, message, throwable);
    }

    public static IStatus createErrorStatus(String message, Throwable throwable)
    {
        return new Status(IStatus.ERROR, ID, 0, message, throwable);
    }

    public static Image createImage(String path)
    {
        return getImageDescriptor(path).createImage();
    }

    public static Shell getActiveWorkbenchShell()
    {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null)
        {
            IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
            if (windows.length > 0)
            {
                return windows[0].getShell();
            }
        }
        else
        {
            return window.getShell();
        }
        return null;
    }

    /**
     * Returns the active workbench window
     *
     * @return the active workbench window
     */
    public static IWorkbenchWindow getActiveWorkbenchWindow()
    {
        if (instance == null)
            return null;
        IWorkbench workBench = PlatformUI.getWorkbench();
        if (workBench == null)
            return null;
        return workBench.getActiveWorkbenchWindow();
    }

    public static Image getImage(String symbolicName)
    {
        return instance.getImageRegistry().get(symbolicName);
    }

    public static ImageDescriptor getImageDescriptor(String symbolicName)
    {
        return instance.getImageRegistry().getDescriptor(symbolicName);
    }

    public static JUnitUiPlugin getInstance()
    {
        return instance;
    }

    public static void log(IStatus status)
    {
        getInstance().getLog().log(status);
    }

    public static void log(Throwable throwable)
    {
        IStatus status;
        var top = throwable;
        if (throwable instanceof CoreException
            && (status = ((CoreException)throwable).getStatus()).getException() != null)
        {
            top = status.getException();
        }
        log(new Status(4, ID, Messages.JUnitUiPlugin_Internal_error, top));
    }

    /**
     * Sets the three image descriptors for enabled, disabled, and hovered to an action. The actions
     * are retrieved from the *lcl16 folders.
     *
     * @param action the action
     * @param iconName the icon name
     */
    public static void setLocalImageDescriptors(IAction action, String iconName)
    {
        setImageDescriptors(action, "lcl16", iconName); //$NON-NLS-1$
    }

    /**
     * Creates an image descriptor for the given path in a bundle. The path can
     * contain variables like $NL$. If no image could be found,
     * <code>useMissingImageDescriptor</code> decides if either the 'missing
     * image descriptor' is returned or <code>null</code>.
     *
     * @param bundle a bundle
     * @param path path in the bundle
     * @param useMissingImageDescriptor if <code>true</code>, returns the shared image descriptor
     *            for a missing image. Otherwise, returns <code>null</code> if the image could not
     *            be found
     * @return an {@link ImageDescriptor}, or <code>null</code> iff there's
     *         no image at the given location and
     *         <code>useMissingImageDescriptor</code> is <code>true</code>
     */
    private static ImageDescriptor createImageDescriptor(Bundle bundle, IPath path, boolean useMissingImageDescriptor)
    {
        var url = FileLocator.find(bundle, path, null);
        if (url != null)
        {
            return ImageDescriptor.createFromURL(url);
        }
        if (useMissingImageDescriptor)
        {
            return ImageDescriptor.getMissingImageDescriptor();
        }
        return null;
    }

    /*
     * Creates an image descriptor for the given prefix and name in the JDT UI bundle. The path can
     * contain variables like $NL$.
     * If no image could be found, <code>useMissingImageDescriptor</code> decides if either
     * the 'missing image descriptor' is returned or <code>null</code>.
     * or <code>null</code>.
     */
    private static ImageDescriptor createImageDescriptor(String pathPrefix, String imageName,
        boolean useMissingImageDescriptor)
    {
        IPath path = ICONS_PATH.append(pathPrefix).append(imageName);
        return createImageDescriptor(getInstance().getBundle(), path, useMissingImageDescriptor);
    }

    private static void setImageDescriptors(IAction action, String type, String relPath)
    {
        ImageDescriptor id = createImageDescriptor("d" + type, relPath, false); //$NON-NLS-1$
        if (id != null)
            action.setDisabledImageDescriptor(id);

        var descriptor = createImageDescriptor("e" + type, relPath, true); //$NON-NLS-1$
        action.setHoverImageDescriptor(descriptor);
        action.setImageDescriptor(descriptor);
    }

    private InjectorAwareServiceRegistrator registrator;

    private Injector injector;

    public IDialogSettings getDialogSettingsSection(String name)
    {
        IDialogSettings dialogSettings = getDialogSettings();
        IDialogSettings section = dialogSettings.getSection(name);
        if (section == null)
        {
            section = dialogSettings.addNewSection(name);
        }
        return section;
    }

    public synchronized Injector getInjector()
    {
        if (injector == null)
            injector = createInjector();

        return injector;
    }

    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start(context);

        registrator = new InjectorAwareServiceRegistrator(context, this::getInjector);

        ServiceInitialization.schedule(() -> registrator.activateManagedService(UnitLauncherManager.class));

        instance = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
        instance = null;

        registrator.deactivateManagedServices(this);

        super.stop(context);
    }

    private Injector createInjector()
    {
        try
        {
            return Guice.createInjector(new ExternalDependenciesModule(this));

        }
        catch (Exception e)
        {
            String msg = MessageFormat.format(Messages.JUnitUiPlugin_Failed_to_create_injector_for_0,
                getBundle().getSymbolicName());
            log(createErrorStatus(msg, e));
            return null;

        }
    }

}
