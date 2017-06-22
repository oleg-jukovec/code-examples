package ru.ozi_blog.task.calc;

import ru.ozi_blog.task.calc.primitives.CalcPrimitive;
import ru.ozi_blog.task.memory.MemoryUnit;

/**
 * The class MemoryCalculator extends the class Calculator and includes one
 * object of the MemoryUnit.
 * 
 * @see ru.ozi_blog.task.memory.MemoryUnit
 * 
 * @author Zhukavets Aleh
 * 
 * @param <T>
 *            the type of a number that can be used in calculations
 */
public class MemoryCalculator<T extends Number> extends Calculator<T> {
	/**
	 * The memory unit
	 */
	private MemoryUnit<T> memory;

	/**
	 * Set the CalcPrimitive object for calculations.
	 * 
	 * @param calcPrimitive
	 *            calcPrimitive will be used in calculations with numbers
	 */
	public MemoryCalculator(CalcPrimitive<T> calcPrimitive) {
		super(calcPrimitive);
		memory = new MemoryUnit<T>();
	}

	/**
	 * Returns the memory unit that used by the object
	 * 
	 * @return the memory unit that used by the object
	 */
	public MemoryUnit<T> getMemory() {
		return memory;
	}

	/**
	 * Set the new memory unit that will be used by the object
	 * 
	 * @param memory
	 *            the memory unit to set
	 */
	public void setMemory(MemoryUnit<T> memory) {
		this.memory = memory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MemoryCalculator [calcPrimitive=" + getCalcPrimitive()
				+ ", memory=" + memory + "]";
	}
}
