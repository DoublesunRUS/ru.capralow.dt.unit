/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.unit.junit.frameworks.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.junit.Test;
import org.osgi.framework.Bundle;

import ru.capralow.dt.unit.junit.frameworks.FrameworkUtils;

public class FrameworkUtilsTest
{
    private static final String PLUGIN_ID = "ru.capralow.dt.unit.junit.tests"; //$NON-NLS-1$

    @Test
    public void testGetModulesForProjectEmpty()
    {
        List<String> modules = FrameworkUtils.getTestModules(null);

        assertEquals("Список модулей: пустой", new ArrayList<String>(), modules); //$NON-NLS-1$
    }

    @Test
    public void testGetModulesForProjectNoFeatures()
    {
        Bundle bundle = Platform.getBundle(PLUGIN_ID);
        IPath resourcePath = Platform.getStateLocation(bundle);

        List<String> modules = FrameworkUtils.getTestModules(resourcePath);

        assertEquals("Список модулей: без фич", new ArrayList<String>(), modules); //$NON-NLS-1$
    }

    @Test
    public void testGetTagsForProjectEmpty()
    {
        List<String> tags = FrameworkUtils.getTestTags(null);

        assertEquals("Список тегов: пустой", new ArrayList<String>(), tags); //$NON-NLS-1$
    }

    @Test
    public void testGetTagsForProjectNoFeatures()
    {
        Bundle bundle = Platform.getBundle(PLUGIN_ID);
        IPath resourcePath = Platform.getStateLocation(bundle);

        List<String> tags = FrameworkUtils.getTestTags(resourcePath);

        assertEquals("Список тегов: без фич", new ArrayList<String>(), tags); //$NON-NLS-1$
    }

}
