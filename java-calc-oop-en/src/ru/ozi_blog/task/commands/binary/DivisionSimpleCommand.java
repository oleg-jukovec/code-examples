package ru.ozi_blog.task.commands.binary;

import ru.ozi_blog.task.calc.primitives.CalcPrimitive;

/**
 * The class DivisionSimpleCommand extends the class SimpleCommand for an
 * arithmetic operation which divides two numbers.
 * 
 * @see ru.ozi_blog.task.commands.binary.SimpleCommand
 * 
 * @author Zhukavets Aleh
 *
 * @param <T>
 *            the type of a number that can be used in calculations
 */
final public class DivisionSimpleCommand<T extends Number> extends SimpleCommand<T> {
	/**
	 * @param calcPrimitive
	 *            can be used in calculations
	 */
	public DivisionSimpleCommand(CalcPrimitive<T> calcPrimitive) {
		super(calcPrimitive);
	}

	/**
	 * Returns first / second
	 * 
	 * @param first
	 *            the division operand
	 * @param second
	 *            the devision operand
	 * @return first / second
	 */
	@Override
	public T simpleExecute(T first, T second) {
		return calcPrimitive.div(first, second);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DivisionSimpleCommand [calcPrimitive=" + calcPrimitive + "]";
	}
}
