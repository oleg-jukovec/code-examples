package ru.ozi_blog.task.commands;

/**
 * Command types which can be used:
 * <ul>
 * <li>{@link #NULLARY}</li>
 * <li>{@link #UNARY}</li>
 * <li>{@link #BINARY}</li>
 * </ul>
 *
 * @see ru.ozi_blog.task.commands.Command
 * 
 * @author Zhukavets Aleh
 * 
 */
public enum CommandType {
	/**
	 * Command request no arguments (clear memory field, export from memory
	 * field etc)
	 */
	NULLARY,
	/**
	 * Command request one argument (cos(), sin() etc)
	 */
	UNARY,
	/**
	 * Command request two arguments (+, - etc)
	 */
	BINARY;
}