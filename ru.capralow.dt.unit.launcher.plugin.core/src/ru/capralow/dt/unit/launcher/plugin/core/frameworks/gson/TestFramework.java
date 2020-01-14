package ru.capralow.dt.unit.launcher.plugin.core.frameworks.gson;

import com.google.gson.annotations.SerializedName;

public class TestFramework {
	@SerializedName("name")
	private String name = ""; //$NON-NLS-1$

	@SerializedName("bundle-name")
	private String bundleName = ""; //$NON-NLS-1$

	public String getName() {
		return name;
	}

	public String getBundleName() {
		return bundleName;
	}

	public void setName(String value) {
		name = value;
	}

	public void setBundleName(String value) {
		bundleName = value;
	}
}
