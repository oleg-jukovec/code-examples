/*
 * Файл BoardLayout.java
 */
package reversi.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import reversi.Game;
import reversi.game.Cell;
import reversi.game.Player;
import reversi.listeners.BoardEventsListener;

/**
 * Класс реализует графический интерфейс для игровой доски.
 */
public class BoardLayout extends JPanel {
	/**
	 * Сгенерированный serial ID
	 */
	private static final long serialVersionUID = 5834762299789973250L;
	/**
	 * Принимает сообщения от игровой доски.
	 */
	private final BoardEventsListener eventsListener;
	/**
	 * Объект очищает выделение полей на доске.
	 */
	private final Runnable clearHighlightRunnable;

	/**
	 * Класс используется для регистрации выбора игрового поля с помощью мыши.
	 */
	private class CellMouseListener extends MouseAdapter {
		/**
		 * Индекс игрового поля.
		 */
		private final int cellIndex;

		/**
		 * Конструктор задаёт индекс прослушиваемого поля.
		 */
		public CellMouseListener(final int index) {
			cellIndex = index;
		}

		/**
		 * Реакция на клик мыши.
		 */
		@Override
		public void mouseClicked(final MouseEvent event) {
			eventsListener.onCellSelected(cellIndex);
		}

	}

	/**
	 * Класс реализует интерфейс Runnable для очистки полей игровой доски от
	 * прошлых выделений в отдельном потоке.
	 */
	private class ClearHighlightRunnable implements Runnable {

		@Override
		public void run() {
			clearCellHighlight();
		}
	}

	/**
	 * Класс реализует интерфейс Runnable для передачи поля игрока в отдельном
	 * потоке.
	 */
	private class TakeCellRunnable implements Runnable {
		/**
		 * Игровое поле.
		 */
		private final CellLayout cellLayout;
		/**
		 * Игрок.
		 */
		private final Player player;

		/**
		 * Конструктор создаёт новый объект с заданным полем и игроком.
		 * 
		 * @param cellLayout
		 *            заданное поле.
		 * @param player
		 *            игрок, которому будет передано поле.
		 */
		public TakeCellRunnable(final CellLayout cellLayout, final Player player) {
			this.cellLayout = cellLayout;
			this.player = player;
		}

		/**
		 * Передача поля осуществляется в отдельном потоке
		 */
		@Override
		public void run() {
			cellLayout.take(player);
		}
	}

	/**
	 * Класс реализует интерфейс Runnable для выделения игрового поля в
	 * отдельном потоке.
	 */
	private class HighlightCellRunnable implements Runnable {
		/**
		 * Индекс игрового поля.
		 */
		private final CellLayout cellLayout;

		/**
		 * Конструктор создаёт объект с заданным индексом.
		 * 
		 * @param cellLayout
		 */
		public HighlightCellRunnable(final CellLayout cellLayout) {
			this.cellLayout = cellLayout;
		}

		/**
		 * Выделение игрового поля в отдельном потоке.
		 */
		@Override
		public void run() {
			cellLayout.highlight();
		}
	}

	/**
	 * Конструктор создаёт новый графический интерфейс игрового поля, события
	 * обрабатывает объект listener.
	 * 
	 * @param listener
	 *            обрабатывает события от игрового поля.
	 */
	public BoardLayout(final BoardEventsListener listener) {
		super(new GridLayout(Game.BOARD_ROW_COUNT, Game.BOARD_COLUMN_COUNT));
		eventsListener = listener;
		clearHighlightRunnable = new ClearHighlightRunnable();
		setVisible(true);
		setPreferredSize(getBoardDimension());
		populateCells();
	}

	/**
	 * Метод вызывается при изменении состояния игровых полей.
	 * 
	 * @param changedCells
	 *            изменённые игровые поля.
	 */
	public void onModelChanged(final Collection<Cell> changedCells) {
		for (final Cell cell : changedCells) {
			SwingUtilities.invokeLater(new TakeCellRunnable(getCellAt(cell.getIndex()), cell.getOwner()));
		}
	}

	/**
	 * Метод подсвечивает доступные для хода поля игровой доски.
	 * 
	 * @param nextMoves
	 *            доступные для хода поля игровой доски.
	 */
	public void onNextMovesAcquired(final Collection<Cell> nextMoves) {
		SwingUtilities.invokeLater(clearHighlightRunnable);
		for (final Cell cell : nextMoves) {
			SwingUtilities.invokeLater(new HighlightCellRunnable(getCellAt(cell.getIndex())));
		}
	}

	/**
	 * Метод очищает какие-либо выделения полей на игровой доске.
	 */
	public void clearCellHighlight() {
		for (int i = 0; i < getComponentCount(); ++i) {
			final CellLayout boardCellLayout = (CellLayout) getComponent(i);
			boardCellLayout.clearHighlight();
		}
	}

	/**
	 * Возвращает графическое представление игрового поля по индексу.
	 * 
	 * @param index
	 *            индекс игрвого поля.
	 * @return графическое представление игрового поля по индексу.
	 */
	private CellLayout getCellAt(final int index) {
		return (CellLayout) getComponent(index);
	}

	/**
	 * Возращает размеры игровой доски.
	 * 
	 * @return размеры игровой доски.
	 */
	private Dimension getBoardDimension() {
		final Dimension boardDimension = new Dimension(Game.BOARD_COLUMN_COUNT * CellLayout.WIDTH,
				Game.BOARD_ROW_COUNT * CellLayout.HEIGHT);
		return boardDimension;
	}

	/**
	 * Метод заполняет графическое представление игрового поля.
	 */
	private void populateCells() {
		for (int i = 0; i < Game.BOARD_ROW_COUNT; ++i) {
			for (int j = 0; j < Game.BOARD_COLUMN_COUNT; ++j) {
				final CellLayout currentCell = new CellLayout();
				final int cellIndex = i * Game.BOARD_COLUMN_COUNT + j;
				// добавляем игровое поле
				add(currentCell, cellIndex);
				// добавляем обработчик событий мыши для поля
				currentCell.addMouseListener(new CellMouseListener(cellIndex));
			}
		}
	}
}
