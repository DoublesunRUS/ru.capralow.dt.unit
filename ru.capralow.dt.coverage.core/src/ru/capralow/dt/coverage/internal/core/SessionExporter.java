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

import static ru.capralow.dt.coverage.core.ISessionExporter.ExportFormat.EXEC;
import static ru.capralow.dt.coverage.core.ISessionExporter.ExportFormat.HTML;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.osgi.util.NLS;
import org.jacoco.core.data.ExecutionDataWriter;
import org.jacoco.report.FileMultiReportOutput;
import org.jacoco.report.IReportGroupVisitor;
import org.jacoco.report.IReportVisitor;
import org.jacoco.report.ISourceFileLocator;
import org.jacoco.report.ZipMultiReportOutput;
import org.jacoco.report.csv.CSVFormatter;
import org.jacoco.report.html.HTMLFormatter;
import org.jacoco.report.xml.XMLFormatter;

import ru.capralow.dt.coverage.core.CoverageStatus;
import ru.capralow.dt.coverage.core.ICoverageSession;
import ru.capralow.dt.coverage.core.ISessionExporter;
import ru.capralow.dt.coverage.core.analysis.IBslModelCoverage;
import ru.capralow.dt.coverage.internal.core.analysis.SessionAnalyzer;

/**
 * Implementation of ISessionExporter.
 */
public class SessionExporter implements ISessionExporter {

	private abstract static class AbstractSourceFileLocator implements ISourceFileLocator {

		protected final URI root;
		private final int tabWidth;

		public AbstractSourceFileLocator(URI root) {
			this.root = root;
			this.tabWidth = 100;
			// final Map<?, ?> options = root.getJavaProject().getOptions(true);
			// this.tabWidth = IndentManipulation.getTabWidth(options);
		}

		@Override
		public final Reader getSourceFile(String packagename, String sourcename) throws IOException {
			// try {
			// packagename = packagename.replace('/', '.');
			// final URI pkg = root.getPackageFragment(packagename);
			// final String source = getSourceReference(pkg, sourcename).getSource();
			// if (source != null)
			// return new StringReader(source);

			return null;

			// } catch (CoreException e) {
			// final IOException ioException = new IOException(e.getMessage());
			// throw (IOException) ioException.initCause(e);
			// }
		}

		@Override
		public final int getTabWidth() {
			return tabWidth;
		}

		protected abstract URI getSourceReference(URI pkg, String sourcename) throws CoreException;

	}

	private static class LibrarySourceFileLocator extends AbstractSourceFileLocator {

		public LibrarySourceFileLocator(URI root) {
			super(root);
		}

		@Override
		protected URI getSourceReference(URI pkg, String sourcename) throws CoreException {
			return null;
			// int idx = sourcename.lastIndexOf('.');
			// if (idx != -1) {
			// sourcename = sourcename.substring(0, idx);
			// }
			// return pkg.getClassFile(sourcename + ".class"); //$NON-NLS-1$
		}
	}

	private static class SourceFolderSourceFileLocator extends AbstractSourceFileLocator {

		public SourceFolderSourceFileLocator(URI root) {
			super(root);
		}

		@Override
		protected URI getSourceReference(URI pkg, String sourcename) throws CoreException {
			return null;
			// return pkg.getCompilationUnit(sourcename);
		}

	}

	private static ISourceFileLocator createSourceFileLocator(URI root) {
		// if (root.getKind() == IPackageFragmentRoot.K_SOURCE)
		// return new SourceFolderSourceFileLocator(root);

		return new LibrarySourceFileLocator(root);
	}

	private final ICoverageSession session;

	private ExportFormat format;

	private String destination;

	public SessionExporter(ICoverageSession session) {
		this.session = session;
	}

	@Override
	public void export(IProgressMonitor monitor) throws CoreException {
		try {
			if (EXEC.equals(format)) {
				createExecFile(monitor);
			} else {
				createReport(monitor);
			}
		} catch (IOException e) {
			throw new CoreException(CoverageStatus.EXPORT_ERROR.getStatus(e));
		}
	}

	@Override
	public void setDestination(String filename) {
		this.destination = filename;
	}

	@Override
	public void setFormat(ExportFormat format) {
		this.format = format;
	}

	private void createExecFile(IProgressMonitor monitor) throws IOException, CoreException {
		monitor.beginTask(NLS.bind(CoreMessages.ExportingSession_task, session.getDescription()), 1);
		final OutputStream out = new BufferedOutputStream(new FileOutputStream(destination));
		final ExecutionDataWriter writer = new ExecutionDataWriter(out);
		session.accept(writer, writer);
		out.close();
		monitor.done();
	}

	private IReportVisitor createFormatter() throws IOException {
		final File file = new File(destination);
		if (HTML.equals(format)) {
			HTMLFormatter htmlFormatter = new HTMLFormatter();
			htmlFormatter.setFooterText(session.getDescription());
			return htmlFormatter.createVisitor(new FileMultiReportOutput(file));
		}
		final OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		switch (format) {
		case HTMLZIP:
			final HTMLFormatter htmlFormatter = new HTMLFormatter();
			htmlFormatter.setFooterText(session.getDescription());
			return htmlFormatter.createVisitor(new ZipMultiReportOutput(out));
		case XML:
			final XMLFormatter xmlFormatter = new XMLFormatter();
			return xmlFormatter.createVisitor(out);
		case CSV:
			final CSVFormatter csvFormatter = new CSVFormatter();
			return csvFormatter.createVisitor(out);
		default:
			out.close();
			throw new AssertionError("Unexpected format " + format); //$NON-NLS-1$
		}
	}

	private void createReport(IProgressMonitor monitor) throws CoreException, IOException {
		final int work = session.getScope().size();
		monitor.beginTask(NLS.bind(CoreMessages.ExportingSession_task, session.getDescription()), work * 2);
		final SessionAnalyzer analyzer = new SessionAnalyzer();
		final IBslModelCoverage modelCoverage = analyzer.processSession(session, new SubProgressMonitor(monitor, work));
		final IReportVisitor formatter = createFormatter();
		formatter.visitInfo(analyzer.getSessionInfos(), analyzer.getExecutionData());
		final IReportGroupVisitor modelgroup = formatter.visitGroup(session.getDescription());
		// for (IV8Project project : modelCoverage.getProjects()) {
		// final IReportGroupVisitor projectgroup =
		// modelgroup.visitGroup(project.getElementName());
		// for (IPackageFragmentRoot root : project.getPackageFragmentRoots()) {
		// final IBundleCoverage coverage = (IBundleCoverage)
		// modelCoverage.getCoverageFor(root);
		// if (coverage != null) {
		// projectgroup.visitBundle(coverage, createSourceFileLocator(root));
		// monitor.worked(1);
		// }
		// }
		// }
		formatter.visitEnd();
		monitor.done();
	}

}
