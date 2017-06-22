package ru.ozi_blog.task.memory;

import ru.ozi_blog.task.exceptions.MemoryCalculatorException;

/**
 * The class MemoryUnit give possibility for storage an object of a Number subclass.
 * <p>
 * This class provides methods for working with a value of a T:
 * <ul>
 * <li>set value {@link #setValue(Number)};</li>
 * <li>get value {@link #getValue()};</li>
 * <li>clear value {@link #clear()}.</li>
 * </ul>
 * 
 * @author Zhukavets Aleh
 * 
 * @param <T>
 *            the type of a number that can be stored
 */
public class MemoryUnit<T extends Number> {
	/**
	 * the stored value of a T
	 */
	private T value;

	/**
	 * The constructor initializes object with the clear memory value
	 */
	public MemoryUnit() {
		value = null;
	}

	/**
	 * Returns stored value
	 * 
	 * @return stored value
	 * @throws MemoryCalculatorException
	 *             if value is clear
	 */
	public T getValue() throws MemoryCalculatorException {
		if (isClear())
			throw new MemoryCalculatorException();
		return value;
	}

	/**
	 * Set the new stored value
	 * 
	 * @param value
	 *            the new stored value
	 */
	public void setValue(T value) {
		this.value = value;
	}

	/**
	 * Clear the current stored value
	 */
	public void clear() {
		value = null;
	}

	/**
	 * Returns false if the stored value is clear and true otherwise
	 * 
	 * @return false if the stored value is clear and true otherwise
	 */
	public boolean isClear() {
		return (value == null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MemoryUnit [value=" + value + "]";
	}

}
