package ru.ozi_blog.task.commands.unary;

import java.util.Deque;

import ru.ozi_blog.task.calc.primitives.CalcPrimitive;
import ru.ozi_blog.task.exceptions.CalculatorException;
import ru.ozi_blog.task.memory.MemoryUnit;


/**
 * The class ExportMemoryCommand extends the class UnaryCommand for the export
 * memory operation.
 * 
 * @see ru.ozi_blog.task.memory.MemoryUnit
 * 
 * @author Zhukavets Aleh
 *
 * @param <T>
 *            the type of a number that can be used in calculations
 */
final public class ExportMemoryCommand<T extends Number> extends UnaryCommand<T> {
	private MemoryUnit<T> memory;
	private CalcPrimitive<T> calcPrimitive;

	/**
	 * @param memory
	 *            the memory field to export value
	 * @param calcPrimitive
	 *            can be used in conversions
	 */
	public ExportMemoryCommand(MemoryUnit<T> memory, CalcPrimitive<T> calcPrimitive) {
		this.memory = memory;
		this.calcPrimitive = calcPrimitive;
	}

	@Override
	public void execute(Deque<String> deque) throws CalculatorException {
		T value = calcPrimitive.getFromString(deque.getFirst());
		memory.setValue(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ExportMemoryCommand [memory=" + memory + ", calcPrimitive=" + calcPrimitive + "]";
	}
}
