package ru.capralow.dt.unit.launcher.plugin.core.frameworks.gson;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class FeatureFormat {
	@SerializedName("lang")
	private String lang = ""; //$NON-NLS-1$

	@SerializedName("desc")
	private List<String> description;

	@SerializedName("server-script")
	private List<String> serverScript;

	@SerializedName("client-script")
	private List<String> clientScript;

	public List<String> getClientScript() {
		return clientScript;
	}

	public List<String> getDescription() {
		return description;
	}

	public String getLang() {
		return lang;
	}

	public List<String> getServerScript() {
		return serverScript;
	}

	public void setClientScript(List<String> value) {
		clientScript = value;
	}

	public void setDescription(List<String> value) {
		description = value;
	}

	public void setLang(String value) {
		lang = value;
	}

	public void setServerScript(List<String> value) {
		serverScript = value;
	}
}
