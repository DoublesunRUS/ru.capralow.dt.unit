package ru.capralow.dt.coverage.internal.core.analysis;

import java.util.Collection;

import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ICoverageNode;

public class ModuleNodeImpl implements ICoverageNode {

	private final ICoverageNode.ElementType elementType;
	private final String name;
	protected CounterImpl branchCounter;
	protected CounterImpl instructionCounter;
	protected CounterImpl lineCounter;
	protected CounterImpl complexityCounter;
	protected CounterImpl methodCounter;
	protected CounterImpl classCounter;

	public ModuleNodeImpl(ICoverageNode.ElementType elementType, String name) {
		this.elementType = elementType;
		this.name = name;
		branchCounter = CounterImpl.COUNTER_0_0;
		instructionCounter = CounterImpl.COUNTER_0_0;
		complexityCounter = CounterImpl.COUNTER_0_0;
		methodCounter = CounterImpl.COUNTER_0_0;
		classCounter = CounterImpl.COUNTER_0_0;
		lineCounter = CounterImpl.COUNTER_0_0;
	}

	public void increment(ICoverageNode child) {
		instructionCounter = instructionCounter.increment(child.getInstructionCounter());

		branchCounter = branchCounter.increment(child.getBranchCounter());
		lineCounter = lineCounter.increment(child.getLineCounter());
		complexityCounter = complexityCounter.increment(child.getComplexityCounter());

		methodCounter = methodCounter.increment(child.getMethodCounter());
		classCounter = classCounter.increment(child.getClassCounter());
	}

	public void increment(Collection<? extends ICoverageNode> children) {
		for (ICoverageNode child : children) {
			increment(child);
		}
	}

	@Override
	public ICoverageNode.ElementType getElementType() {
		return elementType;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ICounter getInstructionCounter() {
		return instructionCounter;
	}

	@Override
	public ICounter getBranchCounter() {
		return branchCounter;
	}

	@Override
	public ICounter getLineCounter() {
		return lineCounter;
	}

	@Override
	public ICounter getComplexityCounter() {
		return complexityCounter;
	}

	@Override
	public ICounter getMethodCounter() {
		return methodCounter;
	}

	@Override
	public ICounter getClassCounter() {
		return classCounter;
	}

	@Override
	public ICounter getCounter(ICoverageNode.CounterEntity entity) {
		switch (entity) {
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
		}
		throw new AssertionError(entity);
	}

	@Override
	public boolean containsCode() {
		return getInstructionCounter().getTotalCount() != 0;
	}

	@Override
	public ICoverageNode getPlainCopy() {
		ModuleNodeImpl copy = new ModuleNodeImpl(elementType, name);
		instructionCounter = CounterImpl.getInstance(instructionCounter);
		branchCounter = CounterImpl.getInstance(branchCounter);
		lineCounter = CounterImpl.getInstance(lineCounter);
		complexityCounter = CounterImpl.getInstance(complexityCounter);
		methodCounter = CounterImpl.getInstance(methodCounter);
		classCounter = CounterImpl.getInstance(classCounter);
		return copy;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name).append(" [").append(elementType).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
		return sb.toString();
	}
}
