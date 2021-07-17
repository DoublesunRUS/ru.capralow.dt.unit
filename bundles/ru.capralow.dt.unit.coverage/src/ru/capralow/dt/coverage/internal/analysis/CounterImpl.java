/**
 * Copyright (c) 2020, Aleksandr Kapralov
 */
package ru.capralow.dt.coverage.internal.analysis;

import org.jacoco.core.analysis.ICounter;

public abstract class CounterImpl
    implements ICounter
{
    private static final int SINGLETON_LIMIT = 30;

    private static final CounterImpl[][] SINGLETONS = new CounterImpl[31][];
    static
    {
        for (int i = 0; i <= SINGLETON_LIMIT; i++)
        {
            SINGLETONS[i] = new CounterImpl[31];
            for (int j = 0; j <= SINGLETON_LIMIT; j++)
            {
                SINGLETONS[i][j] = new Fix(i, j);
            }
        }
    }

    public static final CounterImpl COUNTER_0_0 = SINGLETONS[0][0];

    public static final CounterImpl COUNTER_1_0 = SINGLETONS[1][0];

    public static final CounterImpl COUNTER_0_1 = SINGLETONS[0][1];

    public static CounterImpl getInstance(ICounter counter)
    {
        return getInstance(counter.getMissedCount(), counter.getCoveredCount());
    }

    public static CounterImpl getInstance(int missed, int covered)
    {
        if ((missed <= SINGLETON_LIMIT) && (covered <= SINGLETON_LIMIT))
        {
            return SINGLETONS[missed][covered];
        }
        return new Var(missed, covered);
    }

    protected int missed;

    protected int covered;

    protected CounterImpl(int missed, int covered)
    {
        this.missed = missed;
        this.covered = covered;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof ICounter)
        {
            ICounter that = (ICounter)obj;
            return (missed == that.getMissedCount()) && (covered == that.getCoveredCount());

        }
        return false;
    }

    @Override
    public int getCoveredCount()
    {
        return covered;
    }

    @Override
    public double getCoveredRatio()
    {
        return Double.valueOf(covered) / (missed + covered);
    }

    @Override
    public int getMissedCount()
    {
        return missed;
    }

    @Override
    public double getMissedRatio()
    {
        return Double.valueOf(missed) / (missed + covered);
    }

    @Override
    public int getStatus()
    {
        int status = covered > 0 ? 2 : 0;
        if (missed > 0)
        {
            status |= 0x1;
        }
        return status;
    }

    @Override
    public int getTotalCount()
    {
        return missed + covered;
    }

    @Override
    public double getValue(ICounter.CounterValue value)
    {
        switch (value)
        {
        case TOTALCOUNT:
            return getTotalCount();
        case MISSEDCOUNT:
            return getMissedCount();
        case COVEREDCOUNT:
            return getCoveredCount();
        case MISSEDRATIO:
            return getMissedRatio();
        case COVEREDRATIO:
            return getCoveredRatio();
        default:
            throw new AssertionError(value);
        }
    }

    @Override
    public int hashCode()
    {
        return missed ^ covered * 17;
    }

    public CounterImpl increment(ICounter counter)
    {
        return increment(counter.getMissedCount(), counter.getCoveredCount());
    }

    public abstract CounterImpl increment(int paramInt1, int paramInt2);

    @Override
    public String toString()
    {
        StringBuilder b = new StringBuilder("Counter["); //$NON-NLS-1$
        b.append(getMissedCount());
        b.append('/').append(getCoveredCount());
        b.append(']');
        return b.toString();
    }

    private static class Fix
        extends CounterImpl
    {
        Fix(int missed, int covered)
        {
            super(missed, covered);
        }

        @Override
        public CounterImpl increment(int missed2, int covered2)
        {
            return getInstance(this.missed + missed2, this.covered + covered2);
        }
    }

    private static class Var
        extends CounterImpl
    {
        Var(int missed, int covered)
        {
            super(missed, covered);
        }

        @Override
        public CounterImpl increment(int missed2, int covered2)
        {
            this.missed += missed2;
            this.covered += covered2;
            return this;
        }
    }
}
