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

import org.eclipse.core.runtime.IAdapterFactory;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.analysis.ISourceNode;

import com._1c.g5.v8.dt.metadata.mdclass.MdObject;

import ru.capralow.dt.coverage.core.CoverageTools;
import ru.capralow.dt.coverage.core.analysis.IBslModelCoverage;

/**
 * This factory adapts IResource and Method objects to the corresponding
 * coverage information of the current session. The factory is hooked into the
 * workbench through the extension point
 * <code>org.eclipse.core.runtime.adapters</code>.
 */
public class ModuleCoverageAdapterFactory implements IAdapterFactory {

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object getAdapter(Object object, Class adapterType) {
		IBslModelCoverage mc = CoverageTools.getBslModelCoverage();
		if (mc == null)
			return null;

		ICoverageNode coverage = mc.getCoverageFor((MdObject) object);
		if (adapterType.isInstance(coverage))
			return coverage;

		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {
		return new Class[] { ICoverageNode.class, ISourceNode.class };
	}

}
