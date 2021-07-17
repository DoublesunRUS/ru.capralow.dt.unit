/**
 * Copyright (c) 2020, Aleksandr Kapralov
 */
package ru.capralow.dt.coverage.internal.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.jacoco.core.analysis.CoverageNodeImpl;
import org.jacoco.core.analysis.ISourceNode;

import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;

import ru.capralow.dt.coverage.MdUtils;
import ru.capralow.dt.coverage.analysis.IBslModelCoverage;
import ru.capralow.dt.coverage.internal.CoverageCorePlugin;

/**
 * The IBslModelCoverage implementation maps Bsl elements to its corresponding
 * coverage data objects.
 */
public class BslModelCoverage
    extends CoverageNodeImpl
    implements IBslModelCoverage
{

    /** Maps URI to coverage objects */
    private Map<URI, ISourceNode> coverageMap = new HashMap<>();

    /** Maps modules to projects objects */
    private Map<URI, URI> projectsMap = new HashMap<>();

    /** Maps modules to subsystems objects */
    private Map<URI, URI> subsystemsMap = new HashMap<>();

    /** Maps methods to modules objects */
    private Map<URI, List<URI>> methodsUriMap = new HashMap<>();
    private Map<URI, Map<String, URI>> methodsStringMap = new HashMap<>();

    /** List of all IV8Project objects with coverage information attached */
    private List<URI> projects = new ArrayList<>();

    /** List of all Subsystem objects with coverage information */
    private List<URI> subsystems = new ArrayList<>();

    /** List of all Module objects with coverage information */
    private List<URI> modules = new ArrayList<>();

    /** List of all Method objects with coverage information */
    private List<URI> methods = new ArrayList<>();

    private IV8ProjectManager projectManager;

    public BslModelCoverage()
    {
        super(ElementType.GROUP, "BslModel"); //$NON-NLS-1$

        this.projectManager = CoverageCorePlugin.getInstance().getInjector().getInstance(IV8ProjectManager.class);
    }

    @Override
    public ISourceNode getCoverageFor(String methodName, URI module)
    {
        Map<String, URI> moduleMethods = methodsStringMap.get(module);
        if (moduleMethods == null)
            return null;

        URI element = moduleMethods.get(methodName);
        if (element == null)
            return null;

        return getCoverageFor(element);
    }

    @Override
    public ISourceNode getCoverageFor(URI element)
    {
        ISourceNode coverage = coverageMap.get(element);
        if (coverage != null)
        {
            return coverage;
        }

        return null;
    }

    @Override
    public URI[] getModules()
    {
        URI[] arr = new URI[modules.size()];
        return modules.toArray(arr);
    }

    @Override
    public URI[] getProjects()
    {
        URI[] arr = new URI[projects.size()];
        return projects.toArray(arr);
    }

    @Override
    public URI[] getSubsystems()
    {
        URI[] arr = new URI[subsystems.size()];
        return subsystems.toArray(arr);
    }

    public Boolean isModuleCovered(URI moduleUri)
    {
        return modules.contains(moduleUri);
    }

    public BslNodeImpl putEmptyModule(URI moduleUri)
    {
        BslNodeImpl moduleCoverage = (BslNodeImpl)coverageMap.get(moduleUri);
        if (moduleCoverage != null)
            return moduleCoverage;

        modules.add(moduleUri);

        methodsUriMap.put(moduleUri, new ArrayList<>());

        Module module = (Module)MdUtils.getEObjectByUri(moduleUri);
        moduleCoverage = new BslNodeImpl(ElementType.CLASS, module.getUniqueName());
        coverageMap.put(moduleUri, moduleCoverage);

        moduleCoverage.setTotalMethods(module.allMethods().size());

        return moduleCoverage;
    }

    public void putMethod(URI methodUri, URI moduleUri, URI configurationUri, BslNodeImpl methodCoverage)
    {
        Module module = (Module)MdUtils.getEObjectByUri(moduleUri);
        IV8Project project = projectManager.getProject(module);

        coverageMap.put(methodUri, methodCoverage);

        BslNodeImpl moduleCoverage = (BslNodeImpl)getCoverageFor(moduleUri);
        if (moduleCoverage == null)
        {
            moduleCoverage = putEmptyModule(moduleUri);
            moduleCoverage.setTotalMethods(0);
        }

        methodCoverage.methodCounter = CounterImpl.COUNTER_0_1;

        Map<String, URI> moduleMethods = methodsStringMap.computeIfAbsent(moduleUri, k -> new HashMap<>());
        moduleMethods.put(methodCoverage.getName(), methodUri);

        moduleCoverage.increment(methodCoverage);
        List<URI> methodsList = methodsUriMap.get(moduleUri);
        methodsList.add(methodUri);

        BslNodeImpl projectCoverage = (BslNodeImpl)getCoverageFor(configurationUri);
        if (projectCoverage == null)
        {
            projects.add(configurationUri);

            projectCoverage = new BslNodeImpl(ElementType.GROUP, project.getProject().getName());
            coverageMap.put(configurationUri, projectCoverage);
        }
        projectCoverage.increment(moduleCoverage);
    }

    public void updateModuleCoverage(URI moduleUri)
    {
        BslNodeImpl moduleCoverage = (BslNodeImpl)coverageMap.get(moduleUri);
        List<URI> moduleMethods = methodsUriMap.get(moduleUri);
        if (moduleCoverage == null || moduleMethods == null)
            return;
        for (URI methodUri : moduleMethods)
        {
            BslNodeImpl methodCoverage = (BslNodeImpl)coverageMap.get(methodUri);
            moduleCoverage.increment(methodCoverage);
        }
    }
}
