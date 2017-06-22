package ru.ozi_blog.task.commands.nullary;

import java.util.Deque;

import ru.ozi_blog.task.calc.primitives.CalcPrimitive;
import ru.ozi_blog.task.exceptions.CalculatorException;
import ru.ozi_blog.task.memory.MemoryUnit;


/**
 * The class ImportMemoryCommand extends the class NullaryCommand for the import
 * memory operation.
 * 
 * @see ru.ozi_blog.task.memory.MemoryUnit
 * 
 * @author Zhukavets Aleh
 *
 * @param <T>
 *            the type of a number that can be used in calculations
 */
final public class ImportMemoryCommand<T extends Number> extends NullaryCommand<T> {
	private MemoryUnit<T> memory;
	private CalcPrimitive<T> calcPrimitive;
	
	/**
	 * @param memory
	 *            the memory field for import
	 * @param calcPrimitive
	 *            can be used in conversions
	 */
	public ImportMemoryCommand(MemoryUnit<T> memory,
			CalcPrimitive<T> calcPrimitive) {
		this.memory = memory;
		this.calcPrimitive = calcPrimitive;
	}
	/* (non-Javadoc)
	 * @see by.epam.task.calc.commands.ICommand#execute(java.util.Deque)
	 */
	@Override
	public void execute(Deque<String> deque) throws CalculatorException {
		deque.addFirst(calcPrimitive.getString(memory.getValue()));
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ImportMemoryCommand [memory=" + memory + ", calcPrimitive=" + calcPrimitive + "]";
	}

}
