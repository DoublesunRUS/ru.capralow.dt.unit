package ru.capralow.dt.launching.ui.launchconfigurations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

import com._1c.g5.v8.dt.debug.ui.launchconfigurations.DebugConnectionTab;
import com._1c.g5.v8.dt.debug.ui.launchconfigurations.UnsupportedLaunchTab;
import com._1c.g5.v8.dt.internal.launching.ui.launchconfigurations.ArgumentsTab;
import com._1c.g5.v8.dt.internal.launching.ui.launchconfigurations.IRuntimeClientChangeListener;
import com._1c.g5.v8.dt.internal.launching.ui.launchconfigurations.IRuntimeClientChangeNotifier;
import com._1c.g5.v8.dt.internal.launching.ui.launchconfigurations.RuntimeClientMainTab;
import com._1c.g5.v8.dt.platform.services.core.infobases.IInfobaseManager;
import com._1c.g5.v8.dt.platform.services.model.InfobaseReference;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class UnitTestLaunchTabGroup extends AbstractLaunchConfigurationTabGroup
		implements IRuntimeClientChangeNotifier {
	@Inject
	private IInfobaseManager infobaseManager;
	@Inject
	private Provider<RuntimeClientMainTab> runtimeClientMainTabProvider;
	@Inject
	private Provider<ArgumentsTab> argumentsTabProvider;
	@Inject
	private Provider<DebugConnectionTab> debugConnectionTabProvider;

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		List<ILaunchConfigurationTab> tabs = new ArrayList<>();
		if (infobaseManager.isPersistenceSupported()) {
			tabs.add(new UnitTestLaunchTab());

			RuntimeClientMainTab mainTab = runtimeClientMainTabProvider.get();
			mainTab.setRuntimeClientChangeNotifier(this);
			tabs.add(mainTab);
			tabs.add((ILaunchConfigurationTab) argumentsTabProvider.get());
			if ("debug".equals(mode)) {
				tabs.add((ILaunchConfigurationTab) debugConnectionTabProvider.get());
			}

			tabs.add(new CommonTab());

		} else {
			setTabs(new ILaunchConfigurationTab[] { new UnsupportedLaunchTab() });

		}

		setTabs((ILaunchConfigurationTab[]) tabs.toArray(new ILaunchConfigurationTab[tabs.size()]));
	}

	public void notifyClientChange(String componentTypeId) {
		ILaunchConfigurationTab[] tabs = getTabs();
		for (int length = tabs.length, i = 0; i < length; ++i) {
			final ILaunchConfigurationTab tab = tabs[i];
			if (tab instanceof IRuntimeClientChangeListener) {
				((IRuntimeClientChangeListener) tab).runtimeClientChanged(componentTypeId);
			}
		}
	}

	public void notifyClientAutoSelect() {
		ILaunchConfigurationTab[] tabs = getTabs();
		for (int length = tabs.length, i = 0; i < length; ++i) {
			final ILaunchConfigurationTab tab = tabs[i];
			if (tab instanceof IRuntimeClientChangeListener) {
				((IRuntimeClientChangeListener) tab).runtimeClientAutoSelected();
			}
		}
	}

	public void notifyPorjectChange(final IProject project) {
		ILaunchConfigurationTab[] tabs = getTabs();
		for (int length = tabs.length, i = 0; i < length; ++i) {
			final ILaunchConfigurationTab tab = tabs[i];
			if (tab instanceof IRuntimeClientChangeListener) {
				((IRuntimeClientChangeListener) tab).projectChanged(project);
			}
		}
	}

	public void notifyInfobaseChange(final InfobaseReference infobase) {
		ILaunchConfigurationTab[] tabs = getTabs();
		for (int length = tabs.length, i = 0; i < length; ++i) {
			final ILaunchConfigurationTab tab = tabs[i];
			if (tab instanceof IRuntimeClientChangeListener) {
				((IRuntimeClientChangeListener) tab).infobaseChanged(infobase);
			}
		}
	}
}