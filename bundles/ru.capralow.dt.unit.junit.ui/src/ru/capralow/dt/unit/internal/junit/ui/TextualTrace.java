/**
 * Copyright (c) 2021, Aleksandr Kapralov
 */
package ru.capralow.dt.unit.internal.junit.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

public class TextualTrace
{
    public static final int LINE_TYPE_EXCEPTION = 1;
    public static final int LINE_TYPE_NORMAL = 0;
    public static final int LINE_TYPE_STACKFRAME = 2;
    private final String fTrace;

    public TextualTrace(String trace, String[] filterPatterns)
    {
        this.fTrace = this.filterStack(trace, filterPatterns);
    }

    public void display(ITraceDisplay display, int maxLabelLength)
    {
        StringReader stringReader = new StringReader(this.fTrace);
        BufferedReader bufferedReader = new BufferedReader(stringReader);
        try
        {
            String line = this.readLine(bufferedReader);
            if (line == null)
            {
                return;
            }
            this.displayWrappedLine(display, maxLabelLength, line, 1);
            while ((line = this.readLine(bufferedReader)) != null)
            {
                int type = this.isAStackFrame(line) ? 2 : 0;
                this.displayWrappedLine(display, maxLabelLength, line, type);
            }
        }
        catch (IOException iOException)
        {
            display.addTraceLine(0, this.fTrace);
        }
    }

    private void displayWrappedLine(ITraceDisplay display, int maxLabelLength, String line, int type)
    {
        int labelLength = line.length();
        if (labelLength < maxLabelLength)
        {
            display.addTraceLine(type, line);
        }
        else
        {
            display.addTraceLine(type, line.substring(0, maxLabelLength));
            int offset = maxLabelLength;
            while (offset < labelLength)
            {
                int nextOffset = Math.min(labelLength, offset + maxLabelLength);
                display.addTraceLine(0, line.substring(offset, nextOffset));
                offset = nextOffset;
            }
        }
    }

    private boolean filterLine(String[] patterns, String line)
    {
        int i = patterns.length - 1;
        while (i >= 0)
        {
            String pattern = patterns[i];
            int len = pattern.length() - 1;
            if (pattern.charAt(len) == '*')
            {
                pattern = pattern.substring(0, len);
            }
            else if (Character.isUpperCase(pattern.charAt(0)))
            {
                pattern = "at " + pattern + '.';
            }
            else
            {
                int lastDotIndex = pattern.lastIndexOf(46);
                if (lastDotIndex != -1 && lastDotIndex != len
                    && Character.isUpperCase(pattern.charAt(lastDotIndex + 1)))
                {
                    pattern = String.valueOf(pattern) + '.';
                }
            }
            if (line.indexOf(pattern) > 0)
            {
                return true;
            }
            --i;
        }
        return false;
    }

    private String filterStack(String stackTrace, String[] filterPatterns)
    {
        if (filterPatterns.length == 0 || stackTrace == null)
        {
            return stackTrace;
        }
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        StringReader stringReader = new StringReader(stackTrace);
        BufferedReader bufferedReader = new BufferedReader(stringReader);
        String[] patterns = filterPatterns;
        boolean firstLine = true;
        try
        {
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                if (firstLine || !this.filterLine(patterns, line))
                {
                    printWriter.println(line);
                }
                firstLine = false;
            }
        }
        catch (IOException iOException)
        {
            return stackTrace;
        }
        return stringWriter.toString();
    }

    private boolean isAStackFrame(String itemLabel)
    {
        return itemLabel.contains(" at ");
    }

    private String readLine(BufferedReader bufferedReader) throws IOException
    {
        String readLine = bufferedReader.readLine();
        return readLine == null ? null : readLine.replace('\t', ' ');
    }
}
