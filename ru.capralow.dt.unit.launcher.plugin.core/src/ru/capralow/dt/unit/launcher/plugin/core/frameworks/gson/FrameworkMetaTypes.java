package ru.capralow.dt.unit.launcher.plugin.core.frameworks.gson;

import com.google.gson.annotations.SerializedName;

public class FrameworkMetaTypes {
	@SerializedName("methods")
	public FrameworkMethod[] methods = {};

	@SerializedName("properties")
	public FrameworkProperty[] properties = {};
}
