/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.unit.launcher.frameworks.gson;

import com.google.gson.annotations.SerializedName;

public class FrameworkMetaTypes
{
    @SerializedName("methods")
    public FrameworkMethod[] methods = { };

    @SerializedName("properties")
    public FrameworkProperty[] properties = { };
}
