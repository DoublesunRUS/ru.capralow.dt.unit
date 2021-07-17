package ru.capralow.dt.unit.junit.model;

import com._1c.g5.v8.dt.core.platform.IV8Project;

public interface ITestRunSession
    extends ITestElementContainer
{
    public IV8Project getLaunchedProject();

    public String getTestRunName();
}
