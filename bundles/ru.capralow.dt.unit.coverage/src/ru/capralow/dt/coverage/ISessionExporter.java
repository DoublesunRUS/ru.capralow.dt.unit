/**
 * Copyright (c) 2020, Aleksandr Kapralov
 */
package ru.capralow.dt.coverage;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import ru.capralow.dt.coverage.internal.CoreMessages;

/**
 * API for exporting sessions. This interface is not intended to be implemented
 * by clients. Use {@link CoverageTools#getExporter(ICoverageSession)} to get an
 * instance.
 */
public interface ISessionExporter
{

    /**
     * A call to this method triggers the actual export process.
     *
     * @param monitor progress monitor
     * @throws CoreException if something goes wrong during export
     */
    void export(IProgressMonitor monitor) throws CoreException;

    /**
     * Sets the export file name. Note that in case of HTML export this is only the
     * main file, while more files are created as siblings.
     *
     * @param filename file name of export destination
     */
    void setDestination(String filename);

    /**
     * Sets the export format.
     *
     * @param format export format constant
     */
    void setFormat(ExportFormat format);

    /** Supported export formats. */
    enum ExportFormat
    {

        /** HTML report */
        HTML(CoreMessages.ExportFormatHTML_value, null),

        /** HTML report in single ZIP file */
        HTMLZIP(CoreMessages.ExportFormatHTMLZIP_value, "zip"), //$NON-NLS-1$

        /** XML report */
        XML(CoreMessages.ExportFormatXML_value, "xml"), //$NON-NLS-1$

        /** CSV report */
        CSV(CoreMessages.ExportFormatCSV_value, "csv"), //$NON-NLS-1$

        /** Execution data only */
        EXEC(CoreMessages.ExportFormatEXEC_value, "exec"); //$NON-NLS-1$

        private final String label;
        private final String fileExtension;

        ExportFormat(String label, String fileExtension)
        {
            this.label = label;
            this.fileExtension = fileExtension;
        }

        /**
         * @return default file extension for output in this format, <code>null</code>
         * if output is written to folders
         */
        public String getFileExtension()
        {
            return fileExtension;
        }

        /**
         * @return localized display label
         */
        public String getLabel()
        {
            return label;
        }

        /**
         * @return <code>true</code>, this the output of this format requires a folder
         * to write multiple files to.
         */
        public boolean isFolderOutput()
        {
            return fileExtension == null;
        }

    }

}
