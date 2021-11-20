/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.junit.frameworks.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @author Aleksandr Kapralov
 *
 */
public class FrameworkProperty
{
    /**
     *
     */
    @SerializedName("name-en")
    public String nameEn = ""; //$NON-NLS-1$

    /**
     *
     */
    @SerializedName("name-ru")
    public String nameRu = ""; //$NON-NLS-1$

    /**
     *
     */
    @SerializedName("type")
    public String type = ""; //$NON-NLS-1$
}
