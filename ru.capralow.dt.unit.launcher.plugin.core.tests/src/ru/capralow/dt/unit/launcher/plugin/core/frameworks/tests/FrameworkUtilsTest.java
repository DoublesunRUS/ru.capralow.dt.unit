package ru.capralow.dt.unit.launcher.plugin.core.frameworks.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ru.capralow.dt.unit.launcher.plugin.core.frameworks.FrameworkUtils;

public class FrameworkUtilsTest {

	@Test
	public void testGetModulesForProjectEmpty() {
		List<String> modules = FrameworkUtils.getTestModules(null);

		assertEquals("Список модулей: пустой", new ArrayList<String>(), modules); //$NON-NLS-1$
	}

	@Test
	public void testGetTagsForProjectEmpty() {
		List<String> tags = FrameworkUtils.getTestTags(null);

		assertEquals("Список тегов: пустой", new ArrayList<String>(), tags); //$NON-NLS-1$
	}

}
