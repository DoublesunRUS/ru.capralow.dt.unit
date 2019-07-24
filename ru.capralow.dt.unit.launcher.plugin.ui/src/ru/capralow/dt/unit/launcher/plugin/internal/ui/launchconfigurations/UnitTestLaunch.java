package ru.capralow.dt.unit.launcher.plugin.internal.ui.launchconfigurations;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.junit.JUnitCore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.core.instr.Instrumenter;
import org.jacoco.core.runtime.IRuntime;
import org.jacoco.core.runtime.LoggerRuntime;
import org.jacoco.core.runtime.RuntimeData;

import com._1c.g5.v8.dt.debug.model.base.data.BSLModuleType;
import com._1c.g5.v8.dt.profiling.core.ILineProfilingResult;
import com._1c.g5.v8.dt.profiling.core.IProfilingResult;
import com.google.common.base.Strings;
import com.mountainminds.eclemma.core.CoverageTools;
import com.mountainminds.eclemma.core.ISessionImporter;
import com.mountainminds.eclemma.core.ISessionManager;
import com.mountainminds.eclemma.core.URLExecutionDataSource;

import ru.capralow.dt.unit.launcher.plugin.core.UnitTestLaunchConfigurationAttributes;
import ru.capralow.dt.unit.launcher.plugin.core.frameworks.FrameworkUtils;
import ru.capralow.dt.unit.launcher.plugin.internal.ui.UnitLauncherUiPlugin;

public class UnitTestLaunch {

	public static class MemoryClassLoader extends ClassLoader {

		private final Map<String, byte[]> definitions = new HashMap<>();

		/**
		 * Add a in-memory representation of a class.
		 * 
		 * @param name
		 *            name of the class
		 * @param bytes
		 *            class definition
		 */
		public void addDefinition(final String name, final byte[] bytes) {
			definitions.put(name, bytes);
		}

		@Override
		protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
			final byte[] bytes = definitions.get(name);
			if (bytes != null) {
				return defineClass(name, bytes, 0, bytes.length);
			}
			return super.loadClass(name, resolve);
		}

	}

	private static PrintStream out;

	public static void showCoverageResult(List<IProfilingResult> profilingResults) throws Exception {
		Set<IPackageFragmentRoot> scope = new HashSet<>();

		for (IProfilingResult profilingResult : profilingResults) {
			for (ILineProfilingResult result : profilingResult.getProfilingResults()) {
				if (result.getModuleID().getType() == BSLModuleType.EXT_MD_MODULE)
					continue;

				result.getLine();

				IJavaProject javaProject = JavaCore.create(result.getProject());

				// for (IPackageFragmentRoot packageFragmentRoot :
				// javaProject.getAllPackageFragmentRoots())
				// scope.add(packageFragmentRoot);
			}
		}

		out = System.out;

		final String targetName = UnitTestLaunch.class.getName();

		// For instrumentation and runtime we need a IRuntime instance
		// to collect execution data:
		final IRuntime runtime = new LoggerRuntime();

		// The Instrumenter creates a modified version of our test target class
		// that contains additional probes for execution data recording:
		final Instrumenter instr = new Instrumenter(runtime);
		InputStream original = getTargetClass(targetName);
		final byte[] instrumented = instr.instrument(original, targetName);
		original.close();

		// Now we're ready to run our instrumented class and need to startup the
		// runtime first:
		final RuntimeData data = new RuntimeData();
		runtime.startup(data);

		// In this tutorial we use a special class loader to directly load the
		// instrumented class definition from a byte[] instances.
		final MemoryClassLoader memoryClassLoader = new MemoryClassLoader();
		memoryClassLoader.addDefinition(targetName, instrumented);
		final Class<?> targetClass = memoryClassLoader.loadClass(targetName);

		// At the end of test execution we collect execution data and shutdown
		// the runtime:
		final ExecutionDataStore executionData = new ExecutionDataStore();
		final SessionInfoStore sessionInfos = new SessionInfoStore();
		data.collect(executionData, sessionInfos, false);
		runtime.shutdown();

		// Together with the original class definition we can calculate coverage
		// information:
		final CoverageBuilder coverageBuilder = new CoverageBuilder();
		final Analyzer analyzer = new Analyzer(executionData, coverageBuilder);
		original = getTargetClass(targetName);
		analyzer.analyzeClass(original, targetName);
		original.close();

		// Let's dump some metrics and line coverage information:
		/*
		 * for (final IClassCoverage cc : coverageBuilder.getClasses()) {
		 * out.printf("Coverage of class %s%n", cc.getName());
		 * 
		 * printCounter("instructions", cc.getInstructionCounter());
		 * printCounter("branches", cc.getBranchCounter()); printCounter("lines",
		 * cc.getLineCounter()); printCounter("methods", cc.getMethodCounter());
		 * printCounter("complexity", cc.getComplexityCounter());
		 * 
		 * for (int i = cc.getFirstLine(); i <= cc.getLastLine(); i++) {
		 * out.printf("Line %s: %s%n", Integer.valueOf(i),
		 * getColor(cc.getLine(i).getStatus())); } }
		 */

		ISessionImporter importer = CoverageTools.getImporter();

		importer.setDescription(targetName);

		java.net.URL dataURL = new java.net.URL("file:/Users/kapral/Разработка/1Unit.exec"); //$NON-NLS-1$
		URLExecutionDataSource dataSource = new URLExecutionDataSource(dataURL);
		importer.setExecutionDataSource(dataSource);

		importer.setScope(scope);

		NullProgressMonitor monitor = new NullProgressMonitor();
		importer.importSession(monitor);

		ISessionManager sessionManager = CoverageTools.getSessionManager();
		sessionManager.activateSession(sessionManager.getActiveSession());

		Display.getDefault().asyncExec(() -> {
			String panelId = "com.mountainminds.eclemma.ui.CoverageView"; //$NON-NLS-1$
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(panelId);

			} catch (PartInitException e) {
				String msg = MessageFormat.format(Messages.UnitTestLaunch_Unable_to_show_panel_0, panelId);
				UnitLauncherUiPlugin.log(UnitLauncherUiPlugin.createErrorStatus(msg, e));

			}
		});
	}

	public static void showJUnitResult(IProcess process) {
		if (process.getLabel().contains("dbgs")) //$NON-NLS-1$
			return;

		try {
			ILaunchConfiguration configuration = process.getLaunch().getLaunchConfiguration();

			String extensionProjectName = configuration
					.getAttribute(UnitTestLaunchConfigurationAttributes.EXTENSION_PROJECT_TO_TEST, (String) null);
			if (Strings.isNullOrEmpty(extensionProjectName))
				return;

			String paramsFilePathName = FrameworkUtils.getConfigurationFilesPath(configuration);

			File file = new File(paramsFilePathName + File.separator + "junit.xml"); //$NON-NLS-1$
			if (!file.exists()) {
				String msg = MessageFormat.format(Messages.UnitTestLaunch_Unable_to_find_junit_xml_file_0,
						file.getPath());
				UnitLauncherUiPlugin.log(UnitLauncherUiPlugin.createErrorStatus(msg));
				return;
			}

			JUnitCore.importTestRunSession(file);

			Display.getDefault().asyncExec(() -> {
				String panelId = "org.eclipse.jdt.junit.ResultView"; //$NON-NLS-1$
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(panelId);

				} catch (PartInitException e) {
					String msg = MessageFormat.format(Messages.UnitTestLaunch_Unable_to_show_panel_0, panelId);
					UnitLauncherUiPlugin.log(UnitLauncherUiPlugin.createErrorStatus(msg, e));

				}
			});

			Files.deleteIfExists(file.toPath());

		} catch (CoreException | IOException e) {
			UnitLauncherUiPlugin.log(
					UnitLauncherUiPlugin.createErrorStatus(Messages.UnitTestLaunch_Unable_to_read_junit_xml_file, e));

		}
	}

	private static String getColor(final int status) {
		switch (status) {
		case ICounter.NOT_COVERED:
			return "red";
		case ICounter.PARTLY_COVERED:
			return "yellow";
		case ICounter.FULLY_COVERED:
			return "green";
		}
		return "";
	}

	private static InputStream getTargetClass(final String name) {
		final String resource = '/' + name.replace('.', '/') + ".class";
		return UnitTestLaunch.class.getResourceAsStream(resource);
	}

	private static void printCounter(final String unit, final ICounter counter) {
		final Integer missed = Integer.valueOf(counter.getMissedCount());
		final Integer total = Integer.valueOf(counter.getTotalCount());
		out.printf("%s of %s %s missed%n", missed, total, unit);
	}

	private UnitTestLaunch() {
		throw new IllegalStateException(Messages.UnitTestLaunch_Internal_class);
	}
}
