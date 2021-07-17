/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.unit.junit.frameworks.gson;

import com.google.gson.annotations.SerializedName;

public class FrameworkSettings
{
    @SerializedName("epf-name")
    private String epfName = ""; //$NON-NLS-1$

    @SerializedName("startupOptions")
    private String startupOptions = ""; //$NON-NLS-1$

    public String getEpfName()
    {
        return epfName;
    }

    public String getStartupOptions()
    {
        return startupOptions;
    }

    public void setEpfName(String value)
    {
        epfName = value;
    }

    public void setStartupOptions(String value)
    {
        startupOptions = value;
    }
}
