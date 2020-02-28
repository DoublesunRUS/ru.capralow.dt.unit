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
package ru.capralow.dt.coverage.internal.core.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.jacoco.core.analysis.CoverageNodeImpl;
import org.jacoco.core.analysis.ISourceNode;

import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;

import ru.capralow.dt.coverage.core.MdUtils;
import ru.capralow.dt.coverage.core.analysis.IBslModelCoverage;
import ru.capralow.dt.coverage.internal.core.CoverageCorePlugin;

/**
 * The IBslModelCoverage implementation maps Bsl elements to its corresponding
 * coverage data objects.
 */
public class BslModelCoverage extends CoverageNodeImpl implements IBslModelCoverage {

	/** Maps URI to coverage objects */
	private Map<URI, ISourceNode> coverageMap = new HashMap<>();

	/** Maps modules to projects objects */
	private Map<URI, URI> projectsMap = new HashMap<>();

	/** Maps modules to subsystems objects */
	private Map<URI, URI> subsystemsMap = new HashMap<>();

	/** Maps methods to modules objects */
	private Map<URI, List<URI>> modulesMap = new HashMap<>();

	/** List of all IV8Project objects with coverage information attached */
	private List<URI> projects = new ArrayList<>();

	/** List of all Subsystem objects with coverage information */
	private List<URI> subsystems = new ArrayList<>();

	/** List of all Module objects with coverage information */
	private List<URI> modules = new ArrayList<>();

	/** List of all Method objects with coverage information */
	private List<URI> methods = new ArrayList<>();

	private IV8ProjectManager projectManager;

	public BslModelCoverage() {
		super(ElementType.GROUP, "BslModel"); //$NON-NLS-1$

		this.projectManager = CoverageCorePlugin.getInstance().getInjector().getInstance(IV8ProjectManager.class);
	}

	@Override
	public ISourceNode getCoverageFor(URI element) {
		ISourceNode coverage = coverageMap.get(element);
		if (coverage != null) {
			return coverage;
		}

		return null;
	}

	@Override
	public URI[] getMdObjects() {
		URI[] arr = new URI[modules.size()];
		return modules.toArray(arr);
	}

	@Override
	public URI[] getProjects() {
		URI[] arr = new URI[projects.size()];
		return projects.toArray(arr);
	}

	@Override
	public URI[] getSubsystems() {
		URI[] arr = new URI[subsystems.size()];
		return subsystems.toArray(arr);
	}

	public void putMethod(URI methodURI, URI moduleURI, URI configurationURI, ISourceNode methodCoverage) {
		Module module = (Module) MdUtils.getEObjectByURI(moduleURI);
		IV8Project project = projectManager.getProject(module);

		coverageMap.put(methodURI, methodCoverage);

		BslNodeImpl moduleCoverage = (BslNodeImpl) getCoverageFor(moduleURI);
		if (moduleCoverage == null) {
			modules.add(moduleURI);

			modulesMap.put(moduleURI, new ArrayList<>());

			moduleCoverage = new BslNodeImpl(ElementType.CLASS, module.getUniqueName());
			coverageMap.put(moduleURI, moduleCoverage);
		}
		moduleCoverage.increment(methodCoverage);
		List<URI> methodsList = modulesMap.get(moduleURI);
		methodsList.add(methodURI);

		BslNodeImpl projectCoverage = (BslNodeImpl) getCoverageFor(configurationURI);
		if (projectCoverage == null) {
			projects.add(configurationURI);

			projectCoverage = new BslNodeImpl(ElementType.GROUP, project.getProject().getName());
			coverageMap.put(configurationURI, projectCoverage);
		}
		projectCoverage.increment(moduleCoverage);
	}

	public void updateModuleCoverage(URI moduleUri) {
		BslNodeImpl moduleCoverage = (BslNodeImpl) coverageMap.get(moduleUri);
		List<URI> moduleMethods = modulesMap.get(moduleUri);
		if (moduleCoverage == null || moduleMethods == null)
			return;
		for (URI methodURI : moduleMethods) {
			BslNodeImpl methodCoverage = (BslNodeImpl) coverageMap.get(methodURI);
			moduleCoverage.increment(methodCoverage);
		}
	}
}
