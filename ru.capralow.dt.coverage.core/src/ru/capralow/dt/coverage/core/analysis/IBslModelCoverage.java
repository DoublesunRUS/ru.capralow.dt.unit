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
package ru.capralow.dt.coverage.core.analysis;

import org.eclipse.emf.common.util.URI;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.analysis.ISourceNode;

import ru.capralow.dt.coverage.internal.core.analysis.CounterImpl;

/**
 * The interface for coverage information attached to the Java model. It allows
 * to retrieve coverage information for any Java model element and holds lists
 * of entry points.
 */
public interface IBslModelCoverage extends ICoverageNode {

	/**
	 * This instance is used to indicate that a coverage session is currently
	 * loading.
	 */
	IBslModelCoverage LOADING = new IBslModelCoverage() {

		@Override
		public boolean containsCode() {
			return false;
		}

		@Override
		public ICounter getBranchCounter() {
			return CounterImpl.COUNTER_0_0;
		}

		@Override
		public ICounter getClassCounter() {
			return CounterImpl.COUNTER_0_0;
		}

		@Override
		public ICounter getComplexityCounter() {
			return CounterImpl.COUNTER_0_0;
		}

		@Override
		public ICounter getCounter(CounterEntity entity) {
			return CounterImpl.COUNTER_0_0;
		}

		@Override
		public ISourceNode getCoverageFor(String methodName, URI module) {
			return null;
		}

		@Override
		public ISourceNode getCoverageFor(URI element) {
			return null;
		}

		@Override
		public ElementType getElementType() {
			return ElementType.GROUP;
		}

		@Override
		public ICounter getInstructionCounter() {
			return CounterImpl.COUNTER_0_0;
		}

		@Override
		public ICounter getLineCounter() {
			return CounterImpl.COUNTER_0_0;
		}

		@Override
		public URI[] getModules() {
			return new URI[0];
		}

		@Override
		public ICounter getMethodCounter() {
			return CounterImpl.COUNTER_0_0;
		}

		@Override
		public String getName() {
			return "LOADING"; //$NON-NLS-1$
		}

		@Override
		public ICoverageNode getPlainCopy() {
			return this;
		}

		@Override
		public URI[] getProjects() {
			return new URI[0];
		}

		@Override
		public URI[] getSubsystems() {
			return new URI[0];
		}
	};

	ISourceNode getCoverageFor(String methodName, URI module);

	ISourceNode getCoverageFor(URI element);

	URI[] getModules();

	URI[] getProjects();

	URI[] getSubsystems();
}
