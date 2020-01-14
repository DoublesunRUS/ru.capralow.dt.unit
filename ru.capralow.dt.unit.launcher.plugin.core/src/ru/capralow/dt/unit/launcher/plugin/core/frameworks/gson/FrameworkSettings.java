package ru.capralow.dt.unit.launcher.plugin.core.frameworks.gson;

import com.google.gson.annotations.SerializedName;

public class FrameworkSettings {
	@SerializedName("version")
	private String version = ""; //$NON-NLS-1$

	@SerializedName("epf-name")
	private String epfName = ""; //$NON-NLS-1$

	@SerializedName("startupOptions")
	private String startupOptions = ""; //$NON-NLS-1$

	public String getVersion() {
		return version;
	}

	public void setVersion(String value) {
		version = value;
	}

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
