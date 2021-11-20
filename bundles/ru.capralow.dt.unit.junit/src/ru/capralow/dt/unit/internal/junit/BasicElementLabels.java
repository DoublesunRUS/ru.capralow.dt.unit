/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit;

import java.io.File;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.osgi.util.TextProcessor;

/**
 * A label provider for basic elements like paths. The label provider will make sure that the labels are correctly
 * shown in RTL environments.
 */
public final class BasicElementLabels
{

    /**
     * Returns the label for a file pattern like '*.java'
     *
     * @param name the pattern
     * @return the label of the pattern.
     */
    public static String getFilePattern(String name)
    {
        return markLtr(name, "*.?/\\:."); //$NON-NLS-1$
    }

    /**
     * Returns a label for Java element name. Example is 'new Test<? extends List>() { ...}'.
     * This method should only be used for simple element names. Use
     * JavaElementLabels to create a label from a Java element.
     *
     * @param name the Java element name.
     * @return the label for the Java element
     */
    public static String getJavaElementName(String name)
    {
        return markLtr(name, "<>()?,{}.:"); //$NON-NLS-1$
    }

    /**
     * Returns the label of the path of a file.
     *
     * @param file the file
     * @return the label of the file path to be used in the UI.
     */
    public static String getPathLabel(File file)
    {
        return markLtr(file.getAbsolutePath(), "/\\:."); //$NON-NLS-1$
    }

    /**
     * Returns the label of a path.
     *
     * @param path the path
     * @param isOsPath if <code>true</code>, the path represents an OS path, else it is a workspace path.
     * @return the label of the path to be used in the UI.
     */
    public static String getPathLabel(IPath path, boolean isOsPath)
    {
        String label;
        if (isOsPath)
        {
            label = path.toOSString();
        }
        else
        {
            label = path.makeRelative().toString();
        }
        return markLtr(label, "/\\:."); //$NON-NLS-1$
    }

    /**
     * Returns a label for a resource name.
     *
     * @param resource the resource
     * @return the label of the resource name.
     */
    public static String getResourceName(IResource resource)
    {
        return markLtr(resource.getName(), ":."); //$NON-NLS-1$
    }

    /**
     * Returns a label for a resource name.
     *
     * @param resourceName the resource name
     * @return the label of the resource name.
     */
    public static String getResourceName(String resourceName)
    {
        return markLtr(resourceName, ":."); //$NON-NLS-1$
    }

    /**
     * Returns the label for a URL, URI or URL part. Example is 'http://www.x.xom/s.html#1'
     *
     * @param name the URL string
     * @return the label of the URL.
     */
    public static String getUrlPart(String name)
    {
        return markLtr(name, ":@?-#/\\:."); //$NON-NLS-1$
    }

    /**
     * Returns a label for a version name. Example is '1.4.1'
     *
     * @param name the version string
     * @return the version label
     */
    public static String getVersionName(String name)
    {
        return markLtr(name, ":."); //$NON-NLS-1$
    }

    /**
     * Adds special marks so that that the given string is readable in a BIDI environment.
     *
     * @param string the string
     * @param delimiters the additional delimiters
     * @return the processed styled string
     */
    private static String markLtr(String string, String delimiters)
    {
        return TextProcessor.process(string, delimiters);
    }

    private BasicElementLabels()
    {
    }
}
