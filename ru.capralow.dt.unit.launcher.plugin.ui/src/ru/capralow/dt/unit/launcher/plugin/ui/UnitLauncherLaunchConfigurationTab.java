package ru.capralow.dt.unit.launcher.plugin.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class UnitLauncherLaunchConfigurationTab extends AbstractLaunchConfigurationTab {

	private Text text;

	@Override
	public void createControl(Composite parent) {
		Composite comp = new Group(parent, SWT.BORDER);
		setControl(comp);

		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(comp);

		Label label = new Label(comp, SWT.NONE);
		label.setText("Console Text:");
		GridDataFactory.swtDefaults().applyTo(label);

		text = new Text(comp, SWT.BORDER);
		text.setMessage("Console Text");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(text);
	}

	@Override
	public String getName() {
		return "sample launch tab";
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			String consoleText = configuration.getAttribute(UnitLauncherLaunchConfigurationAttributes.CONSOLE_TEXT,
					"\"C:\\Program Files\\1cv8\\8.3.12.1790\\bin\\1cv8.exe\" /Execute \"C:\\Разработка\\vanessa-automation\\vanessa-automation-single.epf\" /C\"StartFeaturePlayer;VBParams=C:\\temp\\VBParams.json\"");
			text.setText(consoleText);
		} catch (CoreException e) {
			// ignore here
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		// set the text value for the CONSOLE_TEXT key
		configuration.setAttribute(UnitLauncherLaunchConfigurationAttributes.CONSOLE_TEXT, text.getText());
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}

}
