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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;

import ru.capralow.dt.coverage.internal.core.CoreMessages;
import ru.capralow.dt.coverage.internal.core.CoverageCorePlugin;

/**
 * Status objects used by the core plug-in.
 */
public final class CoverageStatus {

	final int code;

	final int severity;

	final String message;

	private CoverageStatus(int code, int severity, String message) {
		this.code = code;
		this.severity = severity;
		this.message = message;
	}

	public IStatus getStatus() {
		String m = NLS.bind(message, Integer.valueOf(code));
		return new Status(severity, CoverageCorePlugin.ID, code, m, null);
	}

	public IStatus getStatus(Throwable t) {
		String m = NLS.bind(message, Integer.valueOf(code));
		return new Status(severity, CoverageCorePlugin.ID, code, m, t);
	}

	public IStatus getStatus(Object param1, Throwable t) {
		String m = NLS.bind(message, Integer.valueOf(code), param1);
		return new Status(severity, CoverageCorePlugin.ID, code, m, t);
	}

	public IStatus getStatus(Object param1, Object param2, Throwable t) {
		String m = NLS.bind(message, new Object[] { Integer.valueOf(code), param1, param2 });
		return new Status(severity, CoverageCorePlugin.ID, code, m, t);
	}

	public IStatus getStatus(Object param1) {
		String m = NLS.bind(message, Integer.valueOf(code), param1);
		return new Status(severity, CoverageCorePlugin.ID, code, m, null);
	}

	/**
	 * Status indicating that it was not possible to obtain a local version of the
	 * runtime agent file.
	 */
	public static final CoverageStatus NO_LOCAL_AGENTJAR_ERROR = new CoverageStatus(5000,
			IStatus.ERROR,
			CoreMessages.StatusNO_LOCAL_AGENTJAR_ERROR_message);

	/**
	 * Error while loading a coverage session.
	 */
	public static final CoverageStatus SESSION_LOAD_ERROR = new CoverageStatus(5001,
			IStatus.ERROR,
			CoreMessages.StatusSESSION_LOAD_ERROR_message);

	/**
	 * The requested launch type is not known.
	 */
	public static final CoverageStatus UNKOWN_LAUNCH_TYPE_ERROR = new CoverageStatus(5002,
			IStatus.ERROR,
			CoreMessages.StatusUNKOWN_LAUNCH_TYPE_ERROR_message);

	/**
	 * Error while merging sessions.
	 */
	public static final CoverageStatus MERGE_SESSIONS_ERROR = new CoverageStatus(5003,
			IStatus.ERROR,
			CoreMessages.StatusMERGE_SESSIONS_ERROR_message);

	/**
	 * The execution data file can not be created.
	 */
	public static final CoverageStatus EXEC_FILE_CREATE_ERROR = new CoverageStatus(5004,
			IStatus.ERROR,
			CoreMessages.StatusEXEC_FILE_CREATE_ERROR_message);

	/**
	 * Error while reading coverage data file.
	 */
	public static final CoverageStatus EXEC_FILE_READ_ERROR = new CoverageStatus(5005,
			IStatus.ERROR,
			CoreMessages.StatusEXEC_FILE_READ_ERROR_message);

	/**
	 * Error while reading coverage data file.
	 */
	public static final CoverageStatus AGENT_CONNECT_ERROR = new CoverageStatus(5006,
			IStatus.ERROR,
			CoreMessages.StatusAGENT_CONNECT_ERROR_message);

	/**
	 * Error while analyzing a bundle of class file.
	 */
	public static final CoverageStatus BUNDLE_ANALYSIS_ERROR = new CoverageStatus(5007,
			IStatus.ERROR,
			CoreMessages.StatusBUNDLE_ANALYSIS_ERROR_message);

	/**
	 * Error while extracting coverage session.
	 */
	public static final CoverageStatus EXPORT_ERROR = new CoverageStatus(5008,
			IStatus.ERROR,
			CoreMessages.StatusEXPORT_ERROR_message);

	/**
	 * Error while starting the agent server.
	 */
	public static final CoverageStatus AGENTSERVER_START_ERROR = new CoverageStatus(5011,
			IStatus.ERROR,
			CoreMessages.StatusAGENTSERVER_START_ERROR_message);

	/**
	 * Error while stopping the agent server.
	 */
	public static final CoverageStatus AGENTSERVER_STOP_ERROR = new CoverageStatus(5012,
			IStatus.ERROR,
			CoreMessages.StatusAGENTSERVER_STOP_ERROR_message);

	/**
	 * Error while dumping coverage data.
	 */
	public static final CoverageStatus EXECDATA_DUMP_ERROR = new CoverageStatus(5013,
			IStatus.ERROR,
			CoreMessages.StatusEXECDATA_DUMP_ERROR_message);

	/**
	 * Error while requesting an execution data dump.
	 */
	public static final CoverageStatus DUMP_REQUEST_ERROR = new CoverageStatus(5014,
			IStatus.ERROR,
			CoreMessages.StatusDUMP_REQUEST_ERROR_message);

	/**
	 * No coverage data file has been created during a coverage launch. This status
	 * is used to issue an error prompt.
	 */
	public static final CoverageStatus NO_COVERAGE_DATA_ERROR = new CoverageStatus(5101,
			IStatus.ERROR,
			CoreMessages.StatusNO_COVERAGE_DATA_ERROR_message);

}
