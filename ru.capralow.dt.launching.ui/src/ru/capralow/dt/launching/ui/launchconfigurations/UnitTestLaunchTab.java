package ru.capralow.dt.launching.ui.launchconfigurations;

import java.util.Collection;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com._1c.g5.v8.dt.core.platform.IExtensionProject;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.internal.launching.ui.launchconfigurations.AbstractRuntimeClientTab;
import com._1c.g5.v8.dt.platform.services.ui.AutoCompleteComboViewer;
import com.google.inject.Inject;

public class UnitTestLaunchTab extends AbstractRuntimeClientTab implements SelectionListener {

	private Button runSingleTest;

	private Button runAllTests;

	private ComboViewer projectViewer;

	private ISelectionChangedListener projectViewerListener;

	@Inject
	private IV8ProjectManager projectManager;
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
	public void performApply(ILaunchConfigurationWorkingCopy arg0) {
		// TODO Автоматически созданная заглушка метода

	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// TODO Автоматически созданная заглушка метода

	}

	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {
		updateLaunchConfigurationDialog();
	}

	@Override
	public void widgetSelected(SelectionEvent arg0) {
		updateLaunchConfigurationDialog();
	}

	private void createFeaturesSettings(Composite parent) {
		runSingleTest = createRadioButton(parent, "Запустить один тест");
		runSingleTest.addSelectionListener(this);
		runSingleTest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				if (((Button) event.getSource()).getSelection()) {
					// TODO Автоматически созданная заглушка метода
				}
			}
		});

		projectViewer = new AutoCompleteComboViewer(parent);
		GridDataFactory.fillDefaults().align(4, 16777216).grab(true, false).hint(200, -1)
				.applyTo(projectViewer.getControl());
		projectViewer.setContentProvider(ArrayContentProvider.getInstance());
		projectViewer.setLabelProvider(new WorkbenchLabelProvider());
		Collection<IProject> projects = getExtensionProjects();
		projectViewer.setInput(projects);
		projectViewer.setComparator(new ViewerComparator());
		projectViewerListener = event -> {
			StructuredSelection selection = (StructuredSelection) event.getSelection();
			if (!selection.isEmpty()) {
				IProject project = (IProject) selection.getFirstElement();
				handleProjectSelection(project);
			}

			updateLaunchConfigurationDialog();
		};

		runAllTests = createRadioButton(parent, "Запустить все тесты расширения");
	}

	private void createFrameworkSettings(Composite parent) {

	}

	private Collection<IProject> getExtensionProjects() {
		return projectManager.getProjects(IExtensionProject.class).stream().map(IV8Project::getProject)
				.collect(Collectors.toList());
	}

	private void handleProjectSelection(IProject project) {
		// TODO Автоматически созданная заглушка метода

	}

	@Override
	protected void doInitializeFrom(ILaunchConfiguration var1) {
		// TODO Автоматически созданная заглушка метода

	}

}