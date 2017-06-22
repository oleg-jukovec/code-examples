package ru.ozi_blog.task.commands.unary;

import java.util.Deque;

import ru.ozi_blog.task.calc.primitives.CalcPrimitive;
import ru.ozi_blog.task.exceptions.CalculatorException;


/**
 * The class ExpUnaryCommand extends the class UnaryCommand for the exponent
 * function.
 * 
 * @author Zhukavets Aleh
 *
 * @param <T>
 *            the type of a number that can be used in calculations
 */
final public class ExpUnaryCommand<T extends Number> extends UnaryCommand<T> {
	private CalcPrimitive<T> calcPrimitive;

	/**
	 * @param calcPrimitive
	 *            can be used in calculations
	 */
	public ExpUnaryCommand(CalcPrimitive<T> calcPrimitive) {
		this.calcPrimitive = calcPrimitive;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see by.epam.task.calc.commands.ICommand#execute(java.util.Deque)
	 */
	@Override
	public void execute(Deque<String> deque) throws CalculatorException {
		T value = calcPrimitive.getFromString(deque.pop());
		deque.addFirst(calcPrimitive.getString(calcPrimitive.exp(value)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ExpUnaryCommand [calcPrimitive=" + calcPrimitive + "]";
	}

}
