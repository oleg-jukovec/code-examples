/*
 * Файл ModelObserver.java
 */
package reversi.listeners;

import java.util.Collection;

import reversi.game.Cell;
import reversi.game.Player;

/**
 * Интерфейс предназначен для передачи игровых сообщений.
 */
public interface ModelObserver {
	/**
	 * Метод сообщает о том, что некоторые игровые поля изменили своё состояние.
	 * 
	 * @param changedCells
	 *            игровые поля, изменившие своё состояние.
	 */
	void onBoardChanged(final Collection<Cell> changedCells);

	/**
	 * Метод сообщает об изменении количества фишек на игровом поле.
	 * 
	 * @param whiteDiscs
	 *            количество белых фишек.
	 * @param blackDiscs
	 *            количество чёрных фишек.
	 */
	void onResultChanged(final int whiteDiscs, final int blackDiscs);

	/**
	 * Метод сообщает список доступных ходов для игрока.
	 * 
	 * @param nextMoves
	 *            список доступных ходов для игрока.
	 */
	void onNextMovesAcquired(final Collection<Cell> nextMoves);

	/**
	 * Метод сообщает о том, что изменился текущий активный игрок.
	 * 
	 * @param player
	 *            текущий активный игрок.
	 */
	void onPlayerChanged(final Player player);
}
