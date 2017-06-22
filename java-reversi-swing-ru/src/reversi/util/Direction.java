/*
 * Файл Direction.java
 */
package reversi.util;

import reversi.Game;

/**
 * Перечисление определеяет возможные направления.
 */
public enum Direction {
	/**
	 * Слева.
	 */
	LEFT(1),
	/**
	 * Справа.
	 */
	RIGHT(1),
	/**
	 * Сверху.
	 */
	TOP(Game.BOARD_COLUMN_COUNT),
	/**
	 * Снизу.
	 */
	BOTTOM(Game.BOARD_COLUMN_COUNT),
	/**
	 * Главная диагональ - вверх.
	 */
	MAIN_DIAGONAL_TOP(Game.BOARD_COLUMN_COUNT + 1),
	/**
	 * Главная диагональ - вниз.
	 */
	MAIN_DIAGONAL_BOTTOM(Game.BOARD_COLUMN_COUNT + 1),
	/**
	 * Второстепенная диагональ - вверх.
	 */
	SECONDARY_DIAGONAL_TOP(Game.BOARD_COLUMN_COUNT - 1),
	/**
	 * Второстепенная диагональ - внизу.
	 */
	SECONDARY_DIAGONAL_BOTTOM(Game.BOARD_COLUMN_COUNT - 1);
	/**
	 * Число показывает, на сколько увеличивается индекс поля, при движении
	 * вдоль направления.
	 */
	private final int increment;

	/**
	 * Конструктор задаёт на сколько увеличивается индекс поля, при движении
	 * вдоль направления.
	 * 
	 * @param increment
	 *            на сколько увеличивается индекс поля, при движении вдоль
	 *            направления.
	 */
	private Direction(final int increment) {
		this.increment = increment;
	}

	/**
	 * Возвращает на сколько увеличивается индекс поля, при движении вдоль
	 * направления.
	 * 
	 * @return на сколько увеличивается индекс поля, при движении вдоль
	 *         направления.
	 */
	public int getIncrement() {
		return increment;
	}
}
