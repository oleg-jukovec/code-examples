package ru.ozi_blog.task.commands.binary;

import java.util.Deque;

import ru.ozi_blog.task.calc.primitives.CalcPrimitive;
import ru.ozi_blog.task.exceptions.CalculatorException;


/**
 * The abstract class BinaryCommand extends the class BinaryCommand for simple
 * binary operations (+, -, * etc) which need two operands for a calculation and
 * return a single value.
 * <p>
 * Method {@link #simpleExecute(Number, Number)} should be overrided into
 * subclasses.
 * 
 * @see ru.ozi_blog.task.commands.binary.BinaryCommand
 * @see ru.ozi_blog.task.calc.primitives.CalcPrimitive
 * 
 * @author Zhukavets Aleh
 *
 * @param <T>
 *            the type of a number that can be used in calculations
 */
public abstract class SimpleCommand<T extends Number> extends BinaryCommand<T> {
	/**
	 * The calcPrimive can be used in calculations
	 */
	protected CalcPrimitive<T> calcPrimitive;

	/**
	 * @param calcPrimitive
	 *            can be used in calculations
	 */
	public SimpleCommand(CalcPrimitive<T> calcPrimitive) {
		this.calcPrimitive = calcPrimitive;
	}

	/**
	 * 
	 * The method make an operation with double-ended queue. It pop two strings
	 * from deque and push a result of calculation (
	 * {@link #simpleExecute(Number, Number)} ) at the deque begin.
	 * 
	 * @param deque
	 *            double-ended queue with Strings
	 * @throws CalculatorException
	 *             if any errors in operation
	 */
	@Override
	final public void execute(Deque<String> deque) throws CalculatorException {
		T firstOperand = calcPrimitive.getFromString(deque.pop());
		T secondOperand = calcPrimitive.getFromString(deque.pop());
		T tmp = simpleExecute(firstOperand, secondOperand);
		deque.addFirst(calcPrimitive.getString(tmp));
	}

	/**
	 * Returns result of operation with first and second operands
	 * 
	 * @param first
	 *            operand of operation
	 * @param second
	 *            operand of operation
	 * @return result of operation
	 */
	public abstract T simpleExecute(T first, T second);
}
