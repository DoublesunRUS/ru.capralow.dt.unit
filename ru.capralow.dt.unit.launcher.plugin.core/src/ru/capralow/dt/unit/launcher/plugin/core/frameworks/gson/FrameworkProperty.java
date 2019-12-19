package ru.capralow.dt.unit.launcher.plugin.core.frameworks.gson;

import com.google.gson.annotations.SerializedName;

public class FrameworkProperty {
	@SerializedName("name-en")
	public String nameEn = ""; //$NON-NLS-1$

	@SerializedName("name-ru")
	public String nameRu = ""; //$NON-NLS-1$

	@SerializedName("type")
	public String type = ""; //$NON-NLS-1$
}
