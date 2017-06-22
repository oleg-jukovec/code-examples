package ru.ozi_blog.task.commands.nullary;

import java.util.Deque;

import ru.ozi_blog.task.exceptions.CalculatorException;
import ru.ozi_blog.task.memory.MemoryUnit;


/**
 * The class ClearMemoryCommand extends the class NullaryCommand for the clear
 * memory operation.
 * 
 * @see ru.ozi_blog.task.memory.MemoryUnit
 * 
 * @author Zhukavets Aleh
 *
 * @param <T>
 *            the type of a number that can be used in calculations
 */
final public class ClearMemoryCommand<T extends Number> extends NullaryCommand<T> {
	private MemoryUnit<T> memory;

	/**
	 * @param memory 
	 *            the memory field for cleaning
	 */
	public ClearMemoryCommand(MemoryUnit<T> memory) {
		this.memory = memory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see by.epam.task.calc.commands.ICommand#execute(java.util.Deque)
	 */
	@Override
	public void execute(Deque<String> deque) throws CalculatorException {
		memory.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ClearMemoryCommand [memory=" + memory + "]";
	}
}
