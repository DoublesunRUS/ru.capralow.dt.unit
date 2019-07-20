package ru.capralow.dt.internal.launching.ui.launchconfigurations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

import com._1c.g5.v8.dt.debug.ui.launchconfigurations.DebugConnectionTab;
import com._1c.g5.v8.dt.debug.ui.launchconfigurations.UnsupportedLaunchTab;
import com._1c.g5.v8.dt.internal.launching.ui.launchconfigurations.ArgumentsTab;
import com._1c.g5.v8.dt.internal.launching.ui.launchconfigurations.RuntimeClientMainTab;
import com._1c.g5.v8.dt.internal.launching.ui.launchconfigurations.RuntimeClientTabGroup;
import com._1c.g5.v8.dt.platform.services.core.infobases.IInfobaseManager;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class UnitTestLaunchTabGroup extends RuntimeClientTabGroup {
	@Inject
	private IInfobaseManager infobaseManager;
	@Inject
	private Provider<UnitTestLaunchTab> unitTestLaunchTabProvider;
	@Inject
	private Provider<ArgumentsTab> argumentsTabProvider;
	@Inject
	private Provider<RuntimeClientMainTab> runtimeClientMainTabProvider;
	@Inject
	private Provider<DebugConnectionTab> debugConnectionTabProvider;

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		List<ILaunchConfigurationTab> tabs = new ArrayList<>();
		if (infobaseManager.isPersistenceSupported()) {
			tabs.add(unitTestLaunchTabProvider.get());

			RuntimeClientMainTab mainTab = runtimeClientMainTabProvider.get();
			mainTab.setRuntimeClientChangeNotifier(this);
			tabs.add(mainTab);

			tabs.add(argumentsTabProvider.get());
			if ("debug".equals(mode)) //$NON-NLS-1$
				tabs.add(debugConnectionTabProvider.get());

			tabs.add(new CommonTab());

		} else {
			setTabs(new ILaunchConfigurationTab[] { new UnsupportedLaunchTab() });

		}

		setTabs(tabs.toArray(new ILaunchConfigurationTab[tabs.size()]));
	}
}