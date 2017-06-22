/*
 * Класс GameLayout.java
 */
package reversi.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.util.Collection;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import reversi.game.Cell;
import reversi.game.Player;
import reversi.listeners.BoardEventsListener;
import reversi.listeners.ModelObserver;

/**
 * Класс реализует графический интерфейс пользователя для игры.
 */
public class GameLayout extends JFrame implements ModelObserver {
	/**
	 * Сгенерированный serial ID
	 */
	private static final long serialVersionUID = -9058837643235514898L;

	/**
	 * Высота окна программы.
	 */
	private static final int FRAME_HEIGHT = 580;

	/**
	 * Ширина окна программы.
	 */
	private static final int FRAME_WIDTH = 558;
	/**
	 * Имя окна программы.
	 */
	private static final String FRAME_NAME = "Реверси";
	/**
	 * Строка количества белых фишек.
	 */
	private static final String WHITE_SCORE = "Белых фишек: ";
	/**
	 * Строка количества чёрных фишек.
	 */
	private static final String BLACK_SCORE = "Чёрных фишек: ";
	/**
	 * Строка хода для чёрных фишек.
	 */
	private static final String TURN_BLACK = "Ход чёрных";
	/**
	 * Строка хода для белых фишек.
	 */
	private static final String TURN_WHITE = "Ход белых";
	/**
	 * Графический интерфейс игровой доски.
	 */
	private final BoardLayout boardLayout;
	/**
	 * Метка для вывода счёта чёрных фишек.
	 */
	private final JLabel blackPlayerScore;
	/**
	 * Метка для вывод счёта белых фишек.
	 */
	private final JLabel whitePlayerScore;
	/**
	 * Метка для вывода текущего хода.
	 */
	private final JLabel turnLabel;

	/**
	 * Конструктор создаёт графический интерфейс программы.
	 * 
	 * @param listener
	 *            используется для передачи сообщений игровой доски.
	 */
	public GameLayout(final BoardEventsListener listener) {
		super(FRAME_NAME);
		// создаём и добавляем графический интерфейс игровой доски
		setLayout(new BorderLayout(0, 0));
		boardLayout = new BoardLayout(listener);
		final Container container = getContentPane();
		container.add(boardLayout, BorderLayout.CENTER);
		// создаём и добавляем информацию о ходе игры
		JPanel resultsPanel = new JPanel();
		resultsPanel.setLayout((new BoxLayout(resultsPanel, BoxLayout.LINE_AXIS)));
		blackPlayerScore = new JLabel();
		whitePlayerScore = new JLabel();
		turnLabel = new JLabel();
		resultsPanel.add(blackPlayerScore, BorderLayout.SOUTH);
		resultsPanel.add(Box.createHorizontalGlue());
		resultsPanel.add(turnLabel, BorderLayout.NORTH);
		resultsPanel.add(Box.createHorizontalGlue());
		resultsPanel.add(whitePlayerScore, BorderLayout.CENTER);
		container.add(resultsPanel, BorderLayout.PAGE_END);
		// устанавливаем размер окна
		setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		// инициализируем графический интерфейс
		pack();
		setVisible(true);
		// запрещаем изменение размера окна
		setResizable(false);
		// устанавливаем операцию для закрытия окна
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see reversi.listeners.ModelObserver#onBoardChanged(java.util.Collection)
	 */
	@Override
	public void onBoardChanged(Collection<Cell> changedCells) {
		boardLayout.onModelChanged(changedCells);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * reversi.listeners.ModelObserver#onNextMovesAcquired(java.util.Collection)
	 */
	@Override
	public void onNextMovesAcquired(Collection<Cell> nextMoves) {
		boardLayout.onNextMovesAcquired(nextMoves);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see reversi.listeners.ModelObserver#onResultChanged(int, int)
	 */
	@Override
	public void onResultChanged(int whiteDiscs, int blackDiscs) {
		whitePlayerScore.setText(WHITE_SCORE + whiteDiscs);
		blackPlayerScore.setText(BLACK_SCORE + blackDiscs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see reversi.listeners.ModelObserver#onPlayerChanged(reversi.game.Player)
	 */
	@Override
	public void onPlayerChanged(Player player) {
		turnLabel.setText(player == Player.WHITE ? TURN_WHITE : TURN_BLACK);
	}

	/**
	 * Метод вызывается при завершении игры. Очищает все выделения полей с
	 * графического интерфейса игровой доски.
	 */
	public void endGame() {
		boardLayout.clearCellHighlight();
	}
}
