/*
 * Файл CellLayout.java
 */
package reversi.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import reversi.game.Player;

/**
 * Класс описывает графическое представление игрового поля на игровой доске.
 */
public class CellLayout extends JPanel {
	/**
	 * Сгенерированный serual ID для компонента, необходим для корректной работы
	 * объектов класса.
	 */
	private static final long serialVersionUID = -2422603891158269826L;
	/**
	 * Цвет подствеченного игрового поля.
	 */
	private static final String COLOR_HIGHLIGHTED = "0xCCCCCC";
	/**
	 * Цвет поля по умолчанию.
	 */
	private static final String COLOR_DEFAULT = "0xFFFFFF";
	/**
	 * Путь к изображению чёрной фишки.
	 */
	private static final String PATH_BLACK_DISC = "assets/black_disc.png";
	/**
	 * Путь к изображению белой фишки.
	 */
	private static final String PATH_WHITE_DISC = "assets/white_disc.png";
	/**
	 * Ширина отступа в пикселях.
	 */
	private static final int BOARDER_THICKNESS = 1;
	/**
	 * Высота поля в пикселях.
	 */
	public static final int HEIGHT = 70;

	/**
	 * Ширина поля в пикселях.
	 */
	public static final int WIDTH = 70;

	/**
	 * Перечисление задаёт возможные цвета для поля.
	 */
	private static enum CellColor {
		/**
		 * Пустое поле.
		 */
		EMPTY(COLOR_DEFAULT),
		/**
		 * Подствеченное поле.
		 */
		HIGHLIGHTED(COLOR_HIGHLIGHTED);
		/**
		 * Переменная, хранящая цвет.
		 */
		private final String сolor;

		/**
		 * Конструктор задаёт цвет.
		 * 
		 * @param color
		 *            цвет.
		 */
		private CellColor(final String color) {
			this.сolor = color;
		}

		/**
		 * Метод возвращает цвет поля.
		 * 
		 * @return цвет игрового поля.
		 */
		public Color getColor() {
			return Color.decode(сolor);
		}
	}

	/**
	 * Ссылка на объект изображения белой фишки.
	 */
	private static BufferedImage whiteDiscImage;
	/**
	 * Ссылкана объект изображения чёрной фишки.
	 */
	private static BufferedImage blackDiscImage;
	/**
	 * Переменная хранит игрока - владельца, поля.
	 */
	private Player cellOwner;
	/**
	 * Цвет поля.
	 */
	private CellColor cellColor;
	/**
	 * Загрузка изображений
	 */
	static{
		try {
			whiteDiscImage = ImageIO.read(new File(PATH_WHITE_DISC));
			blackDiscImage = ImageIO.read(new File(PATH_BLACK_DISC));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Конструктор создаёт новое графическое представление игрового поля.
	 */
	public CellLayout() {
		cellColor = CellColor.EMPTY;
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setVisible(true);
	}

	/**
	 * Метод отрисовывает графическое представление игровго поля.
	 */
	@Override
	protected void paintComponent(final Graphics graphics) {
		super.paintComponent(graphics);
		graphics.setColor(cellColor.getColor());
		graphics.fillRect(BOARDER_THICKNESS, BOARDER_THICKNESS, WIDTH, HEIGHT);
		graphics.drawImage(getCellImage(), BOARDER_THICKNESS, BOARDER_THICKNESS, WIDTH, HEIGHT, null);
	}

	/**
	 * Передача графического представления игрового поля новому вледельцу.
	 * 
	 * @param owner
	 *            новый владелец.
	 */
	public void take(final Player owner) {
		this.cellOwner = owner;
		repaint();
	}

	/**
	 * Метод посвечивает игровое поле.
	 */
	public void highlight() {
		cellColor = CellColor.HIGHLIGHTED;
		repaint();
	}

	/**
	 * Метод задаёт цвет поля по умолчанию.
	 */
	public void clearHighlight() {
		cellColor = CellColor.EMPTY;
		repaint();
	}

	/**
	 * Метод перерисовывает графический компонент.
	 */
	@Override
	public void repaint() {
		paintImmediately(0, 0, WIDTH, HEIGHT);
	}

	/**
	 * Метод возвращает ссылку на объект изображения игровой фишки, если поле
	 * принадлежит какому-либо игроку.
	 * 
	 * @return ссылка на объект изображения игровой фишки, если поле принадлежит
	 *         какому-либо игроку. Иначе null.
	 */
	private Image getCellImage() {
		if (cellOwner == Player.WHITE) {
			return whiteDiscImage;
		} else if (cellOwner == Player.BLACK) {
			return blackDiscImage;
		}
		return null;
	}
}
