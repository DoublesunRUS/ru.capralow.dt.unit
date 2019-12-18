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
 ******************************************************************************/
package ru.capralow.dt.coverage.ui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.JavaRuntime;

import ru.capralow.dt.coverage.core.CoverageTools;

/**
 * Utility class to setup Java projects programmatically.
 *
 * TODO get rid of duplication with ru.capralow.dt.coverage.core.JavaProjectKit
 * from ru.capralow.dt.coverage.core.test
 */
public class JavaProjectKit {

	private static final String DEFAULT_PROJECT_NAME = "UnitTestProject";

	public static void waitForBuild() throws OperationCanceledException, InterruptedException {
		Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
	}

	public final IWorkspace workspace;

	public final IProject project;

	public final IJavaProject javaProject;

	public JavaProjectKit() throws CoreException {
		this(DEFAULT_PROJECT_NAME);
	}

	public JavaProjectKit(String name) throws CoreException {
		workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		project = root.getProject(name);
		project.create(null);
		project.open(null);
		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] { JavaCore.NATURE_ID });
		project.setDescription(description, null);
		javaProject = JavaCore.create(project);
		javaProject.setRawClasspath(new IClasspathEntry[0], null);
		addClassPathEntry(JavaRuntime.getDefaultJREContainerEntry());
	}

	public void addClassPathEntry(IClasspathEntry entry) throws CoreException {
		IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
		IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
		System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
		newEntries[oldEntries.length] = entry;
		javaProject.setRawClasspath(newEntries, null);
	}

	public ICompilationUnit createCompilationUnit(IPackageFragment fragment, String name, String content)
			throws JavaModelException {
		return fragment.createCompilationUnit(name, content, false, null);
	}

	/**
	 * Creates launch configuration for the type with given name.
	 */
	public ILaunchConfiguration createLaunchConfiguration(String mainTypeName) throws Exception {
		ILaunchConfigurationType type = DebugPlugin.getDefault().getLaunchManager()
				.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
		ILaunchConfigurationWorkingCopy config = type.newInstance(null, mainTypeName);
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, javaProject.getElementName());
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, mainTypeName);
		Set<String> modes = new HashSet<>();
		modes.add(CoverageTools.LAUNCH_MODE);
		config.setPreferredLaunchDelegate(modes, IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
		return config.doSave();
	}

	public IPackageFragment createPackage(IPackageFragmentRoot fragmentRoot, String name) throws CoreException {
		return fragmentRoot.createPackageFragment(name, false, null);
	}

	public IPackageFragmentRoot createSourceFolder() throws CoreException {
		IPackageFragmentRoot packageRoot = javaProject.getPackageFragmentRoot(javaProject.getResource());
		addClassPathEntry(JavaCore.newSourceEntry(packageRoot.getPath()));
		return packageRoot;
	}

	public void enableJava5() {
		javaProject.setOption(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_5);
		javaProject.setOption(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5);
	}

}
