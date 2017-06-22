/*
 * Файл ServerGui.java
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import net.Server;

import log.TextAreaLoger;
import log.Loger.Level;
/**
 * Класс позволяет создать объект графического интерфейса сервера
 */
public class ServerGui {
	// путь к папке для синхронизации по умолчанию
	private final static String DEFAULT_PATH = "Папка не задана";
	// порт по умолчанию
	private final static int DEFAULT_PORT = 12345;
	// путь к папке для синхронизации
	private String path = DEFAULT_PATH;
	// порт 
	private int port = DEFAULT_PORT;
	// объект логирования
	private TextAreaLoger loger;
	// tcp/ip сервер
	private Server server;
	// запущен ли сервер
	private boolean serverStarted;
	// главное окно
	private JFrame frame = new JFrame("Сервер");
	// кнопка изменения папки для синхронизации и реакция на её нажатие
	private JButton pathChangeButton = new JButton("Изменить");
	private ActionListener pathChangeListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser
					.setDialogTitle("Выберите папку с файлами для синхронизации");
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.showOpenDialog(frame);

			try {
				File file = fileChooser.getSelectedFile();
				File files[] = file.listFiles(new FileFilter(){
					@Override
					public boolean accept(File arg0) {
						return arg0.isFile();
					}						
				});
				if (files.length > 0){
					path = file.getPath();
					for(File x : files)
						loger.message(Level.MESSAGE, "В новой папке для загрузки доступен файл " + x.getName());
				}else {
					path = DEFAULT_PATH;
					loger.message(Level.ERROR, "В папке нет ни одного файла");
				}
			} catch (NullPointerException e) {
				loger.message(Level.ERROR, "Папка не была выбрана.");
				path = DEFAULT_PATH;
			} finally {
				dirLabel.setText(path);
			}
		}
	};
	// кнопка изменения порта сервера и реакция на её нажатие
	private JButton portChangeButton = new JButton("Изменить");
	private ActionListener portChangeListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				port = Integer.parseInt(portTextField.getText());
				if (port < 0 || port > 65535)
					throw new Exception();
				loger.message(Level.MESSAGE, "Рабочий порт изменён на " + port);
			} catch (Exception e) {
				loger.message(Level.ERROR, "Рабочий порт сервера задан неверно");
				loger.message(Level.ERROR,
						"Использован адрес рабочего порта сервера по умолчанию - "
								+ DEFAULT_PORT);
				port = DEFAULT_PORT;
			} finally {
				portTextField.setText(Integer.toString(port));
			}
		}
	};
	// кнопка запуск/остановки сервера и реакция на её нажатие
	private JButton startStopButton = new JButton("Запуск");
	private ActionListener startStopListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			File dir = new File(path);
			if (!dir.isDirectory()) {
				loger.message(Level.ERROR,
						"Неверно задана папка для синхронизации");
				return;
			}
			try {
				// остановка сервера
				if (serverStarted) {
					server.closeSocket();
					serverStarted = false;
				// запуск сервера
				} else {
					File files[] = (new File(path)).listFiles(new FileFilter(){
						@Override
						public boolean accept(File arg0) {
							return arg0.isFile();
						}						
					});
					try {
						server = new Server(port, files, loger);
					} catch (IOException e) {
						throw new IllegalArgumentException();
					}
					if (server.isAlive())
						serverStarted = true;
					else
						loger.message(Level.ERROR,
								"Не удалось запустить сервер");
				}
			} catch (IllegalArgumentException e2) {
				loger.message(Level.ERROR,
						"Не удалось запустить сервер. Проверьте настройки.");
			} finally {
				if (serverStarted) {
					topDisable();
					startStopButton.setText("Остановка");
					loger.message(Level.MESSAGE, "Сервер запущен.");
				} else {
					topEnable();
					startStopButton.setText("Запуск");
					loger.message(Level.MESSAGE, "Сервер остановлен.");
				}
			}
		}
	};
	/*
	 * Различные элементы интерфейса
	 */
	private JLabel pathLabel = new JLabel("Папка для синхронизации:");
	private JLabel portLabel = new JLabel("Порт сервера:");
	private JLabel dirLabel = new JLabel(DEFAULT_PATH);

	private JTextField portTextField = new JTextField(Integer.toString(port));

	private JPanel topPanel = new JPanel(new GridBagLayout());

	private JTextArea messagesTextArea = new JTextArea(10, 30);
	/**
	 * Создание нового объекта
	 */
	public ServerGui() {
		loger = new TextAreaLoger(messagesTextArea);
		server = null;
		serverStarted = false;

		pathChangeButton.addActionListener(pathChangeListener);
		portChangeButton.addActionListener(portChangeListener);
		startStopButton.addActionListener(startStopListener);
	}
	/**
	 * Метод задаёт расположение элементов интерфейса
	 * 
	 * @param pane главный контейнер
	 */
	public void addComponentsToPane(Container pane) {
		/*
		 * Конструирование верхней области
		 */
		topPanel = new JPanel(new GridBagLayout());
		topPanel.setBorder(BorderFactory
				.createTitledBorder("Настройки сервера"));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.weighty = 1;

		c.ipadx = 10;
		c.gridx = 0;
		c.gridy = 0;
		topPanel.add(pathLabel, c);

		dirLabel.setMaximumSize(new Dimension(300, 30));
		dirLabel.setSize(300, 30);
		dirLabel.setPreferredSize(new Dimension(300, 30));
		c.ipadx = 0;
		c.gridx = 1;
		c.gridy = 0;
		topPanel.add(dirLabel, c);

		c.gridx = 2;
		c.gridy = 0;
		topPanel.add(pathChangeButton, c);

		c.ipadx = 10;
		c.gridx = 0;
		c.gridy = 1;
		topPanel.add(portLabel, c);

		c.ipadx = 0;
		c.gridx = 1;
		c.gridy = 1;
		topPanel.add(portTextField, c);

		c.gridx = 2;
		c.gridy = 1;
		topPanel.add(portChangeButton, c);

		pane.add(topPanel, BorderLayout.NORTH);
		/*
		 * Конструирование центральной области
		 */
		JPanel centerPanel = new JPanel(new GridLayout());
		centerPanel.setBorder(BorderFactory
				.createTitledBorder("Работа сервера"));
		centerPanel.add(startStopButton);
		pane.add(centerPanel, BorderLayout.CENTER);
		/*
		 * Конструирование нижней области
		 */
		JPanel footerPanel = new JPanel(new GridLayout());
		messagesTextArea.setEditable(false);
		final JScrollPane scrollPane = new JScrollPane(messagesTextArea);
		scrollPane.setMinimumSize(new Dimension(300, 100));
		scrollPane.setAutoscrolls(true);
		scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		footerPanel.setBorder(BorderFactory
				.createTitledBorder("Сообщения сервера"));
		footerPanel.add(scrollPane);
		pane.add(footerPanel, BorderLayout.SOUTH);
	}

	/**
	 * Метод создаёт главный фрейм и располагает на нём элементы
	 */
	public void createAndShowGUI() {
		// создаём новое окно
		frame = new JFrame("Сервер");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addComponentsToPane(frame.getContentPane());
		frame.pack();
		// запрещаем изменение размеров
		frame.setResizable(false);
		frame.setVisible(true);
	}
	// "отключаем" верхнюю часть интерфейса
	private void topDisable() {
		topPanel.setEnabled(false);
		pathChangeButton.setEnabled(false);
		portChangeButton.setEnabled(false);
		pathLabel.setEnabled(false);
		dirLabel.setEnabled(false);
		portLabel.setEnabled(false);
		portTextField.setEnabled(false);
	}
	// "включаем" верхнюю часть интерфейса
	private void topEnable() {
		topPanel.setEnabled(true);
		pathChangeButton.setEnabled(true);
		portChangeButton.setEnabled(true);
		pathLabel.setEnabled(true);
		dirLabel.setEnabled(true);
		portLabel.setEnabled(true);
		portTextField.setEnabled(true);
	}
}