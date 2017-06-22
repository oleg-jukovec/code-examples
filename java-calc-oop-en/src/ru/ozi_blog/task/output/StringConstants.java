package ru.ozi_blog.task.output;

/**
 * String constants which can be used anywhere:
 * <ul>
 * <li>{@link #SPACE}</li>
 * <li>{@link #ANSWER_BEGIN}</li>
 * <li>{@link #ERROR_BEGIN}</li>
 * </ul>
 *
 * @author Zhukavets Aleh
 * 
 */
public enum StringConstants {
	SPACE(" "), 
	ANSWER_BEGIN("Calculation result: "), 
	ERROR_BEGIN("Error: ");

	private String string;

	/**
	 * 
	 * @param string
	 *            the string constant
	 */
	StringConstants(String string) {
		this.string = string;
	}

	/**
	 * Returns the string constant
	 * 
	 * @return the string constant
	 */
	public String getString() {
		return string;
	}
}
