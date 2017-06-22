package ru.ozi_blog.task.commands.binary;

import ru.ozi_blog.task.calc.primitives.CalcPrimitive;

/**
 * The class MinusSimpleCommand extends the class SimpleCommand for an
 * arithmetic operation which subtracts two numbers.
 * 
 * @see ru.ozi_blog.task.commands.binary.SimpleCommand
 * 
 * @author Zhukavets Aleh
 *
 * @param <T>
 *            the type of a number that can be used in calculations
 */
final public class MinusSimpleCommand<T extends Number> extends SimpleCommand<T> {
	/**
	 * @param calcPrimitive
	 *            can be used in calculations
	 */
	public MinusSimpleCommand(CalcPrimitive<T> calcPrimitive) {
		super(calcPrimitive);
	}

	/**
	 * Returns first - second
	 * 
	 * @param first
	 *            the subtraction operand
	 * @param second
	 *            the subtraction operand
	 * @return first - second
	 */
	@Override
	public T simpleExecute(T first, T second) {
		return calcPrimitive.sub(first, second);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MinusSimpleCommand [calcPrimitive=" + calcPrimitive + "]";
	}

}
