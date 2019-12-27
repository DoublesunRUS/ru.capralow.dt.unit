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

import org.jacoco.core.analysis.CoverageNodeImpl;
import org.jacoco.core.analysis.ICoverageNode;

import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.metadata.mdclass.MdObject;
import com._1c.g5.v8.dt.metadata.mdclass.Subsystem;

import ru.capralow.dt.coverage.core.analysis.IBslModelCoverage;
import ru.capralow.dt.coverage.internal.core.CoverageCorePlugin;

/**
 * The IBslModelCoverage implementation maps Bsl elements to its corresponding
 * coverage data objects.
 */
public class BslModelCoverage extends CoverageNodeImpl implements IBslModelCoverage {

	/** Maps Methods to coverage objects */
	private final Map<Method, ICoverageNode> coverageMap = new HashMap<>();

	/** List of all IV8Project objects with coverage information attached */
	private final List<IV8Project> projects = new ArrayList<>();

	/** List of all Subsystem objects with coverage information */
	private final List<Subsystem> subsystems = new ArrayList<>();

	/** List of all MdObject objects with coverage information */
	private final List<MdObject> mdObjects = new ArrayList<>();

	private IV8ProjectManager projectManager;

	public BslModelCoverage() {
		super(ElementType.GROUP, "BslModel"); //$NON-NLS-1$

		this.projectManager = CoverageCorePlugin.getInjector().getInstance(IV8ProjectManager.class);
	}

	public void putMethod(Method method, MdObject mdObject, ICoverageNode coverage) {
		coverageMap.put(method, coverage);
		mdObjects.add(mdObject);
		projects.add(projectManager.getProject(mdObject));
	}

	public IV8Project[] getProjects() {
		IV8Project[] arr = new IV8Project[projects.size()];
		return projects.toArray(arr);
	}

	public Subsystem[] getSubsystems() {
		Subsystem[] arr = new Subsystem[subsystems.size()];
		return subsystems.toArray(arr);
	}

	public MdObject[] getMdObjects() {
		MdObject[] arr = new MdObject[mdObjects.size()];
		return mdObjects.toArray(arr);
	}

	public ICoverageNode getCoverageFor(Method element) {
		final ICoverageNode coverage = coverageMap.get(element);
		if (coverage != null) {
			return coverage;
		}

		return null;
	}
}
