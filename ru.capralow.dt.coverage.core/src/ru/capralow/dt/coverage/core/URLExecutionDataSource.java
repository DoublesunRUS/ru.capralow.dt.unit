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
package ru.capralow.dt.coverage.core;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.IExecutionDataVisitor;
import org.jacoco.core.data.ISessionInfoVisitor;

/**
 * {@link IExecutionDataSource} implementation based on a *.exec file obtained
 * from a URL.
 */
public class URLExecutionDataSource implements IExecutionDataSource {

	private final URL url;

	public URLExecutionDataSource(final URL url) {
		this.url = url;
	}

	public void accept(IExecutionDataVisitor executionDataVisitor, ISessionInfoVisitor sessionInfoVisitor)
			throws CoreException {
		try {
			final InputStream in = new BufferedInputStream(url.openStream());
			final ExecutionDataReader reader = new ExecutionDataReader(in);
			reader.setExecutionDataVisitor(executionDataVisitor);
			reader.setSessionInfoVisitor(sessionInfoVisitor);
			reader.read();
			in.close();
		} catch (IOException e) {
			throw new CoreException(EclEmmaStatus.EXEC_FILE_READ_ERROR.getStatus(url, e));
		}
	}

}
