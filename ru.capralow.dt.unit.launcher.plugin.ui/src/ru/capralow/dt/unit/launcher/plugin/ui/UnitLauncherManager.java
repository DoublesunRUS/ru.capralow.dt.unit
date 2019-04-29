package ru.capralow.dt.unit.launcher.plugin.ui;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IProcess;

import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.wiring.IManagedService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import ru.capralow.dt.unit.launcher.plugin.ui.launchconfigurations.UnitTestLaunch;

@Singleton
public class UnitLauncherManager implements IManagedService, IDebugEventSetListener {

	public void activate() {
		DebugPlugin.getDefault().addDebugEventListener(this);
	}

	public void deactivate() {
		DebugPlugin.getDefault().removeDebugEventListener(this);
	}

	@Inject
	private IV8ProjectManager projectManager;

	@Override
	public void handleDebugEvents(DebugEvent[] events) {
		for (DebugEvent event : events) {
			Object source = event.getSource();
			if (source instanceof IProcess && event.getKind() == DebugEvent.TERMINATE) {
				UnitTestLaunch.showJUnitResult((IProcess) source, projectManager);
			}
		}
	}

}
