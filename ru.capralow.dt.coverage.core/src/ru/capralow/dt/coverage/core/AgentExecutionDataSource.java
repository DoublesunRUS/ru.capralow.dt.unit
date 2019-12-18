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

import java.io.IOException;
import java.net.Socket;

import org.eclipse.core.runtime.CoreException;
import org.jacoco.core.data.IExecutionDataVisitor;
import org.jacoco.core.data.ISessionInfoVisitor;
import org.jacoco.core.runtime.RemoteControlReader;
import org.jacoco.core.runtime.RemoteControlWriter;

/**
 * {@link IExecutionDataSource} that receives execution data from a JaCoCo agent
 * via a TCP/IP connection.
 */
public class AgentExecutionDataSource implements IExecutionDataSource {

	private String address;
	private int port;
	private boolean reset;

	public AgentExecutionDataSource(final String address, final int port, final boolean reset) {
		this.address = address;
		this.port = port;
		this.reset = reset;
	}

	public void accept(IExecutionDataVisitor executionDataVisitor, ISessionInfoVisitor sessionInfoVisitor)
			throws CoreException {
		try {
			final Socket socket = new Socket(address, port);
			final RemoteControlWriter writer = new RemoteControlWriter(socket.getOutputStream());
			final RemoteControlReader reader = new RemoteControlReader(socket.getInputStream());
			reader.setExecutionDataVisitor(executionDataVisitor);
			reader.setSessionInfoVisitor(sessionInfoVisitor);
			writer.visitDumpCommand(true, reset);
			reader.read();
			socket.close();
		} catch (IOException e) {
			throw new CoreException(EclEmmaStatus.AGENT_CONNECT_ERROR.getStatus(address, Integer.valueOf(port), e));
		}
	}

}
