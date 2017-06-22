package ru.ozi_blog.task.calc;

import java.util.Deque;
import java.util.Map;
import java.util.TreeMap;

import ru.ozi_blog.task.calc.primitives.CalcPrimitive;
import ru.ozi_blog.task.commands.Command;
import ru.ozi_blog.task.commands.CommandType;
import ru.ozi_blog.task.exceptions.CalculatorException;
import ru.ozi_blog.task.exceptions.ProcessingCalculatorException;


/**
 * The class Calculator is designed to perform arithmetic, trigonometric and
 * other operations for Number subclasses. Numbers and operations should be
 * provided in the method {@link #calculate(Deque)} by a double-ended queue with
 * members of the String type. Each a number and an operation is an isolated
 * String.
 * <p>
 * The class implements several methods for adding or removing operations that
 * can be used in the deque processing.
 * 
 * @see ru.ozi_blog.task.commands.Command
 * 
 * @author Zhukavets Aleh
 * 
 * @param <T>
 *            the type of a number that can be used in calculations
 */
public class Calculator<T extends Number> {
	/**
	 * String types which can be:
	 * <ul>
	 * <li>{@link #NUMBER}</li>
	 * <li>{@link #OPERATION}</li>
	 * <li>{@link #UNKNOWN}</li>
	 * </ul>
	 * 
	 */
	protected static enum StringType {
		/**
		 * Number of a T
		 */
		NUMBER,
		/**
		 * Nullary, Unary or Binary operation
		 * 
		 * @see by.epam.task.commands.binary.BinaryCommand
		 * @see by.epam.task.commands.unary.UnaryCommand
		 * @see by.epam.task.commands.nullary.NullaryCommand
		 */
		OPERATION,
		/**
		 * Others
		 */
		UNKNOWN;
	}

	/**
	 * The sorted map for a String names of operations (keys) and arithmetic,
	 * trigonometric and other operations (values)
	 */
	private Map<String, Command<T>> commands = new TreeMap<String, Command<T>>();

	/**
	 * It can be used in calculations with numbers
	 */
	private CalcPrimitive<T> calcPrimitive;

	/**
	 * Set the CalcPrimitive object for calculations
	 * 
	 * @param calcPrimitive
	 *            will be used in calculations with numbers
	 */
	public Calculator(CalcPrimitive<T> calcPrimitive) {
		this.calcPrimitive = calcPrimitive;
	}

	/**
	 * Returns the CalcPrimitive object which is used in calculations by this
	 * class
	 * 
	 * @return the CalcPrimitive object which is used in calculations by this
	 *         class
	 */
	public CalcPrimitive<T> getCalcPrimitive() {
		return calcPrimitive;
	}

	/**
	 * Set the CalcPrimitive object for calculations
	 * 
	 * @param calcPrimitive
	 *            will be used in calculations with numbers
	 * 
	 */
	public void setCalcPrimitive(CalcPrimitive<T> calcPrimitive) {
		this.calcPrimitive = calcPrimitive;
	}

	/**
	 * Returns result of the calculation.
	 * <p>
	 * The deque must consists of numbers and operations. Each a number should
	 * be a single string. Each an operation should be a single string.
	 * 
	 * @param deque
	 *            the double-ended queue which can be modified by the method
	 * @return the result of the calculation
	 * @throws CalculatorException
	 *             if any errors in the calculation
	 */
	public T calculate(Deque<String> deque) throws CalculatorException {
		/*
		 * Calculation continuous while in the deque more than one member. The
		 * last member should be a number.
		 */
		while (deque.size() > 1) {
			String firstString = deque.pop();
			firstLevelProcessing(deque, firstString);
		}
		/* if the last member is not a number, exception will be thrown */
		if (getStringType(deque.getFirst()) == StringType.NUMBER)
			return calcPrimitive.getFromString(deque.getFirst());
		else
			throw new ProcessingCalculatorException();
	}

	/**
	 * Adds an operation for calculations
	 * 
	 * @param key
	 *            the string name of an operation
	 * @param command
	 *            the operation object
	 */
	public void addCommand(String key, Command<T> command) {
		commands.put(key, command);
	}

	/**
	 * Removes an operation
	 * 
	 * @param key
	 *            the string name of an operation
	 */
	public void removeCommand(String key) {
		commands.remove(key);
	}

	/**
	 * Returns true if an operation exist and false otherwise
	 * 
	 * @param key
	 *            the string name of an operation
	 * @return true if an operation exist and false otherwise
	 */
	public boolean isHaveCommand(String key) {
		return commands.containsKey(key);
	}

	/**
	 * Returns an operation object if it exist
	 * 
	 * @param key
	 *            the string name of an operation
	 * @return true if an operation exist and false otherwise
	 */
	public Command<T> getCommand(String key) {
		return commands.get(key);
	}

	/**
	 * Returns the type of the string: NUMBER, OPERATION or UNKNOWN
	 * 
	 * @param str
	 *            the string
	 * @return the type of the string: NUMBER, OPERATION or UNKNOWN
	 */
	protected StringType getStringType(String str) {
		if (calcPrimitive.isCorrect(str))
			return StringType.NUMBER;
		if (isHaveCommand(str))
			return StringType.OPERATION;
		return StringType.UNKNOWN;
	}

	/**
	 * The first level of the calculation. If a first member of the deque is a
	 * number, a second level will be executed.
	 * 
	 * @param deque
	 *            the double-ended queue which can be modified by the method
	 * @param first
	 *            the former first member of the deque
	 * @throws CalculatorException
	 *             if any errors in the calculation
	 */
	private void firstLevelProcessing(Deque<String> deque, String first)
			throws CalculatorException {
		switch (getStringType(first)) {
		case NUMBER:
			/*
			 * if the first member of the deque is a number, the second level
			 * will be executed
			 */
			String second = deque.pop();
			secondLevelProcessing(deque, first, second);
			break;
		case OPERATION:
			/*
			 * if the first member of the deque is a nullary operation, it will
			 * be executed
			 */
			Command<T> command = commands.get(first);
			if (command.getType() == CommandType.NULLARY)
				command.execute(deque);
			/* else thrown exception */
			else
				throw new ProcessingCalculatorException();
			break;
		case UNKNOWN:
		default:
			throw new ProcessingCalculatorException();
		}
	}

	/**
	 * The second level of the calculation. The former first member of the deque
	 * is a number. If the former second member of the deque is a binary
	 * operation, a third level of the calculation will be executed.
	 * 
	 * @param deque
	 *            the double-ended queue which can be modified by the method
	 * @param number
	 *            the former first member of the deque
	 * @param second
	 *            the former second member of the deque
	 * @throws CalculatorException
	 *             if any errors in the calculation
	 */
	private void secondLevelProcessing(Deque<String> deque, String number,
			String second) throws CalculatorException {
		switch (getStringType(second)) {
		case NUMBER:
			/*
			 * if the second member of the deque is a number, it will be pushed
			 * back at the beginning of the deque
			 */
			deque.addFirst(second);
			break;
		case OPERATION:
			Command<T> command = commands.get(second);
			switch (command.getType()) {
			case BINARY:
				/*
				 * if the second member of the deque is a binary operation, the
				 * third level of the calculation will be executed
				 */
				String third = deque.pop();
				thirdLevelProcessing(deque, number, second, third);
				break;
			case UNARY:
				/*
				 * if the second member of the deque is an unary operation, the
				 * operation will be executed
				 */
				deque.addFirst(number);
				command.execute(deque);
				break;
			case NULLARY:
				/*
				 * If the second member of the deque is a nullary operation, the
				 * operation will be executed
				 */
				command.execute(deque);
				deque.addFirst(number);
				break;
			/* else thrown exception */
			default:
				throw new ProcessingCalculatorException();
			}
			break;
		case UNKNOWN:
		default:
			throw new ProcessingCalculatorException();
		}
	}

	/**
	 * The third level of the calculation. The former first member of the deque
	 * is a number. The former second member of the deque is a binary operation.
	 * 
	 * @param deque
	 *            the double-ended queue which can be modified by the method
	 * @param number
	 *            the former first member of the deque
	 * @param binaryOperation
	 *            the former second member of the deque
	 * @param third
	 *            the former third member of the deque
	 * @throws CalculatorException
	 *             if any errors in the calculation
	 */
	private void thirdLevelProcessing(Deque<String> deque, String number,
			String binaryOperation, String third) throws CalculatorException {
		switch (getStringType(third)) {
		case NUMBER:
			/*
			 * if the third member of the deque is a number, the binary
			 * operation will be executed.
			 */
			deque.addFirst(third);
			deque.addFirst(number);
			commands.get(binaryOperation).execute(deque);
			break;
		case OPERATION:
			/*
			 * if the third member of the deque is a nullary operation, the
			 * nullary operation will be executed.
			 */
			Command<T> command = commands.get(third);
			if (command.getType() == CommandType.NULLARY) {
				command.execute(deque);
				deque.addFirst(binaryOperation);
				deque.addFirst(number);
			} else {
				/* else thrown exception */
				throw new ProcessingCalculatorException();
			}
			break;
		case UNKNOWN:
		default:
			throw new ProcessingCalculatorException();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Calculator [commands=" + commands + ", calcPrimitive="
				+ calcPrimitive + "]";
	}
}