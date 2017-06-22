package ru.ozi_blog.task.calc;

import ru.ozi_blog.task.calc.primitives.CalcPrimitive;
import ru.ozi_blog.task.exceptions.BuildCalculatorException;
import ru.ozi_blog.task.exceptions.FileContentCalculatorException;

/**
 * The class Director build a new Calculator object. That can be:
 * <ul>
 * <li>a simple calculator with basic arithmetic functions (+, -, /, *);</li>
 * <li>a simple calculator with a memory unit support;</li>
 * <li>a scientific calculator with basic arithmetic and other functions (cos,
 * exp, sqrt);</li>
 * <li>a scientific calculator with a memory unit support.</li>
 * </ul>
 * 
 * @author Zhukavets Aleh
 * 
 * @param <T>
 *            the type of a number that can be used by build
 */
public class CalculatorDirector<T extends Number> {
	/**
	 * The CalcPrimitive that will be used for creation a Calculator object
	 */
	private CalcPrimitive<T> calcPrimitive = null;
	/**
	 * The CalculatorBuilder that will be used for creation a Calculator object
	 */
	private CalculatorBuilder<T> builder = null;

	/**
	 * @param calcPrimitive
	 *            will be used in the creation of the object
	 */
	public CalculatorDirector(CalcPrimitive<T> calcPrimitive) {
		this.calcPrimitive = calcPrimitive;
		builder = new CalculatorBuilder<T>();
	}

	/**
	 * Returns a ready Calculator object. If type string contents:
	 * <ul>
	 * <li>"1" - builds a simple calculator;</li>
	 * <li>"2" - builds a simple calculator with a memory unit support;</li>
	 * <li>"3" - builds a scientific calculator;</li>
	 * <li>"4" - builds a scientific calculator with a memory unit support.</li>
	 * </ul>
	 * 
	 * @param type
	 *            the type of a build object
	 * @return a ready Calculator object
	 * @throws FileContentCalculatorException
	 *             if the type is not correct
	 * @throws BuildCalculatorException
	 *             if a build sequence is not correct
	 */
	public Calculator<T> getCalculator(String type)
			throws FileContentCalculatorException, BuildCalculatorException {
		switch (type) {
		/*
		 * makes a simple calculator
		 */
		case "1":
			builder.newCalculator(calcPrimitive);
			builder.addBaseCommands();
			break;
		/*
		 * makes a simple calculator with a memory unit support
		 */
		case "2":
			builder.newMemoryCalculator(calcPrimitive);
			builder.addBaseCommands();
			builder.addMemoryCommands();
			break;
		/* 
		 * makes a scientific calculator
		 */
		case "3":
			builder.newCalculator(calcPrimitive);
			builder.addBaseCommands();
			builder.addScientificCommands();
			break;
		/*
		 * makes a scientific calculator with a memory unit support
		 */
		case "4":
			builder.newMemoryCalculator(calcPrimitive);
			builder.addBaseCommands();
			builder.addMemoryCommands();
			builder.addScientificCommands();
			break;
		default:
			throw new FileContentCalculatorException();
		}
		return builder.getCalculator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CalculatorDirector [calcPrimitive=" + calcPrimitive
				+ ", builder=" + builder + "]";
	}
}