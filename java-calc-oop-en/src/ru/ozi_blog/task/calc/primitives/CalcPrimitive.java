package ru.ozi_blog.task.calc.primitives;

/**
 * This interface give possibility to use any a Number subclass in calculations because
 * the task does not specify a data type for numbers. It can be integer or real.
 * <p>
 * Classes that implement this interface will have basic arithmetic and
 * scientific operations for a T. I might divide this interface into two parts
 * but I do not think that it's really necessary.
 * <p>
 * In addition, this interface provides methods for converting a T to a String
 * and a String to a T.
 * <p>
 * Most methods do not have any exceptions because:
 * <ul>
 * <li>on the condition of the task all input data are correct;</li>
 * <li>NaN and Infinity are logically correct values as a calculation result.</li>
 * </ul>
 * 
 * @see ru.ozi_blog.task.calc.primitives.DoublePrimitive
 * @see ru.ozi_blog.task.calc.primitives.IntegerPrimitive
 * 
 * @author Zhukavets Aleh
 * 
 * @param <T>
 *            the type of a number that can be used in calculations
 * 
 */
public interface CalcPrimitive<T extends Number> {
	/**
	 * Basic arithmetic operation which adds two numbers
	 * 
	 * @param first
	 *            the addition operand
	 * @param second
	 *            the addition operand
	 * @return first + second
	 */
	public T sum(T first, T second);

	/**
	 * Basic arithmetic operation which subtracts two numbers
	 * 
	 * @param first
	 *            the subtraction operand
	 * @param second
	 *            the subtraction operand
	 * @return first - second
	 */
	public T sub(T first, T second);

	/**
	 * Basic arithmetic operation which multiplies two numbers
	 * 
	 * @param first
	 *            the multiplication operand
	 * @param second
	 *            the multiplication operand
	 * @return first * second
	 */
	public T mul(T first, T second);

	/**
	 * Basic arithmetic operation which divides two numbers
	 * 
	 * @param first
	 *            the division operand
	 * @param second
	 *            the division operand
	 * @return first / second
	 */
	public T div(T first, T second);

	/**
	 * Basic operation which calculates the cosine of the value
	 * 
	 * @param value
	 *            the operand of the cosine function in radians
	 * @return the cosine of the value
	 */
	public T cos(T value);

	/**
	 * Basic operation which calculates the (value)^e
	 * 
	 * @param value
	 *            the operand of the calculation
	 * @return (value)^e
	 */
	public T exp(T value);

	/**
	 * Basic operation which calculates the square root of a value.
	 * 
	 * @param value
	 *            the operand of the square root function
	 * @return the square root of value
	 */
	public T sqrt(T value);

	/**
	 * Convert a String to a T value
	 * 
	 * @param str
	 *            a String for converting
	 * @return the value of the specified String as a T
	 * @throws NumberFormatException
	 *             when str is incorrect
	 */
	public T getFromString(String str) throws NumberFormatException;

	/**
	 * Check a String on a possibility of converting
	 * 
	 * @param str
	 *            a String for check
	 * @return true for correct str and false for incorrect
	 */
	public boolean isCorrect(String str);

	/**
	 * Convert a T value to a String
	 * 
	 * @param value
	 *            a T object to converting
	 * @return the value of the specified number as a String
	 */
	public String getString(T value);
}