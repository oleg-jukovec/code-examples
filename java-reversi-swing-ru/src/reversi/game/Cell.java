/*
 * Файл Cell.java
 */
package reversi.game;

import reversi.Game;

/**
 * Класс описывает игровое поле.
 */
public class Cell {
	/**
	 * Положение поля по оси x на игральной доске.
	 */
	private final int x;
	/**
	 * Положение поля по оси y на игральной доске.
	 */
	private final int y;
	/**
	 * Индекс поля на доске.
	 */
	private final int index;
	/**
	 * Владелец поля.
	 */
	private Player owner;

	/**
	 * Конструктор создаёт новое поле с заданным индексом и владельцем.
	 * 
	 * @param index
	 *            индекс нового поля на доске.
	 * @param owner
	 *            владелец нового поля.
	 */
	public Cell(final int index, final Player owner) {
		this.index = index;
		// вычисление положения фишки на оси x
		this.x = index % Game.BOARD_COLUMN_COUNT;
		// вычисление положения фишки на оси y
		this.y = index / Game.BOARD_ROW_COUNT;
		this.owner = owner;
	}

	/**
	 * Конструктор создаёт пустое поле по заданным координатам.
	 * 
	 * @param x
	 *            координата x на доске.
	 * @param y
	 *            координата y на доске.
	 */
	public Cell(final int x, final int y) {
		this.x = x;
		this.y = y;
		// вычисление индекса фишки
		index = y * Game.BOARD_COLUMN_COUNT + x;
		owner = Player.NOBODY;
	}

	/**
	 * Конструктор создаёт новое пустое поле по заданному индексу.
	 * 
	 * @param index
	 *            индекс поля на доске.
	 */
	public Cell(final int index) {
		this(index, Player.NOBODY);
	}

	/**
	 * Метод назначает нового владельца поля.
	 * 
	 * @param player
	 *            новый владелец поля.
	 */
	public void take(Player player) {
		owner = player;
	}

	/**
	 * Возвращает true, если поле не принадлежит ни кому из игроков.
	 * 
	 * @return true, если поле не принадлежит ни кому из игроков.
	 */
	public boolean isEmpty() {
		return isOwnedBy(Player.NOBODY);
	}

	/**
	 * Возвращает true, если заданный игрок является владельцем поле.
	 * 
	 * @param player
	 *            заданный игрок.
	 * @return true, если заданный игрок является владельцем поля.
	 */
	public boolean isOwnedBy(final Player player) {
		return owner == player;
	}

	/**
	 * Возвращает координату x поля на доске.
	 * 
	 * @return координата x поля на доске.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Возвращает координату y поля на доске.
	 * 
	 * @return координата y поля на доске.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Возвращает индекс поля на доске.
	 * 
	 * @return индекс поля на доске.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Возвращает владельца поля.
	 * 
	 * @return владелец поля.
	 */
	public Player getOwner() {
		return owner;
	}

	/**
	 * Возвращает true если у другого заданного поля тот же владелец.
	 * 
	 * @param other
	 *            другое заданное поле.
	 * @return true если у другого заданного поля тот же владелец.
	 */
	public boolean hasSameOwner(final Cell other) {
		return owner == other.owner;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cell other = (Cell) obj;
		if (index != other.index)
			return false;
		if (owner != other.owner)
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
}
