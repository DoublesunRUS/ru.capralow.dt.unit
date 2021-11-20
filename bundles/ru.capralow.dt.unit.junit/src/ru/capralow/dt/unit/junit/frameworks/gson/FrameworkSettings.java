/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.junit.frameworks.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @author Aleksandr Kapralov
 *
 */
public class FrameworkSettings
{
    @SerializedName("epf-name")
    private String epfName = ""; //$NON-NLS-1$

    @SerializedName("startupOptions")
    private String startupOptions = ""; //$NON-NLS-1$

    /**
     * @return String
     */
    public String getEpfName()
    {
        return epfName;
    }

    /**
     * @return String
     */
    public String getStartupOptions()
    {
        return startupOptions;
    }

    /**
     * @param value
     */
    public void setEpfName(String value)
    {
        epfName = value;
    }

    /**
     * @param value
     */
    public void setStartupOptions(String value)
    {
        startupOptions = value;
    }
}
