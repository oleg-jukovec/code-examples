/*
 * Файл Player.java
 */
package reversi.game;

/**
 * Перечисление задаёт список возможных игроков.
 */
public enum Player {
	/**
	 * Игрок с чёрными фишками.
	 */
	BLACK(1),
	/**
	 * Игрок с белыми фишками.
	 */
	WHITE(-1),
	/**
	 * Ни один из игроков.
	 */
	NOBODY(0);
	/**
	 * Численное представление для игрока.
	 */
	private final int sign;

	/**
	 * Конструктор перечисления задаёт численное представление для игрока.
	 * 
	 * @param sign
	 *            занчение численного представления.
	 */
	private Player(final int sign) {
		this.sign = sign;
	}

	/**
	 * Метод возращает численное представления для игрока.
	 * 
	 * @return
	 */
	public int getSign() {
		return sign;
	}

	/**
	 * Метод возвращает противника заданного игрока.
	 * 
	 * @param player
	 *            заданный игрок.
	 * @return противник заданного игрока.
	 */
	public static Player getOpponent(final Player player) {
		if(player == BLACK)
			return WHITE;
		if(player == WHITE)
			return BLACK;
		return NOBODY;
	}
}
