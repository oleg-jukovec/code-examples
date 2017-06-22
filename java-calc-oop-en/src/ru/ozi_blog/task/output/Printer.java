package ru.ozi_blog.task.output;

/**
 * The class Printer can be used for a simple console output.
 * 
 * @author Zhukavets Aleh
 *
 * @param <T>
 *            the type of a number that can be printed in answer
 */
public class Printer<T extends Number> {
	/**
	 * Prints error message on console.
	 * 
	 * @param error
	 *            the error for printing on console
	 */
	public void printError(Errors error) {
		System.out.println(StringConstants.ERROR_BEGIN.getString() + error.getMessage());
	}

	/**
	 * Prints value on console.
	 * 
	 * @param value
	 *            the value for printing on console
	 */
	public void printAnswer(T value) {
		System.out.println(StringConstants.ANSWER_BEGIN.getString() + value.toString());
	}
}