/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.junit.frameworks.gson;

import com.google.gson.annotations.SerializedName;

public class FrameworkMetaTypes
{
    @SerializedName("methods")
    public FrameworkMethod[] methods = { };

    @SerializedName("properties")
    public FrameworkProperty[] properties = { };
}
