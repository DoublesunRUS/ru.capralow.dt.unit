package ru.capralow.dt.unit.launcher.plugin.core.frameworks.gson;

import com.google.gson.annotations.SerializedName;

public class FrameworkSettings {
	@SerializedName("startupOptions")
	private String startupOptions = ""; //$NON-NLS-1$

	public String getStartupOptions() {
		return startupOptions;
	}

	public void setStartupOptions(String value) {
		startupOptions = value;
	}
}
