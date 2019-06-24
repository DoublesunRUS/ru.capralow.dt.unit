package ru.capralow.dt.unit.launcher.plugin.core.frameworks.gson;

import com.google.gson.annotations.SerializedName;

public class TestFramework {
	@SerializedName("name")
	private String name = ""; //$NON-NLS-1$

	@SerializedName("version")
	private String version = ""; //$NON-NLS-1$

	@SerializedName("resource-path")
	private String resourcePath = ""; //$NON-NLS-1$

	@SerializedName("epf-name")
	private String epfName = ""; //$NON-NLS-1$

	public String getEpfName() {
		return epfName;
	}

	public String getName() {
		return name;
	}

	public String getResourcePath() {
		return "/frameworks/" + resourcePath + "/"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getVersion() {
		return version;
	}

	public void setEpfName(String value) {
		epfName = value;
	}

	public void setName(String value) {
		name = value;
	}

	public void setResourcePath(String value) {
		resourcePath = value;
	}

	public void setVersion(String value) {
		version = value;
	}

}
