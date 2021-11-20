/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.junit.model;

import com._1c.g5.v8.dt.core.platform.IV8Project;

/**
 * @author Aleksandr Kapralov
 *
 */
public interface ITestRunSession
    extends ITestElementContainer
{
    /**
     * @return IV8Project
     */
    IV8Project getLaunchedProject();

    /**
     * @return String
     */
    String getTestRunName();
}
