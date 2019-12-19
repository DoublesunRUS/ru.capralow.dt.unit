package ru.capralow.dt.unit.launcher.plugin.core.frameworks.gson;

import com.google.gson.annotations.SerializedName;

public class AssertMethodParameter {
	@SerializedName("name-en")
	public String nameEn = ""; //$NON-NLS-1$

	@SerializedName("name-ru")
	public String nameRu = ""; //$NON-NLS-1$

	@SerializedName("type")
	private String type = ""; //$NON-NLS-1$

	@SerializedName("types")
	private String[] types;

	@SerializedName("isOut")
	public Boolean isOut = false;

	@SerializedName("defaultValue")
	public String defaultValue = ""; //$NON-NLS-1$

	public String[] getTypes() {
		if (type.isEmpty() && types != null) {
			return types;
		}

		return new String[] { type };

	}
}
