/*******************************************************************************
 * Copyright (c) 2006, 2019 Mountainminds GmbH & Co. KG and Contributors
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *
 * Adapted by Alexander Kapralov
 *
 ******************************************************************************/
package ru.capralow.dt.coverage.core.launching;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com._1c.g5.v8.dt.bsl.model.Module;

import ru.capralow.dt.coverage.core.JavaProjectKit;

/**
 * Tests for {@link BslApplicationLauncher}.
 */
public class BslApplicationLauncherTest {

	private JavaProjectKit javaProject1;
	private JavaProjectKit javaProject2;
	private ICoverageLauncher launcher;

	@Before
	public void setup() throws Exception {
		javaProject1 = new JavaProjectKit("project1");
		javaProject2 = new JavaProjectKit("project2");

		launcher = new BslApplicationLauncher();
	}

	@After
	public void teardown() throws Exception {
		javaProject1.destroy();
		javaProject2.destroy();
	}

	@Test
	public void testNoProject() throws Exception {
		JavaProjectKit.waitForBuild();

		ILaunchConfigurationWorkingCopy configuration = getJavaApplicationType().newInstance(javaProject1.project,
				"test.launch");

		final Collection<Module> scope = launcher.getOverallScope(configuration);

		assertEquals(set(), set(scope));
	}

	@Test
	public void testProjectWithSourceFolders() throws Exception {
		IPackageFragmentRoot rootSrc1 = javaProject1.createSourceFolder("src");
		IPackageFragmentRoot rootSrc2 = javaProject1.createSourceFolder("test");
		JavaProjectKit.waitForBuild();

		ILaunchConfigurationWorkingCopy configuration = getJavaApplicationType().newInstance(javaProject1.project,
				"test.launch");
		configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "project1");

		final Collection<Module> scope = launcher.getOverallScope(configuration);

		assertEquals(set(rootSrc1, rootSrc2), set(scope));
	}

	@Test
	public void testProjectWithRootSourceFolder() throws Exception {
		IPackageFragmentRoot rootSrc1 = javaProject1.createSourceFolder();
		JavaProjectKit.waitForBuild();

		ILaunchConfigurationWorkingCopy configuration = getJavaApplicationType().newInstance(javaProject1.project,
				"test.launch");
		configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "project1");

		final Collection<Module> scope = launcher.getOverallScope(configuration);

		assertEquals(set(rootSrc1), set(scope));
	}

	@Test
	public void testProjectWithLibrary() throws Exception {
		IPackageFragmentRoot rootBin1 = javaProject1.createJAR("testdata/bin/signatureresolver.jar",
				"/sample.jar",
				new Path("/UnitTestProject/sample.jar"),
				null);
		JavaProjectKit.waitForBuild();

		ILaunchConfigurationWorkingCopy configuration = getJavaApplicationType().newInstance(javaProject1.project,
				"test.launch");
		configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "project1");

		final Collection<Module> scope = launcher.getOverallScope(configuration);

		assertEquals(set(rootBin1), set(scope));
	}

	@Test
	public void testProjectWithProjectReference() throws Exception {
		IPackageFragmentRoot rootSrc1 = javaProject1.createSourceFolder("src");
		IPackageFragmentRoot rootSrc2 = javaProject2.createSourceFolder("src");
		javaProject1.addProjectReference(javaProject2);
		JavaProjectKit.waitForBuild();

		ILaunchConfigurationWorkingCopy configuration = getJavaApplicationType().newInstance(javaProject1.project,
				"test.launch");
		configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "project1");

		final Collection<Module> scope = launcher.getOverallScope(configuration);

		assertEquals(set(rootSrc1, rootSrc2), set(scope));
	}

	private ILaunchConfigurationType getJavaApplicationType() {
		return DebugPlugin.getDefault().getLaunchManager()
				.getLaunchConfigurationType("org.eclipse.jdt.launching.localJavaApplication");
	}

	private <E> Set<E> set(E... elements) {
		return new HashSet<E>(Arrays.asList(elements));
	}

	private <E> Set<E> set(Collection<E> elements) {
		Set<E> set = new HashSet<E>();
		for (E e : elements) {
			assertTrue("Duplicate element " + e, set.add(e));
		}
		return set;
	}

}
