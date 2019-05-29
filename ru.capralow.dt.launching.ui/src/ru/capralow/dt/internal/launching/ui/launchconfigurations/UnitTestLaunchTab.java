package ru.capralow.dt.internal.launching.ui.launchconfigurations;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com._1c.g5.v8.dt.core.platform.IExtensionProject;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.internal.launching.ui.LaunchingUiPlugin;
import com._1c.g5.v8.dt.internal.launching.ui.launchconfigurations.AbstractRuntimeClientTab;
import com._1c.g5.v8.dt.launching.core.ILaunchConfigurationAttributes;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.platform.services.ui.AutoCompleteComboViewer;
import com.google.inject.Inject;

import ru.capralow.dt.unit.launcher.plugin.core.UnitTestLaunchConfigurationAttributes;
import ru.capralow.dt.unit.launcher.plugin.core.frameworks.FrameworkUtils;
import ru.capralow.dt.unit.launcher.plugin.core.launchconfigurations.model.TestFramework;

public class UnitTestLaunchTab extends AbstractRuntimeClientTab
		implements SelectionListener, ISelectionChangedListener {

	private Collection<TestFramework> frameworks;

	private ComboViewer frameworkViewer;

	private Collection<CommonModule> modules;

	private ComboViewer moduleViewer;

	@Inject
	private IV8ProjectManager projectManager;
	private Collection<IProject> projects;

	private ComboViewer projectViewer;

	private Button runExtensionTests;

	private Button runModuleTests;

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Group(parent, SWT.NONE);
		setControl(composite);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), getHelpContextId());
		GridLayoutFactory.swtDefaults().applyTo(composite);
		composite.setFont(parent.getFont());

		createFeaturesSettings(composite);
		createFrameworkSettings(composite);

		updateLaunchConfigurationDialog();
	}

	@Override
	public String getName() {
		return "1CUnit";
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(UnitTestLaunchConfigurationAttributes.RUN_MODULE_TESTS,
				runModuleTests.getSelection());
		configuration.setAttribute(UnitTestLaunchConfigurationAttributes.RUN_EXTENSION_TESTS,
				runExtensionTests.getSelection());

		IProject project = getSelectedProject();
		configuration.setAttribute(UnitTestLaunchConfigurationAttributes.EXTENSION_PROJECT_TO_TEST,
				project == null ? null : project.getName());

		CommonModule commonModule = getSelectedModule();
		configuration.setAttribute(UnitTestLaunchConfigurationAttributes.EXTENSION_MODULE_TO_TEST,
				commonModule == null ? null : commonModule.getName());

		TestFramework framework = getSelectedFramework();
		configuration.setAttribute(UnitTestLaunchConfigurationAttributes.FRAMEWORK,
				framework == null ? null : framework.getName());

		configuration.setAttribute(ILaunchConfigurationAttributes.STARTUP_OPTION,
				framework == null ? null : "/Execute " + framework.getResourcePath());

	}

	@Override
	public void selectionChanged(SelectionChangedEvent arg0) {
		updateLaunchConfigurationDialog();
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(UnitTestLaunchConfigurationAttributes.RUN_MODULE_TESTS, true);
		configuration.setAttribute(UnitTestLaunchConfigurationAttributes.RUN_EXTENSION_TESTS, false);

		frameworks = FrameworkUtils.getFrameworks();
		if (frameworks != null) {
			String framework = ((TestFramework) frameworks.toArray()[0]).getName();
			configuration.setAttribute(UnitTestLaunchConfigurationAttributes.FRAMEWORK, framework);
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent event) {
		updateLaunchConfigurationDialog();
	}

	@Override
	public void widgetSelected(SelectionEvent event) {
		updateLaunchConfigurationDialog();
	}

	private void createFeaturesSettings(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(composite);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(composite);

		// 1.1
		Label launchProjectAll = new Label(composite, SWT.NONE);
		launchProjectAll.setText(Messages.UnitTestLaunchTab_ExtensionProject_to_Test);
		// 1.2
		projectViewer = new AutoCompleteComboViewer(composite);
		GridDataFactory.fillDefaults().align(4, 16777216).grab(true, false).hint(200, -1)
				.applyTo(projectViewer.getControl());
		projectViewer.setContentProvider(ArrayContentProvider.getInstance());
		projectViewer.setLabelProvider(new WorkbenchLabelProvider());
		projectViewer.setComparator(new ViewerComparator());
		ISelectionChangedListener projectViewerListener = event -> {
			StructuredSelection selection = (StructuredSelection) event.getSelection();
			if (!selection.isEmpty()) {
				IProject project = (IProject) selection.getFirstElement();
				modules = getModulesForProject(project);
				if (modules != null)
					moduleViewer.setInput(modules);

			}

			updateLaunchConfigurationDialog();
		};
		projectViewer.addSelectionChangedListener(projectViewerListener);

		// 2.1
		runExtensionTests = createRadioButton(composite, Messages.UnitTestLaunchTab_RunExtensionTests);
		// 2.2
		new Label(composite, SWT.NONE);

		// 3.1
		runModuleTests = createRadioButton(composite, Messages.UnitTestLaunchTab_RunModuleTests);
		runModuleTests.addSelectionListener(this);
		// 3.2
		new Label(composite, SWT.NONE);

		// 4.1
		Label launchModule = new Label(composite, SWT.NONE);
		launchModule.setText(Messages.UnitTestLaunchTab_ExtensionModule_to_Test);
		// 4.2
		moduleViewer = new AutoCompleteComboViewer(composite);
		GridDataFactory.fillDefaults().align(4, 16777216).grab(true, false).hint(200, -1)
				.applyTo(moduleViewer.getControl());
		moduleViewer.setContentProvider(ArrayContentProvider.getInstance());
		moduleViewer.setLabelProvider(new WorkbenchLabelProvider());
		moduleViewer.setComparator(new ViewerComparator());
		moduleViewer.addSelectionChangedListener(this);

	}

	private void createFrameworkSettings(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(composite);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(composite);

		// 1.1
		Label framework = new Label(composite, SWT.NONE);
		framework.setText(Messages.UnitTestLaunchTab_Framework);
		// 1.2
		frameworkViewer = new AutoCompleteComboViewer(composite);
		GridDataFactory.fillDefaults().align(4, 16777216).grab(true, false).hint(200, -1)
				.applyTo(frameworkViewer.getControl());
		frameworkViewer.setContentProvider(ArrayContentProvider.getInstance());
		frameworkViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element == null)
					return "";
				return element.toString();
			}
		});
		frameworkViewer.setComparator(new ViewerComparator());
		frameworkViewer.addSelectionChangedListener(this);
	}

	private void frameworkSetSelection(ILaunchConfiguration configuration) throws CoreException {
		TestFramework framework = FrameworkUtils.getFrameworkFromConfiguration(configuration, frameworks);

		frameworkViewer
				.setSelection(framework == null ? StructuredSelection.EMPTY : new StructuredSelection(framework));
	}

	private Collection<IProject> getExtensionProjects() {
		return projectManager.getProjects(IExtensionProject.class).stream().map(IV8Project::getProject)
				.collect(Collectors.toList());
	}

	private List<CommonModule> getModulesForProject(IProject project) {
		IV8Project v8Project = projectManager.getProject(project);
		if (!(v8Project instanceof IExtensionProject)) {
			String msg = Messages.UnitTestLaunchTab_WrongProjectClass;
			throw new NullPointerException(msg);
		}

		IExtensionProject extensionProject = (IExtensionProject) v8Project;

		return extensionProject.getConfiguration().getCommonModules().stream().collect(Collectors.toList());
	}

	private TestFramework getSelectedFramework() {
		IStructuredSelection selection = frameworkViewer.getStructuredSelection();
		return !selection.isEmpty() && selection.getFirstElement() instanceof TestFramework
				? (TestFramework) selection.getFirstElement()
				: null;
	}

	private CommonModule getSelectedModule() {
		IStructuredSelection selection = moduleViewer.getStructuredSelection();
		return !selection.isEmpty() && selection.getFirstElement() instanceof CommonModule
				? (CommonModule) selection.getFirstElement()
				: null;
	}

	private IProject getSelectedProject() {
		IStructuredSelection selection = projectViewer.getStructuredSelection();
		return !selection.isEmpty() && selection.getFirstElement() instanceof IProject
				? (IProject) selection.getFirstElement()
				: null;
	}

	private void moduleSetSelection(ILaunchConfiguration configuration) throws CoreException {
		IProject project = getSelectedProject();
		if (project == null)
			return;

		modules = getModulesForProject(project);
		if (modules == null)
			return;
		moduleViewer.setInput(modules);

		CommonModule module = null;
		Iterator<CommonModule> itrModules = modules.iterator();
		while (itrModules.hasNext()) {
			CommonModule candidate = itrModules.next();
			if (candidate.getName().equals(
					configuration.getAttribute(UnitTestLaunchConfigurationAttributes.EXTENSION_MODULE_TO_TEST, ""))) {

				module = candidate;
			}
		}
		moduleViewer.setSelection(module == null ? StructuredSelection.EMPTY : new StructuredSelection(module));

	}

	private void projectSetSelection(ILaunchConfiguration configuration) throws CoreException {
		if (projects == null)
			return;

		IProject project = null;
		Iterator<IProject> itrProjects = projects.iterator();
		while (itrProjects.hasNext()) {
			IProject candidate = itrProjects.next();
			if (candidate.getName().equals(
					configuration.getAttribute(UnitTestLaunchConfigurationAttributes.EXTENSION_PROJECT_TO_TEST, ""))) {

				project = candidate;
			}
		}
		projectViewer.setSelection(project == null ? StructuredSelection.EMPTY : new StructuredSelection(project));
	}

	@Override
	protected void doInitializeFrom(ILaunchConfiguration configuration) {
		projects = getExtensionProjects();
		if (projects != null)
			projectViewer.setInput(projects);

		frameworks = FrameworkUtils.getFrameworks();
		frameworkViewer.setInput(frameworks);

		try {
			runModuleTests.setSelection(
					configuration.getAttribute(UnitTestLaunchConfigurationAttributes.RUN_MODULE_TESTS, false));

			runExtensionTests.setSelection(
					configuration.getAttribute(UnitTestLaunchConfigurationAttributes.RUN_EXTENSION_TESTS, false));

			projectSetSelection(configuration);
			moduleSetSelection(configuration);

			frameworkSetSelection(configuration);

		} catch (CoreException e) {
			LaunchingUiPlugin.log(e);

		}

	}

}