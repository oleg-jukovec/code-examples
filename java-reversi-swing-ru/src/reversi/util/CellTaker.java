/*
 * Файл CellTaker.java
 */
package reversi.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import reversi.game.Board;
import reversi.game.Cell;
import reversi.game.Player;
import reversi.move.MoveChecker;

/**
 * Класс отвечает за передачу игровых полей другому игроку.
 */
public class CellTaker {
	/**
	 * Объект, предназначенный для поиска пути передачи полей.
	 */
	private final MoveChecker checker;
	/**
	 * Игровое поле.
	 */
	private final Board board;

	/**
	 * Конструктор создаёт новый объект с заданным объектом для поиска пути и
	 * игровой доской.
	 * 
	 * @param checker
	 *            ссылка на объект для поиска пути.
	 * @param board
	 *            игровая доска.
	 */
	public CellTaker(final MoveChecker checker, final Board board) {
		this.checker = checker;
		this.board = board;
	}

	/**
	 * Возвращает список всех полей, которые могут быть переданы игроку, если
	 * тот сделает ход на поле с заданным индексом. Назначает игрока владельцем
	 * полей из списка.
	 * 
	 * @param cellIndex
	 *            индекс поля.
	 * @param player
	 *            игрок, сделавший ход.
	 * @return список всех фишек, которые могут быть переданы игроку, если тот
	 *         сделает ход на поле с заданным индексом.
	 */
	public Collection<Cell> takeCell(final int cellIndex, final Player player) {
		return takeSurroundedCells(board.get(cellIndex), player);
	}

	/**
	 * Возвращает список всех полей, которые могут быть переданы указанному
	 * игроку, если тот сделает ход на заданное поле. Назначает игрока
	 * владельцем полей из списка.
	 * 
	 * @param cell
	 *            заданное поле.
	 * @param player
	 *            игрок.
	 * @return список всех полей, которые могут быть переданы указанному игроку,
	 *         если тот сделает ход на заданное поле.
	 */
	private Collection<Cell> takeSurroundedCells(final Cell cell, final Player player) {
		final Set<Cell> takenCells = new LinkedHashSet<Cell>();
		// добавляем заданное поле
		takenCells.add(cell);
		// обход всех направлений и добавления возможных полей
		for (final Direction direction : Direction.values()) {
			takenCells.addAll(getCells(direction, cell, player));
		}
		// передача поля игроку
		for (final Cell takenCell : takenCells) {
			takenCell.take(player);
		}

		return takenCells;
	}

	/**
	 * Метод проверяет отдельное направление и возвращает список полей, которые
	 * могут быть переданы игроку.
	 * 
	 * @param direction
	 *            направление.
	 * @param cell
	 *            поле, с которого начинается поиск.
	 * @param player
	 *            игрок.
	 * @return список полей, которые могут быть переданы игроку.
	 */
	private Collection<Cell> getCells(final Direction direction, final Cell cell, final Player player) {
		final int neighbourIndex = checker.getNeighbourIndex(direction, cell, player);
		final int startIndex = Math.min(cell.getIndex(), neighbourIndex);
		final int endIndex = Math.max(cell.getIndex(), neighbourIndex);
		return neighbourIndex >= 0 ? getCells(startIndex, endIndex, direction.getIncrement())
				: Collections.<Cell>emptySet();
	}

	/**
	 * Возвращает все поля, которые находятся от начального индекса до конечного
	 * с заданным шагом.
	 * 
	 * @param fromIndex
	 *            начальный индекс.
	 * @param toIndex
	 *            конечный индекс.
	 * @param step
	 *            заданный шаг
	 * @return все поля, которые находятся от начального индекса до конечного с
	 *         заданным шагом.
	 */
	private Collection<Cell> getCells(final int fromIndex, final int toIndex, final int step) {
		final Collection<Cell> result = new HashSet<Cell>();
		for (int i = fromIndex; i <= toIndex; i += step) {
			result.add(board.get(i));
		}
		return result;
	}
}
