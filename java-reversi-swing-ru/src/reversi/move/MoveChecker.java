/*
 * Файл MoveChecker.java
 */
package reversi.move;

import reversi.Game;
import reversi.game.Board;
import reversi.game.Cell;
import reversi.game.Player;
import reversi.util.Direction;

/**
 * Класс предназначен для проверки возможности хода игрока и вычисления границ
 * результатов хода.
 */
public class MoveChecker {
	/**
	 * Игровая доска
	 */
	private final Board board;

	/**
	 * Конструктор создаёт объект согласно заданной игровой доски.
	 * 
	 * @param board
	 *            игровая доска.
	 */
	public MoveChecker(final Board board) {
		this.board = board;
	}

	/**
	 * Возвращает true, если ход игрока возможен на поле с указанным индексом.
	 * 
	 * @param cellIndex
	 *            индекс игрового поля.
	 * @param player
	 *            игрок.
	 * @return true, если ход возможен.
	 */
	public boolean isMovePermitted(final int cellIndex, final Player player) {
		final Cell cell = board.get(cellIndex);
		return cell.isEmpty() && isMovePermitted(cell, player);
	}

	/**
	 * Метод возвращает индекс поля, вплодь до которого ход игрока будет иметь
	 * результат или -1.
	 * 
	 * @param direction
	 *            направление хода.
	 * @param cell
	 *            игровое поле, от которого следует начинать вычисления.
	 * @param player
	 *            игрок.
	 * @return индекс поля, вплодь до которого ход игрока будет иметь результат
	 *         или -1.
	 */
	public int getNeighbourIndex(final Direction direction, final Cell cell, final Player player) {
		switch (direction) {
		case TOP:
			return getLineNeighbourIndex(cell, player, true, false);
		case BOTTOM:
			return getLineNeighbourIndex(cell, player, false, false);
		case LEFT:
			return getLineNeighbourIndex(cell, player, true, true);
		case RIGHT:
			return getLineNeighbourIndex(cell, player, false, true);
		case MAIN_DIAGONAL_BOTTOM:
			return getDiagonalNeighbourIndex(cell, player, false, true);
		case MAIN_DIAGONAL_TOP:
			return getDiagonalNeighbourIndex(cell, player, true, true);
		case SECONDARY_DIAGONAL_BOTTOM:
			return getDiagonalNeighbourIndex(cell, player, false, false);
		case SECONDARY_DIAGONAL_TOP:
			return getDiagonalNeighbourIndex(cell, player, true, false);
		default:
			return -1;
		}
	}

	/**
	 * Метод возвращает true, если игрок может сделать ход на указанное поле.
	 * 
	 * @param cell
	 *            игровое поле для возможного хода.
	 * @param player
	 *            игрок.
	 * @return true, если игрок может сделать ход на указанное поле.
	 */
	private boolean isMovePermitted(final Cell cell, final Player player) {
		return getLineNeighbourIndex(cell, player, true, false) >= 0
				|| getLineNeighbourIndex(cell, player, false, false) >= 0
				|| getLineNeighbourIndex(cell, player, true, true) >= 0
				|| getLineNeighbourIndex(cell, player, false, true) >= 0
				|| getDiagonalNeighbourIndex(cell, player, false, true) >= 0
				|| getDiagonalNeighbourIndex(cell, player, true, true) >= 0
				|| getDiagonalNeighbourIndex(cell, player, false, false) >= 0
				|| getDiagonalNeighbourIndex(cell, player, true, false) >= 0;
	}

	/**
	 * Метод возвращает индекс поля, вплодь до которого ход игрока будет иметь
	 * результат или -1 для прямой линии.
	 * 
	 * @param cell
	 *            игровое поле, от которого следует начинать вычисления.
	 * @param player
	 *            игрок.
	 * @param isNegativeDirection
	 *            положительное или отрицательное направление.
	 * @param horizontal
	 *            горизонтальное (true) или вертикальное (false)
	 * @return индекс поля, вплодь до которого ход игрока будет иметь результат
	 *         или -1.
	 */
	private int getLineNeighbourIndex(final Cell cell, final Player player, final boolean isNegativeDirection,
			final boolean horizontal) {
		int cellIndex = cell.getIndex();
		// максимальный индекс, для которого есть смысл продолжать движение
		int max = getEndLineIndex(cell, isNegativeDirection, horizontal);
		// движение вдоль направления
		for (int i = 1; i < max; ++i) {
			cellIndex = incrementLineIndex(cellIndex, isNegativeDirection, horizontal);
			final Cell currentCell = board.get(cellIndex);
			if (isClosestNeighbour(player, i, currentCell)) {
				return cellIndex;
			} else if (isStoppingSearch(player, i, currentCell)) {
				return -1;
			}
		}
		return -1;
	}
	/**
	 * Возвращает значение, на которое следует изменять индекс текущего поля для продолжения
	 * движения.
	 * 
	 * @param cellIndex индекс поля.
	 * @param isNegativeDirection направление движения.
	 * @param horizontal является ли линия горизонталью.
	 * @return значение, на которое следует изменять индекс текущего поля для продолжения
	 * движения.
	 */
	private int incrementLineIndex(final int cellIndex, final boolean isNegativeDirection, final boolean horizontal) {
		if (horizontal) {
			return isNegativeDirection ? cellIndex - 1 : cellIndex + 1;
		}
		return isNegativeDirection ? cellIndex - Game.BOARD_COLUMN_COUNT : cellIndex + Game.BOARD_COLUMN_COUNT;
	}
	/**
	 * Возвращает индекс конца линии.
	 * 
	 * @param startCell поле начала поиска.
	 * @param isNegativeDirection направление поиска.
	 * @param horizontal является ли линия горизональю.
	 * @return индекс конца линии.
	 */
	private int getEndLineIndex(final Cell startCell, final boolean isNegativeDirection, final boolean horizontal) {
		if (horizontal) {
			return isNegativeDirection ? startCell.getX() + 1 : Game.BOARD_COLUMN_COUNT - startCell.getX();
		}
		return isNegativeDirection ? startCell.getY() + 1 : Game.BOARD_ROW_COUNT - startCell.getY();
	}

	/**
	 * Метод возвращает индекс поля, вплодь до которого ход игрока будет иметь
	 * результат или -1 для диагонали.
	 * 
	 * @param cell
	 *            игровое поле, от которого следует начинать вычисления.
	 * @param player
	 *            игрок.
	 * @param isNegativeDirection
	 *            положительное или отрицательное направление.
	 * @param main
	 *            главная (true) или второстепенная (false)
	 * @return индекс поля, вплодь до которого ход игрока будет иметь результат
	 *         или -1.
	 */
	private int getDiagonalNeighbourIndex(final Cell cell, final Player player, final boolean isNegativeDirection,
			final boolean main) {
		int cellIndex = cell.getIndex();
		int iteration = 1;
		// если можно продолжать движение
		if (!canDiagonalMove(isNegativeDirection, cellIndex, main))
			return -1;

		cellIndex = incrementDiagonalIndex(cellIndex, isNegativeDirection, main);
		// пока не достигнут конец диагонали
		while (!isDiagonalEnd(cellIndex, main)) {
			final Cell currentCell = board.get(cellIndex);
			if (isClosestNeighbour(player, iteration, currentCell)) {
				return cellIndex;
			} else if (isStoppingSearch(player, iteration, currentCell)) {
				return -1;
			}
			cellIndex = incrementDiagonalIndex(cellIndex, isNegativeDirection, main);

			++iteration;
		}

		Cell currentCell = board.get(cellIndex);
		return isClosestNeighbour(player, iteration, currentCell) ? cellIndex : -1;
	}
	/**
	 * Метод возращает true, если возможно движение по диагонали.
	 * 
	 * @param isNegativeDirection направление движения.
	 * @param cellIndex индекс поля.
	 * @param main является ли диагональ главной.
	 * @return true, если возможно движение по диагонали.
	 */
	private boolean canDiagonalMove(final boolean isNegativeDirection, int cellIndex, final boolean main) {
		boolean canMoveBottom = main
				? !(cellIndex / Game.BOARD_COLUMN_COUNT == Game.BOARD_COLUMN_COUNT - 1
						|| cellIndex % Game.BOARD_COLUMN_COUNT == Game.BOARD_COLUMN_COUNT - 1)
				: !(cellIndex % Game.BOARD_COLUMN_COUNT == 0
						|| cellIndex / Game.BOARD_COLUMN_COUNT == Game.BOARD_COLUMN_COUNT - 1);
		boolean canMoveTop = main
				? !(cellIndex / Game.BOARD_COLUMN_COUNT == 0 || cellIndex % Game.BOARD_COLUMN_COUNT == 0)
				: !(cellIndex / Game.BOARD_COLUMN_COUNT == 0
						|| cellIndex % Game.BOARD_COLUMN_COUNT == Game.BOARD_COLUMN_COUNT - 1);
		return !((!isNegativeDirection && !canMoveBottom) || (isNegativeDirection && !canMoveTop));
	}

	/**
	 * Метод возращает число, на которое следует изменять индекс поля по ходу
	 * движения.
	 * 
	 * @param cellIndex
	 *            индекс поля.
	 * @param isNegativeDirection
	 *            направление движения.
	 * @param main
	 *            является ли диагональ главной.
	 * @return число, на которое следует изменять индекс поля по ходу движения.
	 */
	private int incrementDiagonalIndex(final int cellIndex, final boolean isNegativeDirection, 
			                            final boolean main) {
		if (main) {
			return isNegativeDirection ? cellIndex - Game.BOARD_COLUMN_COUNT - 1
					: cellIndex + Game.BOARD_COLUMN_COUNT + 1;
		}
		return isNegativeDirection ? cellIndex - Game.BOARD_COLUMN_COUNT + 1 : cellIndex + Game.BOARD_COLUMN_COUNT - 1;
	}

	/**
	 * Возвращает true, если достигнут конец диагонали.
	 * 
	 * @param cellIndex
	 *            индекс поля.
	 * @param main
	 *            является ли диагональ главной.
	 * @return true, если достигнут конец диагонали.
	 */
	private boolean isDiagonalEnd(final int cellIndex, final boolean main) {
		boolean baseCheck = cellIndex % 8 == 0 || cellIndex / 8 == 0 || cellIndex % 8 == 7 || cellIndex / 8 == 7;
		if (main) {
			return baseCheck && cellIndex != 56 && cellIndex != 7;
		}
		return baseCheck && cellIndex != 0 && cellIndex != 63;
	}

	/**
	 * Возвращает true, если следует остановить поиск.
	 * 
	 * @param player
	 *            игрок.
	 * @param iteration
	 *            итерация по направлению.
	 * @param currentCell
	 *            текущее поле.
	 * @return true, если следует остановить поиск.
	 */
	private boolean isStoppingSearch(final Player player, int iteration, final Cell currentCell) {
		return currentCell.isEmpty() || (currentCell.isOwnedBy(player) && iteration == 1);
	}

	/**
	 * Возвращает true, если достигнут предел движения в направлении.
	 * 
	 * @param player
	 *            игрок.
	 * @param iteration
	 *            итерация по направлению.
	 * @param currentCell
	 *            текущее поле.
	 * @return true, если достигнут предел движения в направлении.
	 */
	private boolean isClosestNeighbour(final Player player, int iteration, final Cell currentCell) {
		return currentCell.isOwnedBy(player) && (iteration > 1 || player == Player.NOBODY);
	}
}
