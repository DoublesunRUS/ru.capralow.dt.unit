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

import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.internal.analysis.CounterImpl;

import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.metadata.mdclass.MdObject;
import com._1c.g5.v8.dt.metadata.mdclass.Subsystem;

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
		public ElementType getElementType() {
			return ElementType.GROUP;
		}

		@Override
		public String getName() {
			return "LOADING"; //$NON-NLS-1$
		}

		@Override
		public ICounter getInstructionCounter() {
			return CounterImpl.COUNTER_0_0;
		}

		@Override
		public ICounter getBranchCounter() {
			return CounterImpl.COUNTER_0_0;
		}

		@Override
		public ICounter getLineCounter() {
			return CounterImpl.COUNTER_0_0;
		}

		@Override
		public ICounter getComplexityCounter() {
			return CounterImpl.COUNTER_0_0;
		}

		@Override
		public ICounter getMethodCounter() {
			return CounterImpl.COUNTER_0_0;
		}

		@Override
		public ICounter getClassCounter() {
			return CounterImpl.COUNTER_0_0;
		}

		@Override
		public ICounter getCounter(CounterEntity entity) {
			return CounterImpl.COUNTER_0_0;
		}

		@Override
		public ICoverageNode getPlainCopy() {
			return this;
		}

		@Override
		public IV8Project[] getProjects() {
			return new IV8Project[0];
		}

		@Override
		public Subsystem[] getSubsystems() {
			return new Subsystem[0];
		}

		@Override
		public MdObject[] getMdObjects() {
			return new MdObject[0];
		}

		@Override
		public ICoverageNode getCoverageFor(IV8Project element) {
			return null;
		}

		@Override
		public ICoverageNode getCoverageFor(Subsystem element) {
			return null;
		}

		@Override
		public ICoverageNode getCoverageFor(MdObject element) {
			return null;
		}

		@Override
		public ICoverageNode getCoverageFor(Method element) {
			return null;
		}

		@Override
		public boolean containsCode() {
			return false;
		}
	};

	IV8Project[] getProjects();

	Subsystem[] getSubsystems();

	MdObject[] getMdObjects();

	ICoverageNode getCoverageFor(IV8Project element);

	ICoverageNode getCoverageFor(Subsystem element);

	ICoverageNode getCoverageFor(MdObject element);

	ICoverageNode getCoverageFor(Method element);
}
