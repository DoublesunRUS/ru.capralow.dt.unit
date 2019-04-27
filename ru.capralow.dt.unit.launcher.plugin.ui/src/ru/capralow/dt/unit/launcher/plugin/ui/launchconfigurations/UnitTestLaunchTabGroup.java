package ru.capralow.dt.unit.launcher.plugin.ui.launchconfigurations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

import com._1c.g5.v8.dt.platform.services.core.infobases.IInfobaseManager;
import com.google.inject.Inject;

public class UnitTestLaunchTabGroup extends AbstractLaunchConfigurationTabGroup {
	@Inject
	private IInfobaseManager infobaseManager;

	@Inject

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		setTabs(new ILaunchConfigurationTab[] { new UnitTestLaunchTab(), new CommonTab() });

		List<ILaunchConfigurationTab> tabs = new ArrayList<>();
		if (infobaseManager.isPersistenceSupported()) {
			tabs.add(new UnitTestLaunchTab());

			tabs.add(new CommonTab());
		} else {
			// Здесь нужно добавить страницу, на которой будет написано что запуск не
			// поддерживается
		}

		setTabs((ILaunchConfigurationTab[]) tabs.toArray(new ILaunchConfigurationTab[tabs.size()]));
	}

}
