/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.junit.frameworks.gson;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author Aleksandr Kapralov
 *
 */
public class FeatureFormat
{
    @SerializedName("lang")
    private String lang = ""; //$NON-NLS-1$

    @SerializedName("desc")
    private List<String> description;

    @SerializedName("server-script")
    private List<String> serverScript;

    @SerializedName("client-script")
    private List<String> clientScript;

    /**
     * @return List
     */
    public List<String> getClientScript()
    {
        return clientScript;
    }

    /**
     * @return List
     */
    public List<String> getDescription()
    {
        return description;
    }

    /**
     * @return String
     */
    public String getLang()
    {
        return lang;
    }

    /**
     * @return List
     */
    public List<String> getServerScript()
    {
        return serverScript;
    }

    /**
     * @param value
     */
    public void setClientScript(List<String> value)
    {
        clientScript = value;
    }

    /**
     * @param value
     */
    public void setDescription(List<String> value)
    {
        description = value;
    }

    /**
     * @param value
     */
    public void setLang(String value)
    {
        lang = value;
    }

    /**
     * @param value
     */
    public void setServerScript(List<String> value)
    {
        serverScript = value;
    }
}
