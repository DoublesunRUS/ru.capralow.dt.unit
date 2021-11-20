/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.launcher;

import java.util.Objects;

/**
 * @author Aleksandr Kapralov
 *
 */
public class JUnitRuntimeClasspathEntry
{
    private final String fPluginId;

    private final String fPluginRelativePath;

    /**
     * @param pluginId
     * @param jarFile
     */
    public JUnitRuntimeClasspathEntry(String pluginId, String jarFile)
    {
        fPluginId = pluginId;
        fPluginRelativePath = jarFile;
    }

    /**
     * @return JUnitRuntimeClasspathEntry
     */
    public JUnitRuntimeClasspathEntry developmentModeEntry()
    {
        return new JUnitRuntimeClasspathEntry(getPluginId(), "bin"); //$NON-NLS-1$
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof JUnitRuntimeClasspathEntry))
        {
            return false;
        }
        JUnitRuntimeClasspathEntry other = (JUnitRuntimeClasspathEntry)obj;
        if (!fPluginId.equals(other.getPluginId()))
        {
            return false;
        }
        return Objects.equals(fPluginRelativePath, other.getPluginRelativePath());
    }

    /**
     * @return String
     */
    public String getPluginId()
    {
        return fPluginId;
    }

    /**
     * @return String
     */
    public String getPluginRelativePath()
    {
        return fPluginRelativePath;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(fPluginId, fPluginRelativePath);
    }

    @Override
    public String toString()
    {
        return "ClasspathEntry(" + fPluginId + "/" + fPluginRelativePath + ")"; //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
    }
}
