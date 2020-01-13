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
package ru.capralow.dt.coverage.internal.ui.annotation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModelEvent;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.jface.text.source.IAnnotationModelListener;
import org.eclipse.jface.text.source.IAnnotationModelListenerExtension;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.analysis.ILine;
import org.jacoco.core.analysis.ISourceNode;

import ru.capralow.dt.coverage.core.CoverageTools;
import ru.capralow.dt.coverage.core.analysis.IBslCoverageListener;
import ru.capralow.dt.coverage.internal.ui.CoverageUIPlugin;

/**
 * IAnnotationModel implementation for efficient coverage highlighting.
 */
public final class CoverageAnnotationModel implements IAnnotationModel {

	/** Key used to piggyback our model to the editor's model. */
	private static final Object KEY = new Object();

	/**
	 * Attaches a coverage annotation model for the given editor if the editor can
	 * be annotated. Does nothing if the model is already attached.
	 *
	 * @param editor
	 *            Editor to attach a annotation model to
	 */
	public static void attach(XtextEditor editor) {
		IDocumentProvider provider = editor.getDocumentProvider();
		// there may be text editors without document providers (SF #1725100)
		if (provider == null)
			return;
		IAnnotationModel model = provider.getAnnotationModel(editor.getEditorInput());
		if (!(model instanceof IAnnotationModelExtension))
			return;
		IAnnotationModelExtension modelex = (IAnnotationModelExtension) model;

		IDocument document = provider.getDocument(editor.getEditorInput());

		CoverageAnnotationModel coveragemodel = (CoverageAnnotationModel) modelex.getAnnotationModel(KEY);
		if (coveragemodel == null) {
			coveragemodel = new CoverageAnnotationModel(editor, document);
			modelex.addAnnotationModel(KEY, coveragemodel);
		}
	}

	/**
	 * Detaches the coverage annotation model from the given editor. If the editor
	 * does not have a model attached, this method does nothing.
	 *
	 * @param editor
	 *            Editor to detach the annotation model from
	 */
	public static void detach(XtextEditor editor) {
		IDocumentProvider provider = editor.getDocumentProvider();
		// there may be text editors without document providers (SF #1725100)
		if (provider == null)
			return;
		IAnnotationModel model = provider.getAnnotationModel(editor.getEditorInput());
		if (!(model instanceof IAnnotationModelExtension))
			return;
		IAnnotationModelExtension modelex = (IAnnotationModelExtension) model;
		modelex.removeAnnotationModel(KEY);
	}

	private static ISourceNode findSourceCoverageForElement(Object element) {
		IPath objectFullPath = ((IResource) element).getFullPath();

		URI uri = URI.createPlatformResourceURI(objectFullPath.toString(), false);
		uri = uri.appendFragment("/0"); //$NON-NLS-1$

		ICoverageNode coverage = CoverageTools.getCoverageInfo(uri);
		if (coverage instanceof ISourceNode) {
			return (ISourceNode) coverage;
		}
		return null;
	}

	/** List of current CoverageAnnotation objects */
	private List<CoverageAnnotation> annotations = new ArrayList<>(32);
	/** List of registered IAnnotationModelListener */
	private List<IAnnotationModelListener> annotationModelListeners = new ArrayList<>(2);

	private final XtextEditor editor;

	private final IDocument document;

	private int openConnections = 0;

	private boolean annotated = false;

	private IBslCoverageListener coverageListener = () -> updateAnnotations(true);

	private IDocumentListener documentListener = new IDocumentListener() {
		@Override
		public void documentAboutToBeChanged(DocumentEvent event) {
			// Нечего делать
		}

		@Override
		public void documentChanged(DocumentEvent event) {
			updateAnnotations(false);
		}
	};

	private CoverageAnnotationModel(XtextEditor editor, IDocument document) {
		this.editor = editor;
		this.document = document;
		updateAnnotations(true);
	}

	/**
	 * External modification is not supported.
	 */
	@Override
	public void addAnnotation(Annotation annotation, Position position) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addAnnotationModelListener(IAnnotationModelListener listener) {
		if (!annotationModelListeners.contains(listener)) {
			annotationModelListeners.add(listener);
			fireModelChanged(new AnnotationModelEvent(this, true));
		}
	}

	@Override
	public void connect(IDocument document2) {
		if (this.document != document2) {
			throw new IllegalArgumentException("Can't connect to different document.");
		}
		for (final CoverageAnnotation ca : annotations) {
			try {
				document2.addPosition(ca.getPosition());
			} catch (BadLocationException ex) {
				CoverageUIPlugin.log(ex);
			}
		}
		if (openConnections++ == 0) {
			CoverageTools.addBslCoverageListener(coverageListener);
			document2.addDocumentListener(documentListener);
		}
	}

	@Override
	public void disconnect(IDocument document2) {
		if (this.document != document2) {
			throw new IllegalArgumentException("Can't disconnect from different document.");
		}
		for (final CoverageAnnotation ca : annotations) {
			document2.removePosition(ca.getPosition());
		}
		if (--openConnections == 0) {
			CoverageTools.removeBslCoverageListener(coverageListener);
			document2.removeDocumentListener(documentListener);
		}
	}

	@Override
	public Iterator getAnnotationIterator() {
		return annotations.iterator();
	}

	@Override
	public Position getPosition(Annotation annotation) {
		if (annotation instanceof CoverageAnnotation)
			return ((CoverageAnnotation) annotation).getPosition();

		return null;
	}

	/**
	 * External modification is not supported.
	 */
	@Override
	public void removeAnnotation(Annotation annotation) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeAnnotationModelListener(IAnnotationModelListener listener) {
		annotationModelListeners.remove(listener);
	}

	private void clear() {
		AnnotationModelEvent event = new AnnotationModelEvent(this);
		clear(event);
		fireModelChanged(event);
	}

	private void clear(AnnotationModelEvent event) {
		for (final CoverageAnnotation ca : annotations) {
			event.annotationRemoved(ca, ca.getPosition());
		}
		annotations.clear();
	}

	private void createAnnotations(final ISourceNode linecoverage) {
		AnnotationModelEvent event = new AnnotationModelEvent(this);
		clear(event);
		final int firstline = linecoverage.getFirstLine();
		final int lastline = Math.min(linecoverage.getLastLine(), document.getNumberOfLines());
		try {
			for (int l = firstline; l <= lastline; l++) {
				final ILine line = linecoverage.getLine(l);
				if (line.getStatus() != ICounter.EMPTY) {
					final IRegion region = document.getLineInformation(l - 1);
					final CoverageAnnotation ca = new CoverageAnnotation(region.getOffset(), region.getLength(), line);
					annotations.add(ca);
					event.annotationAdded(ca);
				}
			}
		} catch (BadLocationException ex) {
			CoverageUIPlugin.log(ex);
		}
		fireModelChanged(event);
	}

	private ISourceNode findSourceCoverageForEditor() {
		if (editor.isDirty()) {
			return null;
		}
		final IEditorInput input = editor.getEditorInput();
		if (input == null)
			return null;

		final Object element = input.getAdapter(IResource.class);
		return findSourceCoverageForElement(element);
	}

	private void fireModelChanged(AnnotationModelEvent event) {
		event.markSealed();
		if (!event.isEmpty()) {
			for (final IAnnotationModelListener l : annotationModelListeners) {
				if (l instanceof IAnnotationModelListenerExtension) {
					((IAnnotationModelListenerExtension) l).modelChanged(event);
				} else {
					l.modelChanged(this);
				}
			}
		}
	}

	private void updateAnnotations(boolean force) {
		final ISourceNode coverage = findSourceCoverageForEditor();
		if (coverage != null) {
			if (!annotated || force) {
				createAnnotations(coverage);
				annotated = true;
			}
		} else {
			if (annotated) {
				clear();
				annotated = false;
			}
		}
	}

}
