/*
 * Файл Game.java
 */
package reversi;

import javax.swing.JOptionPane;

import reversi.game.Board;
import reversi.game.Player;
import reversi.gui.GameLayout;
import reversi.listeners.BoardEventsListener;
import reversi.util.TurnSwitcher;

/**
 * Класс Game предоставляет основной функционал для работы приложения.
 */
public class Game implements BoardEventsListener {

	/**
	 * Строка разделитель между счетами игроков.
	 */
	private static final String TEXT_SLASHER = " : ";

	/**
	 * Строка начала информации о счёте.
	 */
	private static final String TEXT_WINNER_COUNT = " со счётом ";

	/**
	 * Строка победитель.
	 */
	private static final String TEXT_WINNER = "Победили ";

	/**
	 * Количество строк на доске.
	 */
	public static final int BOARD_ROW_COUNT = 8;

	/**
	 * Количество колонок на доске.
	 */
	public static final int BOARD_COLUMN_COUNT = 8;

	/**
	 * Графический интерфейс игры.
	 */
	private final GameLayout gameLayout;

	/**
	 * Доска для игры.
	 */
	private final Board board;

	/**
	 * Текущий игрок.
	 */
	private Player currentPlayer;

	/**
	 * Переключатель ходов.
	 */
	private final TurnSwitcher turnSwitcher;

	/**
	 * Конструктор создаёт новую игру
	 */
	public Game() {
		// инициализация графического интерфейса
		gameLayout = new GameLayout(this);
		// инициализация доски для игры
		board = new Board(gameLayout);
		// инициализация переключателя ходов
		turnSwitcher = new TurnSwitcher();
		// начало игры с чёрных фишек
		currentPlayer = Player.BLACK;
		// старт игры
		board.startGame();
	}

	/**
	 * Метод проверяет, закончена ли игра.
	 * 
	 * @return true, если игра закончена или false в обратном случае.
	 */
	public boolean isFinished() {
		return !board.hasNextMove(Player.WHITE) && !board.hasNextMove(Player.BLACK);
	}

	/**
	 * Делает ход чёрными фишками.
	 */
	public void nextBlackMove() {
		// переключает ход на чёрные фишки
		currentPlayer = Player.BLACK;
		// если на доске можно сделать ход игроком
		if (board.hasNextMove(currentPlayer)) {
			// сообщаем о начале хода
			board.nextMove(currentPlayer);
			// блокируем поток выполнения
			turnSwitcher.startTurn();
		}
	}

	/**
	 * Делает ход белыми фишками.
	 */
	public void nextWhiteMove() {
		// переключает ход на чёрные фишки
		currentPlayer = Player.WHITE;
		// если на доске можно сделать ход игроком
		if (board.hasNextMove(currentPlayer)) {
			// сообщаем о начале хода
			board.nextMove(currentPlayer);
			// блокируем поток выполнения
			turnSwitcher.startTurn();
		}
	}

	/**
	 * Метод вызывается графическим интерфейсом, когда пользователь выбирает
	 * место для фишки на доске.
	 */
	@Override
	public void onCellSelected(final int cellIndex) {
		// если можно сделать ход
		if (isLegalMove(cellIndex)) {
			// делаем ход
			board.takeCell(cellIndex, currentPlayer);
			// снимаем блокировку с потока выполнения
			turnSwitcher.endTurn();
		}
	}

	/**
	 * Метод показывает диалог об окончании игры.
	 */
	public void showWinner() {
		// количество белых фишек на доске
		final int whiteDiscs = board.getDiscCount(Player.WHITE);
		// количество чёрных фишек на доске
		final int blackDiscs = board.getDiscCount(Player.BLACK);
		// задаём строку с именем победителя
		final String winner = whiteDiscs > blackDiscs ? "белые" : "чёрные";
		// вычисляем количество фишек победителя
		int winnerDiscs = whiteDiscs > blackDiscs ? whiteDiscs : blackDiscs;
		// вычисляем количество фишек проигравшего
		int looseDiscs = whiteDiscs > blackDiscs ? blackDiscs : whiteDiscs;
		// выводим диалог с именем победителя и счётом
		JOptionPane.showMessageDialog(gameLayout,
				TEXT_WINNER + winner + TEXT_WINNER_COUNT + winnerDiscs + TEXT_SLASHER + looseDiscs);
		// графический интерфейс завершает игру
		gameLayout.endGame();
	}

	/**
	 * Метод проверяет, можно ли сделать ход на место ячейки.
	 * 
	 * @param cellIndex
	 *            индекс ячейки
	 * @return true, если да или false, в обратном случае.
	 */
	private boolean isLegalMove(final int cellIndex) {
		return board.isMovePermitted(cellIndex, currentPlayer);
	}
}
