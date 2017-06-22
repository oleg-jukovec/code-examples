package ru.ozi_blog.task.calc.primitives;

/**
 * The class IntegerPrimitive implements the interface CalcPrimitive for Integer.
 * <p>
 * It is just an example which shows how it is possible to use other Number
 * subclasses in calculations.
 * 
 * @see ru.ozi_blog.task.calc.primitives.CalcPrimitive
 *  
 * @author Zhukavets Aleh
 * 
 */
public class IntegerPrimitive implements CalcPrimitive<Integer> {
	/**
	 * Initialization the singleton instance
	 */
	private static final IntegerPrimitive instance = new IntegerPrimitive();

	private IntegerPrimitive() {
	}

	/**
	 * Returns the instance of the CalcPrimitive for Integer
	 * 
	 * @return the instance of the CalcPrimitive for Integer
	 */
	public static IntegerPrimitive getInstance() {
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see by.epam.task.calc.primitives.CalcPrimitive#sum(java.lang.Number,
	 * java.lang.Number)
	 */
	@Override
	public Integer sum(Integer first, Integer second) {
		return first + second;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see by.epam.task.calc.primitives.CalcPrimitive#sub(java.lang.Number,
	 * java.lang.Number)
	 */
	@Override
	public Integer sub(Integer first, Integer second) {
		return first - second;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see by.epam.task.calc.primitives.CalcPrimitive#mul(java.lang.Number,
	 * java.lang.Number)
	 */
	@Override
	public Integer mul(Integer first, Integer second) {
		return first * second;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see by.epam.task.calc.primitives.CalcPrimitive#div(java.lang.Number,
	 * java.lang.Number)
	 */
	@Override
	public Integer div(Integer first, Integer second) {
		return first / second;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see by.epam.task.calc.primitives.ICalcPrimitive#cos(java.lang.Number)
	 */
	@Override
	public Integer cos(Integer value) {
		return (int) Math.cos(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see by.epam.task.calc.primitives.ICalcPrimitive#exp(java.lang.Number)
	 */
	@Override
	public Integer exp(Integer value) {
		return (int) Math.exp(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see by.epam.task.calc.primitives.ICalcPrimitive#sqrt(java.lang.Number)
	 */
	@Override
	public Integer sqrt(Integer value) {
		return (int) Math.sqrt(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see by.epam.task.calc.primitives.ICalcPrimitive#value(java.lang.String)
	 */
	@Override
	public Integer getFromString(String str) throws NumberFormatException {
		Integer result = new Integer(str);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * by.epam.task.calc.primitives.ICalcPrimitive#isCorrect(java.lang.String)
	 */
	@Override
	public boolean isCorrect(String str) {
		try {
			new Integer(str);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * by.epam.task.calc.primitives.ICalcPrimitive#getString(java.lang.Number)
	 */
	@Override
	public String getString(Integer value) {
		return value.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "IntegerPrimitive";
	}

}
