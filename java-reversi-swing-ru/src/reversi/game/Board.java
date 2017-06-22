/*
 * Файл Board.java
 */
package reversi.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import reversi.Game;
import reversi.listeners.ModelObserver;
import reversi.move.MoveChecker;
import reversi.util.CellTaker;

/**
 * Класс описывает игровую доску.
 *
 */
public class Board {
	/**
	 * Константа задаёт индекс центрального верхнего левого поля.
	 */
	private static final int POSITION_CENTER_TOP_LEFT = 27;
	/**
	 * Константа задаёт индекс центрального верхнего правого поля.
	 */
	private static final int POSITION_CENTER_TOP_RIGHT = 28;
	/**
	 * Константа задаёт индекс центрального нижнего левогополя.
	 */
	private static final int POSITION_CENTER_BOTTOM_LEFT = 35;
	/**
	 * Константа задаёт индекс центрального нижнего правого поля.
	 */
	private static final int POSITION_CENTER_BOTTOM_RIGHT = 36;
	/**
	 * Объект, который принимает сообщения от игровой доски.
	 */
	private final ModelObserver observer;
	/**
	 * Все игровые поля и их индексы.
	 */
	private final Map<Integer, Cell> board;
	/**
	 * Объект используется для проверки возможности хода.
	 */
	private final MoveChecker checker;
	/**
	 * Объект используется для передачи фишек другому игроку.
	 */
	private final CellTaker cellTaker;

	/**
	 * Конструктор создаёт новую игровую доску.
	 * 
	 * @param observer
	 *            объект, который будет принимать сообщения от доски.
	 */
	public Board(ModelObserver observer) {
		// инициализация и заполнение доски пустыми полями
		board = new LinkedHashMap<Integer, Cell>();
		for (int i = 0; i < Game.BOARD_ROW_COUNT; ++i) {
			for (int j = 0; j < Game.BOARD_COLUMN_COUNT; ++j) {
				final Cell currentCell = new Cell(j, i);
				board.put(currentCell.getIndex(), currentCell);
			}
		}
		// инициализация ссылок и объектов
		checker = new MoveChecker(this);
		cellTaker = new CellTaker(checker, this);
		this.observer = observer;
	}

	/**
	 * Передаёт игроку фишки начиная с индекса согласно правилам игры.
	 * 
	 * @param cellIndex
	 *            индекс фишки, с которой нужно начать передачу фишек.
	 * @param player
	 *            игрок, которому необходимо передать фишки.
	 */
	public void takeCell(final int cellIndex, final Player player) {
		// получаем список переданных фишек
		final Collection<Cell> takenCells = cellTaker.takeCell(cellIndex, player);
		// передаём список объекту, принимающему сообщения.
		observer.onBoardChanged(takenCells);
		observer.onResultChanged(getDiscCount(Player.WHITE), getDiscCount(Player.BLACK));
	}

	/**
	 * Возвращает может ли игрок поставить фишку в поле с заданным индексом.
	 * 
	 * @param cellIndex
	 *            индекс поля.
	 * @param player
	 *            игрок.
	 * @return true, если игрок может поставить фишку по заданному полю.
	 */
	public boolean isMovePermitted(final int cellIndex, final Player player) {
		return checker.isMovePermitted(cellIndex, player);
	}

	/**
	 * Метод передаёт сообщение о новом ходе игрока.
	 * 
	 * @param player
	 *            игрок, делающий следующий ход.
	 */
	public void nextMove(final Player player) {
		observer.onPlayerChanged(player);
		observer.onNextMovesAcquired(getNextMoves(player));
	}

	/**
	 * Метод возращает игровое поле, исходя из его индекса на доске.
	 * 
	 * @param cellIndex
	 *            индекс игрового поля на доске.
	 * @return игровое поле.
	 */
	public Cell get(final int cellIndex) {
		return board.get(cellIndex);
	}

	/**
	 * Метод возвращает игровое поле, исходя из его координат на доске.
	 * 
	 * @param x
	 *            координата по оси х.
	 * @param y
	 *            координата по оси y.
	 * @return игровое поле, с заданными координатами на доске.
	 */
	public Cell get(final int x, final int y) {
		return board.get(y * Game.BOARD_COLUMN_COUNT + x);
	}

	/**
	 * Возвращает список полей, доступные для хода игроку.
	 * 
	 * @param player
	 *            игрок.
	 * @return список полей, доступные для хода игроку.
	 */
	public Collection<Cell> getNextMoves(final Player player) {
		// инициализация пустого списка
		final List<Cell> result = new ArrayList<Cell>();
		// заполнение списка значениями
		for (int i = 0; i < board.size(); ++i) {
			if (isMovePermitted(i, player)) {
				result.add(new Cell(i));
			}
		}
		return result;
	}

	/**
	 * Метод задаёт начальные фишки в центре игровой доски.
	 */
	public void startGame() {
		takeCell(POSITION_CENTER_TOP_LEFT, Player.WHITE);
		takeCell(POSITION_CENTER_TOP_RIGHT, Player.BLACK);
		takeCell(POSITION_CENTER_BOTTOM_LEFT, Player.BLACK);
		takeCell(POSITION_CENTER_BOTTOM_RIGHT, Player.WHITE);
	}

	/**
	 * Метод проверяет, есть ли доступные хода у заданного игрока.
	 * 
	 * @param player
	 *            заданный игрок.
	 * @return true, если есть доступные хода.
	 */
	public boolean hasNextMove(final Player player) {
		return !getNextMoves(player).isEmpty();
	}

	/**
	 * Возвращает количество фишек на поле заданного игрока.
	 * 
	 * @param player
	 *            заданный игрок.
	 * @return количество фишек на поле заданного игрока.
	 */
	public int getDiscCount(Player player) {
		int count = 0;
		// подсчёт количества фишек
		for (final Cell cell : board.values()) {
			if (cell.isOwnedBy(player)) {
				++count;
			}
		}
		return count;
	}
}
