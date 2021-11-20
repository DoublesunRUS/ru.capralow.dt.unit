/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.junit.frameworks.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @author Aleksandr Kapralov
 *
 */
public class FrameworkMethod
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
    @SerializedName("params")
    public FrameworkMethodParameter[] params = { };

    @SerializedName("returnedValue")
    private String returnedValue = ""; //$NON-NLS-1$

    @SerializedName("returnedValues")
    private String[] returnedValues = { };

    /**
     * @return int
     */
    public int getMinParams()
    {
        int minParams = params.length;

        for (FrameworkMethodParameter param : params)
        {
            if (Boolean.TRUE.equals(param.isDefaultValue))
            {
                minParams--;
            }
        }

        return minParams;
    }

    /**
     * @return String
     */
    public String[] getReturnedValues()
    {
        if (returnedValue.isEmpty() && returnedValues != null)
        {
            return returnedValues;
        }

        return new String[] { returnedValue };
    }
}
