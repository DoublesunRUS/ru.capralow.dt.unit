/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.coverage.internal.analysis;

import java.util.Collection;

import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ILine;
import org.jacoco.core.analysis.ISourceNode;

public class BslNodeImpl
    implements ISourceNode
{

    private final ElementType elementType;
    private final String name;
    protected CounterImpl branchCounter;
    protected CounterImpl instructionCounter;
    protected CounterImpl lineCounter;
    protected CounterImpl complexityCounter;
    protected CounterImpl methodCounter;
    protected CounterImpl classCounter;

    private LineImpl[] lines;
    private int offset;

    public BslNodeImpl(ElementType elementType, String name)
    {
        this.elementType = elementType;
        this.name = name;
        branchCounter = CounterImpl.COUNTER_0_0;
        instructionCounter = CounterImpl.COUNTER_0_0;
        complexityCounter = CounterImpl.COUNTER_0_0;
        methodCounter = CounterImpl.COUNTER_0_0;
        classCounter = CounterImpl.COUNTER_0_0;
        lineCounter = CounterImpl.COUNTER_0_0;

        lines = null;
        offset = -1;
    }

    @Override
    public boolean containsCode()
    {
        return getInstructionCounter().getTotalCount() != 0;
    }

    public void ensureCapacity(int first, int last)
    {
        if ((first == -1) || (last == -1))
        {
            return;
        }
        if (lines == null)
        {
            offset = first;
            lines = new LineImpl[last - first + 1];
        }
        else
        {
            int newFirst = Math.min(getFirstLine(), first);
            int newLast = Math.max(getLastLine(), last);
            int newLength = newLast - newFirst + 1;
            if (newLength > lines.length)
            {
                LineImpl[] newLines = new LineImpl[newLength];
                System.arraycopy(lines, 0, newLines, offset - newFirst, lines.length);

                offset = newFirst;
                lines = newLines;
            }
        }
    }

    @Override
    public ICounter getBranchCounter()
    {
        return branchCounter;
    }

    @Override
    public ICounter getClassCounter()
    {
        return classCounter;
    }

    @Override
    public ICounter getComplexityCounter()
    {
        return complexityCounter;
    }

    @Override
    public ICounter getCounter(CounterEntity entity)
    {
        switch (entity)
        {
        case INSTRUCTION:
            return getInstructionCounter();
        case BRANCH:
            return getBranchCounter();
        case LINE:
            return getLineCounter();
        case COMPLEXITY:
            return getComplexityCounter();
        case METHOD:
            return getMethodCounter();
        case CLASS:
            return getClassCounter();
        default:
            throw new AssertionError(entity);
        }
    }

    @Override
    public ElementType getElementType()
    {
        return elementType;
    }

    @Override
    public int getFirstLine()
    {
        return offset;
    }

    @Override
    public ICounter getInstructionCounter()
    {
        return instructionCounter;
    }

    @Override
    public int getLastLine()
    {
        return lines == null ? -1 : offset + lines.length - 1;
    }

    @Override
    public LineImpl getLine(int nr)
    {
        if (lines == null || nr < getFirstLine() || nr > getLastLine())
            return LineImpl.EMPTY;

        LineImpl line = lines[nr - offset];
        return line == null ? LineImpl.EMPTY : line;
    }

    @Override
    public ICounter getLineCounter()
    {
        return lineCounter;
    }

    @Override
    public ICounter getMethodCounter()
    {
        return methodCounter;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public ISourceNode getPlainCopy()
    {
        BslNodeImpl copy = new BslNodeImpl(elementType, name);
        instructionCounter = CounterImpl.getInstance(instructionCounter);
        branchCounter = CounterImpl.getInstance(branchCounter);
        lineCounter = CounterImpl.getInstance(lineCounter);
        complexityCounter = CounterImpl.getInstance(complexityCounter);
        methodCounter = CounterImpl.getInstance(methodCounter);
        classCounter = CounterImpl.getInstance(classCounter);
        return copy;
    }

    public void increment(Collection<? extends ISourceNode> children)
    {
        for (ISourceNode child : children)
        {
            increment(child);
        }
    }

    public void increment(ICounter instructions, ICounter branches, int line)
    {
        if (line != -1)
        {
            incrementLine(instructions, branches, line);
        }
        instructionCounter = instructionCounter.increment(instructions);
        branchCounter = branchCounter.increment(branches);
    }

    public void increment(ISourceNode child)
    {
        instructionCounter = instructionCounter.increment(child.getInstructionCounter());

        branchCounter = branchCounter.increment(child.getBranchCounter());
        complexityCounter = complexityCounter.increment(child.getComplexityCounter());

        methodCounter = methodCounter.increment(child.getMethodCounter());
        classCounter = classCounter.increment(child.getClassCounter());

        int firstLine = child.getFirstLine();
        if (firstLine != -1)
        {
            int lastLine = child.getLastLine();
            ensureCapacity(firstLine, lastLine);
            for (int i = firstLine; i <= lastLine; i++)
            {
                ILine line = child.getLine(i);
                incrementLine(line.getInstructionCounter(), line.getBranchCounter(), i);
            }
        }
    }

    public void setTotalMethods(int amount)
    {
        methodCounter = CounterImpl.getInstance(amount, 0);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" [").append(elementType).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
        return sb.toString();
    }

    private void incrementLine(ICounter instructions, ICounter branches, int line)
    {
        ensureCapacity(line, line);
        LineImpl l = getLine(line);
        int oldTotal = l.getInstructionCounter().getTotalCount();
        int oldCovered = l.getInstructionCounter().getCoveredCount();
        lines[line - offset] = l.increment(instructions, branches);

        if (instructions.getTotalCount() > 0)
        {
            if (instructions.getCoveredCount() == 0)
            {
                if (oldTotal == 0)
                {
                    lineCounter = lineCounter.increment(CounterImpl.COUNTER_1_0);

                }
            }
            else if (oldTotal == 0)
            {
                lineCounter = lineCounter.increment(CounterImpl.COUNTER_0_1);

            }
            else if (oldCovered == 0)
            {
                lineCounter = lineCounter.increment(-1, 1);
            }
        }
    }

}
