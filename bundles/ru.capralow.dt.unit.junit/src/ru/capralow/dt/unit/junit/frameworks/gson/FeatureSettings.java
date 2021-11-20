/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.junit.frameworks.gson;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

/**
 * @author Aleksandr Kapralov
 *
 */
public class FeatureSettings
{
    @SerializedName("feature-format")
    private FeatureFormat[] featureFormat;

    private Map<String, FeatureFormat> featuresMap = new HashMap<>();

    /**
     * @param lang
     * @return FeatureFormat
     */
    public FeatureFormat getFeature(String lang)
    {
        return featuresMap.get(lang);
    }

    /**
     * @return FeatureFormat
     */
    public FeatureFormat[] getFeatureFormat()
    {
        return featureFormat;
    }

    /**
     * @param lang
     * @param featureFormat
     */
    public void setFeature(String lang, FeatureFormat featureFormat)
    {
        featuresMap.put(lang, featureFormat);
    }

    /**
     * @param value
     */
    public void setFeatureFormat(FeatureFormat[] value)
    {
        featureFormat = value;
    }

}
