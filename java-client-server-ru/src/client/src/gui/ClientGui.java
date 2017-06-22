/*
 * Файл ClientGui.java
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
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import net.Client;
import log.TextAreaLoger;
import log.Loger.Level;
/**
 * Класс позволяет создать объект графического интерфейса клиента
 */
public class ClientGui {
	// путь к папке для синхронизации по умолчанию
	private final static String DEFAULT_PATH = "Папка не задана";
	// порт по умолчанию
	private final static int DEFAULT_PORT = 12345;
	// адрес сервера по умолчанию
	private final static String DEFAULT_HOST = "localhost";
	// путь к папке для синхронизации
	private String path = DEFAULT_PATH;
	// номер порта сервера
	private int port = DEFAULT_PORT;
	// адрес сервера
	private String host = DEFAULT_HOST;
	// tcp/ip клиент
	private Client client = null;
	// объект для прослушивания события tcp/ip клиента
	private ConnectionActionListener cListener = null;
	// основной фрейм
	private JFrame frame = new JFrame("Сервер");
	// было ли установлено соединение с сервером
	private boolean connected = false;
	
	// кнопка изменения папки и реакция на нажатие
	private JButton pathChangeButton = new JButton("Изменить");
	private ActionListener pathChangeListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Выберите папку для сохранения файлов");
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.showOpenDialog(frame);

			try {
				File file = fileChooser.getSelectedFile();
				path = file.getPath();
			} catch (NullPointerException e) {
				loger.message(Level.ERROR, "Папка не была выбрана.");
				path = DEFAULT_PATH;
			} finally {
				dirLabel.setText(path);
			}

		}
	};

	// кнопка изменения порта сервера и реакция на нажатие
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
	// кнопка изменения адреса сервера и реакция на нажатие
	private JButton hostChangeButton = new JButton("Изменить");
	private ActionListener hostChangeListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			String newHost = hostTextField.getText();
			try {
				if (newHost.length() == 0)
					throw new Exception();
				host = newHost;
			} catch (Exception e) {
				loger.message(Level.ERROR,
						"Адрес сервера не должно быть пустым");
				host = DEFAULT_HOST;
			} finally {
				hostTextField.setText(host);
				loger.message(Level.MESSAGE, "Адрес сервера изменён на: "
						+ host);
			}
		}
	};
	// кнопка покдлючения к серверу и реакция на нажатие
	private JButton startStopButton = new JButton("Подключиться");
	private ActionListener startStopListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			File dir = new File(path);
			if (!dir.isDirectory()) {
				loger.message(Level.ERROR,
						"Неверно задана папка для синхронизации");
				loger.message(Level.ERROR,
						"Соединение с сервером не было установлено");
				return;
			}

			if (!connected) {
				try {
					client = new Client(host, port, loger);
					client.setConnectionActionListener(cListener);
					client.start();
				} catch (UnknownHostException e) {
					loger.message(Level.ERROR, "Невозможно найти сервер.");
					loger.message(Level.ERROR,
							"Соединение с сервером не было установлено");
				} catch (IllegalArgumentException e) {
					loger.message(Level.ERROR,
							"Неправильно заданы параметры подключения");
					loger.message(Level.ERROR,
							"Соединение с сервером не было установлено");
				} catch (IOException e) {
					loger.message(Level.ERROR, "Ошибка ввода/вывода.");
					loger.message(Level.ERROR,
							"Соединение с сервером не было установлено");
				} catch (Exception e) {
					loger.message(Level.ERROR,
							"Соединение с сервером не было установлено");
				}
			} else {
				client.closeSocket();
				client = null;
			}
		}
	};
	// кнопка загрузки файла и реакция на нажатие
	private JButton downloadButton = new JButton("Скачать файл");
	private ActionListener downloadListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			File dir = new File(path);
			if (!dir.isDirectory()) {
				loger.message(Level.ERROR,
						"Неверно задана папка для синхронизации");
				return;
			}

			try {
				// получаем список файлов
				String files[] = client.getFileList();
				String file = (String) JOptionPane.showInputDialog(frame,
						"Файлы, которые можно загрузить:",
						"Выберите файл для загрузки",
						JOptionPane.QUESTION_MESSAGE, null, files, files[0]);
				// если файл не выбран
				if (file == null) {
					loger.message(Level.ERROR, "Файл для загрузки не выбран");
				// загрузку файла
				} else {
					final File download = new File(dir.getAbsolutePath()
							+ File.separator + file);
					loger.message(Level.MESSAGE, "Выбран файл: " + download);
					final int number = Arrays.asList(files).indexOf(file);

					Thread downloadThread = new Thread(new Runnable() {
						@Override
						public void run() {
							client.downloadFile(number, download);
						}
					});
					downloadThread.start();
				}
			} catch (Exception e) {
				loger.message(Level.ERROR,
						"Недостаточно данных для выбора файла.");
			}
		}
	};
	
	/*
	 * 
	 * Различные элементы интерфейса
	 *
	 */
	private JLabel pathLabel = new JLabel("Папка для синхронизации:");
	private JLabel portLabel = new JLabel("Порт сервера:");
	private JLabel hostLabel = new JLabel("Адрес сервера:");
	private JLabel dirLabel = new JLabel(DEFAULT_PATH);
	private JTextField portTextField = new JTextField(Integer.toString(port));
	private JTextField hostTextField = new JTextField(host);
	private JTextArea messagesTextArea = new JTextArea(10, 30);
	private JPanel topPanel = new JPanel(new GridBagLayout());
	// объект логирования
	private TextAreaLoger loger = new TextAreaLoger(messagesTextArea);
	/**
	 * Конструирование объекта
	 */
	public ClientGui() {
		pathChangeButton.addActionListener(pathChangeListener);
		portChangeButton.addActionListener(portChangeListener);
		hostChangeButton.addActionListener(hostChangeListener);
		startStopButton.addActionListener(startStopListener);
		downloadButton.addActionListener(downloadListener);
		// создание объекта для прослушивания событий клиента
		cListener = new ConnectionActionListener() {
			@Override
			public void connectionOpen() {
				loger.message(Level.MESSAGE,
						"Соединение с сервером установлено.");
				connected = true;
				topDisable();
				downloadEnable();
				startStopButton.setText("Отключиться");
			}
			@Override
			public void connectionClose() {
				loger.message(Level.MESSAGE, "Соединение с сервером разорвано.");
				connected = false;
				topEnable();
				downloadDisable();
				startStopButton.setText("Подключиться");
			}
			@Override
			public void downloadFileStart() {
				if (connected) {
					startStopDisable();
					downloadDisable();
				}
			}
			@Override
			public void downloadFileFinish() {
				if (connected) {
					startStopEnable();
					downloadEnable();
				}
			}
		};
		connected = false;
		topEnable();
		downloadDisable();
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
		topPanel.setBorder(BorderFactory.createTitledBorder("Настройки клиент"));

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
		topPanel.add(hostLabel, c);

		c.ipadx = 0;
		c.gridx = 1;
		c.gridy = 1;
		topPanel.add(hostTextField, c);

		c.gridx = 2;
		c.gridy = 1;
		topPanel.add(hostChangeButton, c);

		c.ipadx = 10;
		c.gridx = 0;
		c.gridy = 2;
		topPanel.add(portLabel, c);

		c.ipadx = 0;
		c.gridx = 1;
		c.gridy = 2;
		topPanel.add(portTextField, c);

		c.gridx = 2;
		c.gridy = 2;
		topPanel.add(portChangeButton, c);

		pane.add(topPanel, BorderLayout.NORTH);
		/*
		 * Конструирование центральной области
		 */
		JPanel centerPanel = new JPanel(new GridLayout());
		centerPanel.setBorder(BorderFactory
				.createTitledBorder("Работа с сервером"));
		centerPanel.add(startStopButton);
		centerPanel.add(downloadButton);
		pane.add(centerPanel, BorderLayout.CENTER);
		/*
		 * Конструирование нижней области
		 */
		JPanel footerPanel = new JPanel(new GridLayout());
		messagesTextArea.setEditable(false);
		final JScrollPane scrollPane = new JScrollPane(messagesTextArea);

		messagesTextArea.setAutoscrolls(true);
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
		frame = new JFrame("Клиент");
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
		hostChangeButton.setEnabled(false);
		pathLabel.setEnabled(false);
		dirLabel.setEnabled(false);
		hostLabel.setEnabled(false);
		portLabel.setEnabled(false);
		portTextField.setEnabled(false);
		hostTextField.setEnabled(false);
	}
	// "включаем" верхнюю часть интерфейса
	private void topEnable() {
		topPanel.setEnabled(true);
		pathChangeButton.setEnabled(true);
		portChangeButton.setEnabled(true);
		hostChangeButton.setEnabled(true);
		pathLabel.setEnabled(true);
		dirLabel.setEnabled(true);
		portLabel.setEnabled(true);
		hostLabel.setEnabled(true);
		portTextField.setEnabled(true);
		hostTextField.setEnabled(true);
	}
	// "отключаем" кнопку загрузки
	private void downloadDisable() {
		downloadButton.setEnabled(false);
	}
	// "включаем" кнопку загрузки
	private void downloadEnable() {
		downloadButton.setEnabled(true);
	}
	// "включаем" кнопку подключения/отключения к серверу
	private void startStopDisable() {
		startStopButton.setEnabled(false);
	}
	// "отключаем" кнопку подключения/отключения к серверу
	private void startStopEnable() {
		startStopButton.setEnabled(true);
	}
}