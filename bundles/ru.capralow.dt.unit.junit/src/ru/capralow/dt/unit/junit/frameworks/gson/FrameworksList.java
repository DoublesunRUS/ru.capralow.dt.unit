/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.junit.frameworks.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @author Aleksandr Kapralov
 *
 */
public class FrameworksList
{
    @SerializedName("list")
    private TestFramework[] list;

    /**
     * @return TestFramework
     */
    public TestFramework[] getList()
    {
        return list;
    }

    /**
     * @param value
     */
    public void setList(TestFramework[] value)
    {
        list = value;
    }
}
