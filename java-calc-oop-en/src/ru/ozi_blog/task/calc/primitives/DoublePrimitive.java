package ru.ozi_blog.task.calc.primitives;

/**
 * The class DoublePrimitive implements the interface CalcPrimitive for Double.
 * <p>
 * It is used in calculations for the task.
 * 
 * @see ru.ozi_blog.task.calc.primitives.CalcPrimitive
 * 
 * @author Zhukavets Aleh
 * 
 */
public class DoublePrimitive implements CalcPrimitive<Double> {
	/**
	 * Initialization the singleton instance
	 */
	private static final DoublePrimitive instance = new DoublePrimitive();

	private DoublePrimitive() {
	}

	/**
	 * Returns the instance of the CalcPrimitive for Double
	 * 
	 * @return the instance of the CalcPrimitive for Double
	 */
	public static DoublePrimitive instance() {
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see by.epam.task.calc.primitives.CalcPrimitive#sum(java.lang.Number,
	 * java.lang.Number)
	 */
	@Override
	public Double sum(Double first, Double second) {
		return first + second;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see by.epam.task.calc.primitives.CalcPrimitive#sub(java.lang.Number,
	 * java.lang.Number)
	 */
	@Override
	public Double sub(Double first, Double second) {
		return first - second;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see by.epam.task.calc.primitives.CalcPrimitive#mul(java.lang.Number,
	 * java.lang.Number)
	 */
	@Override
	public Double mul(Double first, Double second) {
		return first * second;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see by.epam.task.calc.primitives.CalcPrimitive#div(java.lang.Number,
	 * java.lang.Number)
	 */
	@Override
	public Double div(Double first, Double second) {
		return first / second;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see by.epam.task.calc.primitives.ICalcPrimitive#cos(java.lang.Number)
	 */
	@Override
	public Double cos(Double value) {
		return Math.cos(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see by.epam.task.calc.primitives.ICalcPrimitive#exp(java.lang.Number)
	 */
	@Override
	public Double exp(Double value) {
		return Math.exp(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see by.epam.task.calc.primitives.ICalcPrimitive#sqrt(java.lang.Number)
	 */
	@Override
	public Double sqrt(Double value) {
		return Math.sqrt(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see by.epam.task.calc.primitives.ICalcPrimitive#value(java.lang.String)
	 */
	@Override
	public Double getFromString(String str) throws NumberFormatException {
		Double result = new Double(str);
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
			new Double(str);
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
	public String getString(Double value) {
		return value.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DoublePrimitive";
	}

}
