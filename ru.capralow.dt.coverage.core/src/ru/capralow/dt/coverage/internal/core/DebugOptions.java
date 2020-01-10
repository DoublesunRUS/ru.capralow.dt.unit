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
package ru.capralow.dt.coverage.internal.core;

import java.io.PrintStream;
import java.text.MessageFormat;

import org.eclipse.core.runtime.Platform;

/**
 * Access to debug options and tracing facilities for this plug-in.
 */
public final class DebugOptions {

	/**
	 * Interface for optional trace output.
	 */
	public interface ITracer {

		/**
		 * Determines whether this tracer is enabled. Clients may use this method to
		 * avoid expensive calculation for debug output.
		 *
		 * @return <code>true</code> if the tracer is enabled
		 */
		boolean isEnabled();

		/**
		 * Prints the given debug message if the tracer is enabled.
		 *
		 * @param message
		 *            text message for trace output
		 */
		void trace(String message);

		/**
		 * Prints the given debug message if the tracer is enabled. The parameter object
		 * will be inserted for the <code>{x}</code> placeholder.
		 *
		 * @param message
		 *            text message for trace output
		 * @param param1
		 *            parameter object for inserting
		 */
		void trace(String message, Object param1);

		/**
		 * Prints the given debug message if the tracer is enabled. The parameter object
		 * wills be inserted for the <code>{x}</code> placeholder.
		 *
		 * @param message
		 *            text message for trace output
		 * @param param1
		 *            first parameter object for inserting
		 * @param param2
		 *            first parameter object for inserting
		 */
		void trace(String message, Object param1, Object param2);

		/**
		 * Prints the given debug message if the tracer is enabled. The parameter object
		 * wills be inserted for the <code>{x}</code> placeholder.
		 *
		 * @param message
		 *            text message for trace output
		 * @param param1
		 *            first parameter object for inserting
		 * @param param2
		 *            first parameter object for inserting
		 * @param param3
		 *            third parameter object for inserting
		 */
		void trace(String message, Object param1, Object param2, Object param3);

		/**
		 * Starts a timer for the calling thread.
		 */
		void startTimer();

		/**
		 * Prints out the elapsed time since starting the timer.
		 *
		 * @param message
		 *            identification for the timed period
		 */
		void stopTimer(String message);

		/**
		 * Start measuring heap memory usage.
		 */
		void startMemoryUsage();

		/**
		 * Print out heap memory usage since starting measurement.
		 *
		 * @param message
		 *            identification for this memory usage output
		 */
		void stopMemoryUsage(String message);

	}

	private static final ITracer NUL_TRACER = new ITracer() {

		@Override
		public boolean isEnabled() {
			return false;
		}

		@Override
		public void trace(String message) {
			// Нечего делать
		}

		@Override
		public void trace(String message, Object param1) {
			// Нечего делать
		}

		@Override
		public void trace(String message, Object param1, Object param2) {
			// Нечего делать
		}

		@Override
		public void trace(String message, Object param1, Object param2, Object param3) {
			// Нечего делать
		}

		@Override
		public void startTimer() {
			// Нечего делать
		}

		@Override
		public void stopTimer(String message) {
			// Нечего делать
		}

		@Override
		public void startMemoryUsage() {
			// Нечего делать
		}

		@Override
		public void stopMemoryUsage(String message) {
			// Нечего делать
		}
	};

	private static class PrintStreamTracer implements ITracer {

		private final PrintStream out;

		private final String channel;

		private final ThreadLocal<Long> starttime = new ThreadLocal<>();

		private final ThreadLocal<Long> heapsize = new ThreadLocal<>();

		PrintStreamTracer(String channel) {
			this(channel, System.out);
		}

		PrintStreamTracer(String channel, PrintStream out) {
			this.channel = channel;
			this.out = out;
		}

		@Override
		public boolean isEnabled() {
			return true;
		}

		@Override
		public void trace(String message) {
			out.print("["); //$NON-NLS-1$
			out.print(channel);
			out.print("] "); //$NON-NLS-1$
			out.println(message);
		}

		private void trace(String message, Object[] params) {
			trace(MessageFormat.format(message, params));
		}

		@Override
		public void trace(String message, Object param1) {
			trace(message, new Object[] { param1 });
		}

		@Override
		public void trace(String message, Object param1, Object param2) {
			trace(message, new Object[] { param1, param2 });
		}

		@Override
		public void trace(String message, Object param1, Object param2, Object param3) {
			trace(message, new Object[] { param1, param2, param3 });
		}

		@Override
		public void startTimer() {
			starttime.set(Long.valueOf(System.currentTimeMillis()));
		}

		@Override
		public void stopTimer(String message) {
			Long start = starttime.get();
			if (start == null) {
				trace("Timer {0} not startet.", message);
			} else {
				long time = System.currentTimeMillis() - start.longValue();
				trace("{0} ms for {1}", new Object[] { Long.valueOf(time), message }); //$NON-NLS-1$
			}
		}

		@Override
		public void startMemoryUsage() {
			Runtime rt = Runtime.getRuntime();
			heapsize.set(Long.valueOf(rt.totalMemory() - rt.freeMemory()));
		}

		@Override
		public void stopMemoryUsage(String message) {
			Long start = heapsize.get();
			if (start == null) {
				trace("Memory usage for {0} not started.", message);
			} else {
				Runtime rt = Runtime.getRuntime();
				long bytes = rt.totalMemory() - rt.freeMemory() - start.longValue();
				trace("{0} bytes for {1}", //$NON-NLS-1$
						new Object[] { Long.valueOf(bytes), message });
			}
		}
	}

	private static final String KEYPREFIX_DEBUG = CoverageCorePlugin.ID + "/debug/"; //$NON-NLS-1$

	private static ITracer getTracer(String channel) {
		String key = KEYPREFIX_DEBUG + channel;
		if (Boolean.parseBoolean(Platform.getDebugOption(key)))
			return new PrintStreamTracer(channel);

		return NUL_TRACER;
	}

	public static final ITracer PERFORMANCETRACER = getTracer("performance"); //$NON-NLS-1$

	public static final ITracer ANALYSISTRACER = getTracer("analysis"); //$NON-NLS-1$

	private DebugOptions() {
		// no instances
	}

}
