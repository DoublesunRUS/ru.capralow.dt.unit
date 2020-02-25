package ru.capralow.dt.unit.launcher.plugin.core.frameworks.gson;

import com.google.gson.annotations.SerializedName;

public class FrameworkSettings {
	@SerializedName("epf-name")
	private String epfName = ""; //$NON-NLS-1$

	@SerializedName("startupOptions")
	private String startupOptions = ""; //$NON-NLS-1$

	public String getEpfName() {
		return epfName;
	}

	public void setEpfName(String value) {
		epfName = value;
	}

	public String getStartupOptions() {
		return startupOptions;
	}

	public void setStartupOptions(String value) {
		startupOptions = value;
	}
}
