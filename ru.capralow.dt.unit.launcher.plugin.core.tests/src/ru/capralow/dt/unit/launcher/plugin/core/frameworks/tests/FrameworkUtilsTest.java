package ru.capralow.dt.unit.launcher.plugin.core.frameworks.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;

import ru.capralow.dt.unit.launcher.plugin.core.frameworks.FrameworkUtils;

public class FrameworkUtilsTest {

	@Test
	public void testGetModulesForProjectEmpty() {
		List<CommonModule> modules = FrameworkUtils.getModulesForProject(null, null);

		assertEquals("Список модулей: пустой", new ArrayList<CommonModule>(), modules);
	}

	@Test
	public void testGetTagsForProjectEmpty() {
		List<String> tags = FrameworkUtils.getTagsForProject(null, null);

		assertEquals("Список тегов: пустой", new ArrayList<String>(), tags);
	}

}
