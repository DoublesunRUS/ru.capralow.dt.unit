/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.junit.frameworks.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @author Aleksandr Kapralov
 *
 */
public class TestFramework
{
    @SerializedName("name")
    private String name = ""; //$NON-NLS-1$

    @SerializedName("bundle-name")
    private String bundleName = ""; //$NON-NLS-1$

    /**
     * @return String
     */
    public String getBundleName()
    {
        return bundleName;
    }

    /**
     * @return String
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param value
     */
    public void setBundleName(String value)
    {
        bundleName = value;
    }

    /**
     * @param value
     */
    public void setName(String value)
    {
        name = value;
    }
}
