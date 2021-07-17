/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.unit.junit.frameworks.gson;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class FeatureSettings
{
    @SerializedName("feature-format")
    private FeatureFormat[] featureFormat;

    private Map<String, FeatureFormat> featuresMap = new HashMap<>();

    public FeatureFormat getFeature(String lang)
    {
        return featuresMap.get(lang);
    }

    public FeatureFormat[] getFeatureFormat()
    {
        return featureFormat;
    }

    public void setFeature(String lang, FeatureFormat featureFormat)
    {
        featuresMap.put(lang, featureFormat);
    }

    public void setFeatureFormat(FeatureFormat[] value)
    {
        featureFormat = value;
    }

}
