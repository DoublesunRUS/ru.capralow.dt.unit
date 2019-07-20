package ru.capralow.dt.unit.launcher.plugin.internal.ui;

import java.util.List;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IProcess;

import com._1c.g5.v8.dt.profiling.core.IProfileTarget;
import com._1c.g5.v8.dt.profiling.core.IProfilingResult;
import com._1c.g5.v8.dt.profiling.core.IProfilingService;
import com._1c.g5.wiring.IManagedService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import ru.capralow.dt.unit.launcher.plugin.internal.ui.launchconfigurations.UnitTestLaunch;

@Singleton
public class UnitLauncherManager implements IManagedService, IDebugEventSetListener {

	@Inject
	private IProfilingService profilingService;

	@Override
	public void activate() {
		DebugPlugin.getDefault().addDebugEventListener(this);
		profilingService.toggleTargetWaitingState(true);
	}

	@Override
	public void deactivate() {
		profilingService.toggleTargetWaitingState(false);
		DebugPlugin.getDefault().removeDebugEventListener(this);
	}

	@Override
	public void handleDebugEvents(DebugEvent[] events) {
		for (DebugEvent event : events) {
			Object source = event.getSource();
			if (event.getKind() == DebugEvent.TERMINATE) {
				if (source instanceof IProcess) {
					UnitTestLaunch.showJUnitResult((IProcess) source);

				} else if (source instanceof IProfileTarget) {
					List<IProfilingResult> profilingResults = profilingService.getResults();

					try {
						UnitTestLaunch.showCoverageResult(profilingResults);
					} catch (Exception e) {
						// TODO Автоматически созданный блок catch
						e.printStackTrace();
					}

				}

			}
		}
	}

}
