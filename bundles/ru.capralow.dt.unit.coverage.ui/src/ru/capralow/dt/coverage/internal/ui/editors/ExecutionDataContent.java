/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal.ui.editors;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfo;
import org.jacoco.core.data.SessionInfoStore;

import ru.capralow.dt.coverage.internal.ui.CoverageUiPlugin;

/**
 * Internal editor model for execution data.
 */
class ExecutionDataContent
{

    private static InputStream openStream(IEditorInput input) throws CoreException, IOException
    {
        if (input instanceof IStorageEditorInput)
        {
            final IStorage storage = ((IStorageEditorInput)input).getStorage();
            return storage.getContents();
        }
        if (input instanceof IURIEditorInput)
        {
            final URI uri = ((IURIEditorInput)input).getURI();
            return uri.toURL().openStream();
        }
        throw new IOException("Unsupported input type: " + input.getClass()); //$NON-NLS-1$
    }

    private ExecutionDataStore executionData;
    private SessionInfoStore sessionData;

    private final List<IPropertyListener> listeners;

    ExecutionDataContent()
    {
        clear();
        listeners = new ArrayList<>();
    }

    public void addPropertyListener(IPropertyListener listener)
    {
        if (!listeners.contains(listener))
        {
            listeners.add(listener);
        }
    }

    public ExecutionData[] getExecutionData()
    {
        final Collection<ExecutionData> data = executionData.getContents();
        return data.toArray(new ExecutionData[data.size()]);
    }

    public SessionInfo[] getSessionInfos()
    {
        final Collection<SessionInfo> infos = sessionData.getInfos();
        return infos.toArray(new SessionInfo[infos.size()]);
    }

    public void load(IEditorInput input)
    {
        clear();
        try
        {
            if (input instanceof CoverageSessionInput)
            {
                final CoverageSessionInput csi = (CoverageSessionInput)input;
                csi.getSession().accept(executionData, sessionData);
            }
            else
            {
                final InputStream stream = openStream(input);
                final ExecutionDataReader reader = new ExecutionDataReader(stream);
                reader.setExecutionDataVisitor(executionData);
                reader.setSessionInfoVisitor(sessionData);
                while (reader.read())
                {
                    // Do nothing
                }
            }
        }
        catch (CoreException | IOException e)
        {
            CoverageUiPlugin.log(e);
        }
        fireChangedEvent();
    }

    public void removePropertyListener(IPropertyListener listener)
    {
        listeners.remove(listener);
    }

    private void clear()
    {
        executionData = new ExecutionDataStore();
        sessionData = new SessionInfoStore();
    }

    private void fireChangedEvent()
    {
        for (final IPropertyListener l : listeners)
        {
            l.propertyChanged(this, 0);
        }
    }

}
