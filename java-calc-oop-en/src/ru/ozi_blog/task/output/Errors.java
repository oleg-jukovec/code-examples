package ru.ozi_blog.task.output;

/**
 * Constant error strings which can be used for output:
 * <ul>
 * <li>{@link #ARGUMENTS_NOT_FOUND}</li>
 * <li>{@link #FILE_READING}</li>
 * <li>{@link #FILE_CONTENT}</li>
 * <li>{@link #BUILD}</li>
 * <li>{@link #CALCULATION}</li>
 * <li>{@link #MEMORY}</li>
 * <li>{@link #UNKNOWN}</li>
 * </ul>
 *
 * @author Zhukavets Aleh
 * 
 */
public enum Errors {
	ARGUMENTS_NOT_FOUND("Please, enter the file name after the program name"), 
	FILE_READING("Could not find or read file"), 
	FILE_CONTENT("Wrong content of the file"), 
	BUILD("Incorrect calculator build sequence"), 
	CALCULATION("Calculation error"), 
	MEMORY("Wrong memory operation"), 
	UNKNOWN("Unknown");

	private final String message;

	/**
	 * @param message
	 *            the error string
	 */
	Errors(String message) {
		this.message = message;
	}

	/**
	 * Returns the error string
	 * 
	 * @return the error string
	 */
	public String getMessage() {
		return message;
	}
}