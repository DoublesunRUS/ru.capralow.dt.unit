/**
 * Copyright (c) 2020, Aleksandr Kapralov
 */
package ru.capralow.dt.coverage.internal.analysis;

import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ILine;

public abstract class LineImpl
    implements ILine
{

    private static final int SINGLETON_INS_LIMIT = 8;

    private static final int SINGLETON_BRA_LIMIT = 4;

    private static final LineImpl[][][][] SINGLETONS = new LineImpl[9][][][];
    static
    {
        for (int i = 0; i <= SINGLETON_INS_LIMIT; i++)
        {
            SINGLETONS[i] = new LineImpl[9][][];
            for (int j = 0; j <= SINGLETON_INS_LIMIT; j++)
            {
                SINGLETONS[i][j] = new LineImpl[5][];
                for (int k = 0; k <= SINGLETON_BRA_LIMIT; k++)
                {
                    SINGLETONS[i][j][k] = new LineImpl[5];
                    for (int l = 0; l <= SINGLETON_BRA_LIMIT; l++)
                    {
                        SINGLETONS[i][j][k][l] = new Fix(i, j, k, l);
                    }
                }
            }
        }
    }

    public static final LineImpl EMPTY = SINGLETONS[0][0][0][0];
    protected CounterImpl instructions;
    protected CounterImpl branches;

    private LineImpl(CounterImpl instructions, CounterImpl branches)
    {
        this.instructions = instructions;
        this.branches = branches;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof ILine)
        {
            ILine that = (ILine)obj;
            return (instructions.equals(that.getInstructionCounter())) && (branches.equals(that.getBranchCounter()));

        }
        return false;
    }

    @Override
    public ICounter getBranchCounter()
    {
        return branches;
    }

    @Override
    public ICounter getInstructionCounter()
    {
        return instructions;
    }

    @Override
    public int getStatus()
    {
        return instructions.getStatus() | branches.getStatus();
    }

    @Override
    public int hashCode()
    {
        return 23 * instructions.hashCode() ^ branches.hashCode();
    }

    public abstract LineImpl increment(ICounter paramICounter1, ICounter paramICounter2);

    private static final class Fix
        extends LineImpl
    {

        private static LineImpl getInstance(CounterImpl instructions, CounterImpl branches)
        {
            int im = instructions.getMissedCount();
            int ic = instructions.getCoveredCount();
            int bm = branches.getMissedCount();
            int bc = branches.getCoveredCount();
            if ((im <= SINGLETON_INS_LIMIT) && (ic <= SINGLETON_INS_LIMIT) && (bm <= SINGLETON_BRA_LIMIT)
                && (bc <= SINGLETON_BRA_LIMIT))
            {

                return SINGLETONS[im][ic][bm][bc];
            }
            return new Var(instructions, branches);
        }

        Fix(int im, int ic, int bm, int bc)
        {
            super(CounterImpl.getInstance(im, ic), CounterImpl.getInstance(bm, bc));
        }

        @Override
        public LineImpl increment(ICounter instructions1, ICounter branches1)
        {
            return getInstance(this.instructions.increment(instructions1), this.branches.increment(branches1));
        }

    }

    private static final class Var
        extends LineImpl
    {

        Var(CounterImpl instructions, CounterImpl branches)
        {
            super(instructions, branches);
        }

        @Override
        public LineImpl increment(ICounter instructions1, ICounter branches1)
        {
            this.instructions = this.instructions.increment(instructions1);
            this.branches = this.branches.increment(branches1);
            return this;
        }
    }
}
