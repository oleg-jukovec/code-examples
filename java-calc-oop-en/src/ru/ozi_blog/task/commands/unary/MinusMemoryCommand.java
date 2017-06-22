package ru.ozi_blog.task.commands.unary;

import java.util.Deque;

import ru.ozi_blog.task.calc.primitives.CalcPrimitive;
import ru.ozi_blog.task.exceptions.CalculatorException;
import ru.ozi_blog.task.memory.MemoryUnit;


/**
 * The class MinusMemoryCommand extends the class UnaryCommand for the operation
 * which subtracts the current value from the memory value.
 * 
 * @see ru.ozi_blog.task.memory.MemoryUnit
 * 
 * @author Zhukavets Aleh
 *
 * @param <T>
 *            the type of a number that can be used in calculations
 */
final public class MinusMemoryCommand<T extends Number> extends UnaryCommand<T> {
	private MemoryUnit<T> memory;
	private CalcPrimitive<T> calcPrimitive;

	/**
	 * @param memory
	 *            the memory field for the subtraction
	 * @param calcPrimitive
	 *            can be used in calculations
	 */
	public MinusMemoryCommand(MemoryUnit<T> memory, CalcPrimitive<T> calcPrimitive) {
		this.memory = memory;
		this.calcPrimitive = calcPrimitive;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see by.epam.task.calc.commands.ICommand#execute(java.util.Deque)
	 */
	@Override
	public void execute(Deque<String> deque) throws CalculatorException {
		T first = memory.getValue();
		T second = calcPrimitive.getFromString(deque.getFirst());
		memory.setValue(calcPrimitive.sub(first, second));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MinusMemoryCommand [memory=" + memory + ", calcPrimitive=" + calcPrimitive + "]";
	}
}
