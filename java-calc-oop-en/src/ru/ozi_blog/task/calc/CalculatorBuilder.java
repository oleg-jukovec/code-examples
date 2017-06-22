package ru.ozi_blog.task.calc;

import ru.ozi_blog.task.calc.primitives.CalcPrimitive;
import ru.ozi_blog.task.commands.binary.*;
import ru.ozi_blog.task.commands.nullary.*;
import ru.ozi_blog.task.commands.unary.*;
import ru.ozi_blog.task.exceptions.BuildCalculatorException;

/**
 * The class CalculatorBuilder takes over the process of creating an object of
 * the class Calculator. A correct build sequence:
 * <ul>
 * <li>Director makes a new object of the class Calculator or MemoryCalculator (
 * {@link #newCalculator(CalcPrimitive)} or
 * {@link #newMemoryCalculator(CalcPrimitive)});</li>
 * <li>Director adds operations to the object ({@link #addBaseCommands()},
 * {@link #addMemoryCommands()}, {@link #addScientificCommands()});</li>
 * <li>Director gets the ready object ({@link #getCalculator()}).</li>
 * </ul>
 * 
 * @see ru.ozi_blog.task.calc.Calculator
 * @see ru.ozi_blog.task.calc.MemoryCalculator
 * 
 * @author Zhukavets Aleh
 * 
 * @param <T>
 *            the type of a number that can be used by a Calculator or
 *            a MemoryCalculator
 */
public class CalculatorBuilder<T extends Number> {
	/**
	 * An object to build
	 */
	private Calculator<T> newCalc = null;

	/**
	 * Makes a new object of the class Calculator
	 * 
	 * @param calcPrimitive
	 *            will be used in the creation of the object
	 */
	public void newCalculator(CalcPrimitive<T> calcPrimitive) {
		newCalc = new Calculator<T>(calcPrimitive);
	}

	/**
	 * Makes a new object of the class MemoryCalculator
	 * 
	 * @param calcPrimitive
	 *            will be used in the creation of the object
	 */
	public void newMemoryCalculator(CalcPrimitive<T> calcPrimitive) {
		newCalc = new MemoryCalculator<T>(calcPrimitive);
	}

	/**
	 * Adds basic arithmetic operations to object
	 * 
	 * @throws BuildCalculatorException
	 *             if an object is not be made
	 */
	public void addBaseCommands() throws BuildCalculatorException {
		if (newCalc == null)
			throw new BuildCalculatorException();

		newCalc.addCommand("+",
				new PlusSimpleCommand<T>(newCalc.getCalcPrimitive()));
		newCalc.addCommand("-",
				new MinusSimpleCommand<T>(newCalc.getCalcPrimitive()));
		newCalc.addCommand("*",
				new MultiplicationSimpleCommand<T>(newCalc.getCalcPrimitive()));
		newCalc.addCommand("/",
				new DivisionSimpleCommand<T>(newCalc.getCalcPrimitive()));
	}

	/**
	 * Adds scientific operations to object
	 * 
	 * @throws BuildCalculatorException
	 *             if an object is not be made
	 */
	public void addScientificCommands() throws BuildCalculatorException {
		if (newCalc == null)
			throw new BuildCalculatorException();

		newCalc.addCommand("sqrt",
				new SqrtUnaryCommand<T>(newCalc.getCalcPrimitive()));
		newCalc.addCommand("exp",
				new ExpUnaryCommand<T>(newCalc.getCalcPrimitive()));
		newCalc.addCommand("cos",
				new CosUnaryCommand<T>(newCalc.getCalcPrimitive()));
	}

	/**
	 * Adds memory operations to object
	 * 
	 * @throws BuildCalculatorException
	 *             if an object is not be made or the object is not the
	 *             MemoryCalculator
	 */
	public void addMemoryCommands() throws BuildCalculatorException {
		if (newCalc == null)
			throw new BuildCalculatorException();

		/*if an object can't be converted to a MemoryCalculator object, thrown exception*/
		MemoryCalculator<T> tmp = null;
		if (newCalc instanceof MemoryCalculator) {
			tmp = (MemoryCalculator<T>) newCalc;
		} else {
			throw new BuildCalculatorException();
		}

		tmp.addCommand("import", 
				new ImportMemoryCommand<T>(tmp.getMemory(), tmp.getCalcPrimitive()));
		tmp.addCommand("export", 
				new ExportMemoryCommand<T>(tmp.getMemory(), tmp.getCalcPrimitive()));
		tmp.addCommand("clear", 
				new ClearMemoryCommand<T>(tmp.getMemory()));
		tmp.addCommand("plus", 	
				new PlusMemoryCommand<T>(tmp.getMemory(), tmp.getCalcPrimitive()));
		tmp.addCommand("minus", 
				new MinusMemoryCommand<T>(tmp.getMemory(), tmp.getCalcPrimitive()));
	}

	/**
	 * Returns the ready object
	 * 
	 * @return the ready object
	 * @throws BuildCalculatorException
	 *             if an object is not be made
	 */
	public Calculator<T> getCalculator() throws BuildCalculatorException {
		if (newCalc == null)
			throw new BuildCalculatorException();

		Calculator<T> tmp = newCalc;
		newCalc = null;
		return tmp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CalculatorBuilder [newCalc=" + newCalc + "]";
	}
}