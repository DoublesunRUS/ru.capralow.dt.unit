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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jacoco.core.analysis.CoverageNodeImpl;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.analysis.IMethodCoverage;

import com._1c.g5.v8.dt.bm.index.emf.IBmEmfIndexManager;
import com._1c.g5.v8.dt.bm.index.emf.IBmEmfIndexProvider;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;

import ru.capralow.dt.coverage.core.MdUtils;
import ru.capralow.dt.coverage.core.analysis.IBslModelCoverage;
import ru.capralow.dt.coverage.internal.core.CoverageCorePlugin;
import ru.capralow.dt.coverage.internal.core.DebugOptions;
import ru.capralow.dt.coverage.internal.core.DebugOptions.ITracer;

/**
 * The IBslModelCoverage implementation maps Bsl elements to its corresponding
 * coverage data objects.
 */
public class BslModelCoverage extends CoverageNodeImpl implements IBslModelCoverage {

	private static final ITracer TRACER = DebugOptions.ANALYSISTRACER;

	/** Maps Bsl elements to coverage objects */
	private final Map<Module, ICoverageNode> coverageMap = new HashMap<>();

	/** List of all IV8Project objects with coverage information attached */
	private final List<IV8Project> projects = new ArrayList<>();

	/** List of all Module objects with coverage information */
	private final List<Module> fragmentRoots = new ArrayList<>();

	/** List of all IPackageFragment objects with coverage information */
	private final List<URI> fragments = new ArrayList<>();

	/** List of all IType objects with coverage information */
	private final List<URI> types = new ArrayList<>();

	private IBmEmfIndexManager bmEmfIndexManager;

	public BslModelCoverage() {
		super(ElementType.GROUP, "BslModel"); //$NON-NLS-1$

		this.bmEmfIndexManager = CoverageCorePlugin.getInjector().getInstance(IBmEmfIndexManager.class);
	}

	public void putFragmentRoot(URI root, IBundleCoverage coverage) {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(root.segment(1));
		IBmEmfIndexProvider bmEmfIndexProvider = bmEmfIndexManager.getEmfIndexProvider(project);

		Module module = ((CommonModule) MdUtils
				.getConfigurationObject(root.segment(3).concat(".").concat(root.segment(4)), bmEmfIndexProvider))
						.getModule();

		coverageMap.put(module, coverage);
		fragmentRoots.add(module);
		// getProjectCoverage(root.getJavaProject()).increment(coverage);
	}

	private CoverageNodeImpl getProjectCoverage(IV8Project project) {
		CoverageNodeImpl coverage = (CoverageNodeImpl) coverageMap.get(project);
		if (coverage == null) {
			// coverage = new CoverageNodeImpl(ElementType.GROUP, project.getElementName());
			// coveragemap.put(project, coverage);
			projects.add(project);
		}
		return coverage;
	}

	public void putFragment(URI element, ICoverageNode coverage) {
		// coverageMap.put(element, coverage);
		// fragments.add(element);
	}

	public void putType(URI element, ICoverageNode coverage) {
		// coverageMap.put(element, coverage);
		// types.add(element);
	}

	public void putClassFile(IClassFile element, ICoverageNode coverage) {
		// coveragemap.put(element, coverage);
	}

	public void putCompilationUnit(ICompilationUnit element, ICoverageNode coverage) {
		// coveragemap.put(element, coverage);
	}

	// IJavaModelCoverage interface

	public IV8Project[] getProjects() {
		IV8Project[] arr = new IV8Project[projects.size()];
		return projects.toArray(arr);
	}

	public Module[] getPackageFragmentRoots() {
		Module[] arr = new Module[fragmentRoots.size()];
		return fragmentRoots.toArray(arr);
	}

	public IPackageFragment[] getPackageFragments() {
		IPackageFragment[] arr = new IPackageFragment[fragments.size()];
		return fragments.toArray(arr);
	}

	public IType[] getTypes() {
		IType[] arr = new IType[types.size()];
		return types.toArray(arr);
	}

	public ICoverageNode getCoverageFor(IJavaElement element) {
		final ICoverageNode coverage = coverageMap.get(element);
		if (coverage != null) {
			return coverage;
		}
		if (IJavaElement.METHOD == element.getElementType()) {
			resolveMethods((IType) element.getParent());
			return coverageMap.get(element);
		}
		return null;
	}

	private void resolveMethods(final IType type) {
		IClassCoverage classCoverage = (IClassCoverage) getCoverageFor(type);
		if (classCoverage == null) {
			return;
		}
		try {
			MethodLocator locator = new MethodLocator(type);
			for (IMethodCoverage methodCoverage : classCoverage.getMethods()) {
				final IMethod method = locator.findMethod(methodCoverage.getName(), methodCoverage.getDesc());
				if (method != null) {
					// coveragemap.put(method, methodCoverage);
				} else {
					TRACER.trace("Method not found in Java model: {0}.{1}{2}",
							type.getFullyQualifiedName(),
							methodCoverage.getName(),
							methodCoverage.getDesc());
				}
			}
		} catch (JavaModelException e) {
			TRACER.trace("Error while creating method locator for {0}: {1}", type.getFullyQualifiedName(), e);
		}
	}
}
