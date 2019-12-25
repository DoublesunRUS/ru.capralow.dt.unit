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
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jacoco.core.analysis.CoverageNodeImpl;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.analysis.IMethodCoverage;

import com._1c.g5.v8.dt.core.platform.IV8Project;

import ru.capralow.dt.coverage.core.analysis.IBslModelCoverage;
import ru.capralow.dt.coverage.internal.core.DebugOptions;
import ru.capralow.dt.coverage.internal.core.DebugOptions.ITracer;

/**
 * The IBslModelCoverage implementation maps Bsl elements to its corresponding
 * coverage data objects.
 */
public class BslModelCoverage extends CoverageNodeImpl implements IBslModelCoverage {

	private static final ITracer TRACER = DebugOptions.ANALYSISTRACER;

	/** Maps Bsl elements to coverage objects */
	private final Map<URI, ICoverageNode> coveragemap = new HashMap<>();

	/** List of all IV8Project objects with coverage information attached */
	private final List<IV8Project> projects = new ArrayList<>();

	/** List of all IPackageFragmentRoot objects with coverage information */
	private final List<URI> fragmentroots = new ArrayList<>();

	/** List of all IPackageFragment objects with coverage information */
	private final List<URI> fragments = new ArrayList<>();

	/** List of all IType objects with coverage information */
	private final List<URI> types = new ArrayList<>();

	public BslModelCoverage() {
		super(ElementType.GROUP, "BslModel"); //$NON-NLS-1$
	}

	public void putFragmentRoot(URI root, IBundleCoverage coverage) {
		coveragemap.put(root, coverage);
		fragmentroots.add(root);
		// getProjectCoverage(root.getJavaProject()).increment(coverage);
	}

	private CoverageNodeImpl getProjectCoverage(IV8Project project) {
		CoverageNodeImpl coverage = (CoverageNodeImpl) coveragemap.get(project);
		if (coverage == null) {
			// coverage = new CoverageNodeImpl(ElementType.GROUP, project.getElementName());
			// coveragemap.put(project, coverage);
			projects.add(project);
		}
		return coverage;
	}

	public void putFragment(URI element, ICoverageNode coverage) {
		coveragemap.put(element, coverage);
		fragments.add(element);
	}

	public void putType(URI element, ICoverageNode coverage) {
		coveragemap.put(element, coverage);
		types.add(element);
	}

	public void putClassFile(IClassFile element, ICoverageNode coverage) {
		// coveragemap.put(element, coverage);
	}

	public void putCompilationUnit(ICompilationUnit element, ICoverageNode coverage) {
		// coveragemap.put(element, coverage);
	}

	// IJavaModelCoverage interface

	public IJavaProject[] getProjects() {
		IJavaProject[] arr = new IJavaProject[projects.size()];
		return projects.toArray(arr);
	}

	public IPackageFragmentRoot[] getPackageFragmentRoots() {
		IPackageFragmentRoot[] arr = new IPackageFragmentRoot[fragmentroots.size()];
		return fragmentroots.toArray(arr);
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
		final ICoverageNode coverage = coveragemap.get(element);
		if (coverage != null) {
			return coverage;
		}
		if (IJavaElement.METHOD == element.getElementType()) {
			resolveMethods((IType) element.getParent());
			return coveragemap.get(element);
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
