package ru.capralow.dt.coverage.internal.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.model.IProcess;

import com._1c.g5.wiring.IManagedService;

import ru.capralow.dt.coverage.internal.core.launching.CoverageLaunch;

public class CoverageManager implements IManagedService {

	/** Status used to trigger user prompts */
	private static final IStatus PROMPT_STATUS = new Status(IStatus.INFO, "org.eclipse.debug.ui", 200, "", null); //$NON-NLS-1$//$NON-NLS-2$

	private ILaunchListener launchListener = new ILaunchListener() {
		@Override
		public void launchAdded(ILaunch launch) {
			// Нечего делать
		}

		@Override
		public void launchChanged(ILaunch launch) {
			// Нечего делать
		}

		@Override
		public void launchRemoved(ILaunch launch) {
			if (CoverageCorePlugin.getInstance().getPreferences().getAutoRemoveSessions()) {
				CoverageCorePlugin.getInstance().getSessionManager().removeSessionsFor(launch);
			}
		}
	};

	private IDebugEventSetListener debugListener = new IDebugEventSetListener() {
		@Override
		public void handleDebugEvents(DebugEvent[] events) {
			for (DebugEvent e : events) {
				if (e.getSource() instanceof IProcess && e.getKind() == DebugEvent.TERMINATE) {
					IProcess proc = (IProcess) e.getSource();
					ILaunch launch = proc.getLaunch();
					if (launch instanceof CoverageLaunch) {
						CoverageLaunch coverageLaunch = (CoverageLaunch) launch;
						coverageLaunch.getAgentServer().stop();
					}

				}
			}
		}

	};

	@Override
	public void activate() {
		DebugPlugin.getDefault().getLaunchManager().addLaunchListener(launchListener);
		DebugPlugin.getDefault().addDebugEventListener(debugListener);
	}

	@Override
	public void deactivate() {
		DebugPlugin.getDefault().removeDebugEventListener(debugListener);
		DebugPlugin.getDefault().getLaunchManager().removeLaunchListener(launchListener);
	}

}
