package ru.ozi_blog.task.commands;

import java.util.Deque;

import ru.ozi_blog.task.exceptions.CalculatorException;


/**
 * The interface Command implements a single method for processing arithmetic
 * and other operations which can be used in calculations.
 * 
 * @author Zhukavets Aleh
 * 
 * @param <T>
 *            the type of a number that can be used in calculations
 */
public interface Command<T extends Number> {
	/**
	 * 
	 * The method make operation with a double-ended queue. Content of the deque
	 * might be changed by this method.
	 * 
	 * @param deque
	 *            double-ended queue with Strings
	 * @throws CalculatorException
	 *             if any errors in operation
	 */
	void execute(Deque<String> deque) throws CalculatorException;

	/**
	 * Returns the CommandType of the command (BINARY, UNARY or NULLARY)
	 * 
	 * @return the CommandType of the command (BINARY, UNARY or NULLARY)
	 */
	CommandType getType();
}