package ru.capralow.dt.unit.launcher.plugin.core.frameworks.gson;

import com.google.gson.annotations.SerializedName;

public class FrameworksList {
	@SerializedName("list")
	private TestFramework[] list;

	public TestFramework[] getList() {
		return list;
	}

	public void setList(TestFramework[] value) {
		list = value;
	}
}
