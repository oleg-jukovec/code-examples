package ru.ozi_blog.task.commands.nullary;


import java.util.Deque;

import ru.ozi_blog.task.commands.Command;
import ru.ozi_blog.task.commands.CommandType;
import ru.ozi_blog.task.exceptions.CalculatorException;

/**
 * The abstract class NullaryCommand implements the interface Command for nullary
 * operations. Method {@link #execute(Deque)} should be overrided into subclasses.
 * 
 * @see ru.ozi_blog.task.commands.Command
 * 
 * @author Zhukavets Aleh
 * 
 * @param <T>
 *            the type of a number that can be used in calculations
 */
public abstract class NullaryCommand<T extends Number> implements Command<T> {
	/*
	 * (non-Javadoc)
	 * 
	 * @see by.epam.task.commands.Command#execute(java.util.Deque)
	 */
	@Override
	public abstract void execute(Deque<String> deque) throws CalculatorException;

	/**
	 * Returns the CommandType.NULLARY
	 * 
	 * @return the CommandType.NULLARY
	 */
	@Override
	final public CommandType getType() {
		return CommandType.NULLARY;
	}

}
