package ru.capralow.dt.coverage.internal.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.manipulation.search.IOccurrencesFinder.OccurrenceLocation;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.actions.ActionMessages;
import org.eclipse.jdt.internal.ui.actions.ActionUtil;
import org.eclipse.jdt.internal.ui.actions.SelectionConverter;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.javaeditor.JavaElementHyperlinkDetector;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.actions.SelectionDispatchAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IEditorStatusLine;

import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.ui.util.OpenHelper;

import ru.capralow.dt.coverage.internal.ui.CoverageUIPlugin;

/**
 * This action opens a Java editor on a Java element or file.
 * <p>
 * The action is applicable to selections containing elements of type
 * <code>ICompilationUnit</code>, <code>IMember</code> or <code>IFile</code>.
 *
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 *
 * @since 2.0
 *
 * @noextend This class is not intended to be subclassed by clients.
 */
public class OpenAction extends SelectionDispatchAction {

	private JavaEditor fEditor;

	private IResourceLookup resourceLookup;

	/**
	 * Creates a new <code>OpenAction</code>. The action requires that the selection
	 * provided by the site's selection provider is of type <code>
	 * org.eclipse.jface.viewers.IStructuredSelection</code>.
	 *
	 * @param site
	 *            the site providing context information for this action
	 */
	public OpenAction(IWorkbenchSite site) {
		super(site);
		setText(ActionMessages.OpenAction_label);
		setToolTipText(ActionMessages.OpenAction_tooltip);
		setDescription(ActionMessages.OpenAction_description);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.OPEN_ACTION);

		resourceLookup = CoverageUIPlugin.getInstance().getInjector().getInstance(IResourceLookup.class);
	}

	/**
	 * Note: This constructor is for internal use only. Clients should not call this
	 * constructor.
	 *
	 * @param editor
	 *            the Java editor
	 *
	 * @noreference This constructor is not intended to be referenced by clients.
	 */
	public OpenAction(JavaEditor editor) {
		this(editor.getEditorSite());
		fEditor = editor;
		setText(ActionMessages.OpenAction_declaration_label);
		setEnabled(EditorUtility.getEditorInputJavaElement(fEditor, false) != null);
	}

	@Override
	public void run(IStructuredSelection selection) {
		if (!checkEnabled(selection))
			return;
		run(selection.toArray());
	}

	@Override
	public void run(ITextSelection selection) {
		ITypeRoot input = EditorUtility.getEditorInputJavaElement(fEditor, false);
		if (input == null) {
			setStatusLineMessage();
			return;
		}
		IRegion region = new Region(selection.getOffset(), selection.getLength());
		OccurrenceLocation location = JavaElementHyperlinkDetector.findBreakOrContinueTarget(input, region);
		if (location != null) {
			fEditor.selectAndReveal(location.getOffset(), location.getLength());
			return;
		}
		location = JavaElementHyperlinkDetector.findSwitchCaseTarget(input, region);
		if (location != null) {
			fEditor.selectAndReveal(location.getOffset(), location.getLength());
			return;
		}
		try {
			IJavaElement[] elements = SelectionConverter.codeResolveForked(fEditor, false);
			elements = selectOpenableElements(elements);
			if (elements == null || elements.length == 0) {
				if (!ActionUtil.isProcessable(fEditor))
					return;
				setStatusLineMessage();
				return;
			}

			IJavaElement element = elements[0];
			if (elements.length > 1) {
				if (needsUserSelection(elements, input)) {
					element = SelectionConverter.selectJavaElement(elements,
							getShell(),
							getDialogTitle(),
							ActionMessages.OpenAction_select_element);
					if (element == null)
						return;
				}
			}

			run(new Object[] { element });
		} catch (InvocationTargetException e) {
			ExceptionHandler.handle(e, getShell(), getDialogTitle(), ActionMessages.OpenAction_error_message);
		} catch (InterruptedException e) {
			// ignore
		}
	}

	/**
	 * Note: this method is for internal use only. Clients should not call this
	 * method.
	 *
	 * @param elements
	 *            the elements to process
	 *
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public void run(Object[] elements) {
		if (elements == null)
			return;

		MultiStatus status = new MultiStatus(JavaUI.ID_PLUGIN,
				IStatus.OK,
				ActionMessages.OpenAction_multistatus_message,
				null);

		for (int i = 0; i < elements.length; i++) {
			Object element = elements[i];
			EObject bslElement = (EObject) element;

			URI uri = resourceLookup.getPlatformResourceUri(bslElement);

			OpenHelper openHelper = new OpenHelper();
			openHelper.openEditor(uri, null);
		}
		if (!status.isOK()) {
			IStatus[] children = status.getChildren();
			ErrorDialog.openError(getShell(),
					getDialogTitle(),
					ActionMessages.OpenAction_error_message,
					children.length == 1 ? children[0] : status);
		}
	}

	@Override
	public void selectionChanged(IStructuredSelection selection) {
		setEnabled(checkEnabled(selection));
	}

	@Override
	public void selectionChanged(ITextSelection selection) {
		// Нечего делать
	}

	private boolean checkEnabled(IStructuredSelection selection) {
		if (selection.isEmpty())
			return false;
		for (Iterator<?> iter = selection.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (element instanceof CommonModule)
				continue;
			return false;
		}
		return true;
	}

	private String getDialogTitle() {
		return ActionMessages.OpenAction_error_title;
	}

	private boolean needsUserSelection(IJavaElement[] elements, ITypeRoot input) {
		if (elements[0] instanceof IPackageFragment) {
			IJavaProject javaProject = input.getJavaProject();
			if (JavaModelUtil.is9OrHigher(javaProject)) {
				try {
					if (javaProject.getModuleDescription() != null) {
						for (IJavaElement element : elements) {
							IPackageFragmentRoot root = (IPackageFragmentRoot) element
									.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
							if (root.getModuleDescription() != null)
								return true;
						}
					}
				} catch (JavaModelException e) {
					// silent
				}
			}
			// below 9 or with no modules in the picture:
			// if there are multiple IPackageFragments that could be selected, use the first
			// one on the build path.
			return false;
		}
		return true;
	}

	/**
	 * Selects the openable elements out of the given ones.
	 *
	 * @param elements
	 *            the elements to filter
	 * @return the openable elements
	 * @since 3.4
	 */
	private IJavaElement[] selectOpenableElements(IJavaElement[] elements) {
		List<IJavaElement> result = new ArrayList<>(elements.length);
		for (int i = 0; i < elements.length; i++) {
			IJavaElement element = elements[i];
			switch (element.getElementType()) {
			case IJavaElement.PACKAGE_DECLARATION:
			case IJavaElement.PACKAGE_FRAGMENT_ROOT:
			case IJavaElement.JAVA_PROJECT:
			case IJavaElement.JAVA_MODEL:
				break;
			default:
				result.add(element);
				break;
			}
		}
		return result.toArray(new IJavaElement[result.size()]);
	}

	/**
	 * Sets the error message in the status line.
	 *
	 * @since 3.7
	 */
	private void setStatusLineMessage() {
		IEditorStatusLine statusLine = fEditor.getAdapter(IEditorStatusLine.class);
		if (statusLine != null)
			statusLine.setMessage(true, ActionMessages.OpenAction_error_messageBadSelection, null);
		getShell().getDisplay().beep();
		return;
	}
}
