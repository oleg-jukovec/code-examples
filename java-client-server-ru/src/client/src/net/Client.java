/*
 * Файл Client.java
 */
package net;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import gui.ConnectionActionListener;
import log.Loger;
import log.Loger.Level;
/**
 * Класс Client предоставляет простой tcp/ip клиент. Подключается к серверу, запрашивает
 * список файлов для закачки и закачивает файл. Первичное взаимодействие с сервером должно
 * происходить в новом потоке (метод run запускается this.start())
 */
public class Client extends Thread {
	// сокет для работы
	private Socket socket;
	// объект для логирования сообщений
	private Loger loger;
	// выходной поток данных
	private BufferedOutputStream sout;
	// входной поток данных
	private ObjectInputStream oin;
	// список имён файлов, доступных для загрузки
	private String files[];
	// объект передающий события клиента во вне 
	private ConnectionActionListener listener;
	/**
	 * Конструирует готовый объект. Подключается к серверу, но не начинает
	 * отправлять сообщения.
	 * 
	 * @param host адрес сервера
	 * @param port порт сервера
	 * @param loger объект для логирования
	 * @throws UnknownHostException в случае неудачи поиска хоста
	 * @throws IOException в случае неудачного подключения
	 * @throws IllegalArgumentException в случае неверно заданных аргументов
	 */
	public Client(String host, int port, Loger loger)
			throws UnknownHostException, IOException, IllegalArgumentException {
		// если аргументы заданы неверно
		if (host == null || loger == null)
			// выбрасываем исключение
			throw new IllegalArgumentException();
		// открываем сокет
		socket = new Socket(host, port);
		// получаем ссылки на поля
		this.loger = loger;
		oin = new ObjectInputStream(socket.getInputStream());
		sout = new BufferedOutputStream(socket.getOutputStream());
	}
	/**
	 * Первичный обмен данными с сервером. Не должен запускаться непосредственно.
	 * Только с помощью this.start() в новом потоке.
	 */
	@Override
	public void run() {
		try {
			socketStarted();
			loger.message(Level.MESSAGE, "Подключение к серверу.");
			// отправляем серверу запрос на подключение
			sout.write(Protocol.CONNECT);
			sout.flush();
			loger.message(Level.MESSAGE, "Ожидание ответа сервера.");
			// получаем ответ сервера
			int operation = oin.read();
			// если сервер ответил согласно процедуре
			if (operation == Protocol.CONNECT_SUCCESS) {
				loger.message(Level.MESSAGE, "Удачное подключение к серверу.");
				// после удачного подключения запрашиваем список файлов доступных для загрузки
				sout.write(Protocol.GET_FILES);
				sout.flush();
				loger.message(Level.MESSAGE, "Получение списка файлов.");
				// получения списка имён файлов, доступных для загрузки
				files = (String[]) oin.readObject();
				// если не удалось получить список файлов
				if(files == null || files.length == 0)
					// выбрасываем исключение
					throw new IllegalArgumentException();
				// вывод объектом логировния список файлов, доступных для загрузки
				for (String x : files) {
					loger.message(Level.MESSAGE,
							"Можно загрузить файл: " + x);
				}
			// если сервер ответил неверно
			} else {
				loger.message(Level.ERROR, "Неудачное подключение к серверу.");
				closeSocket();
			}
		// обработка ошибок
		} catch (IOException e) {
			loger.message(Level.ERROR, "Отключение от сервера.");
			closeSocket();
		} catch (ClassNotFoundException | IllegalArgumentException e1) {
			loger.message(Level.ERROR, "Неудалось получить список файлов.");
			closeSocket();
		} catch (Exception e) {
			loger.message(Level.ERROR, "Неизвестная ошибка. Отключение от сервера.");
			closeSocket();
			throw e;
		}

	}
	/**
	 * Метод закрывает соединение с сервером
	 */
	public void closeSocket() {
		try {
			sout.write(Protocol.HALT);
			sout.flush();
			socket.close();
		} catch (Exception e) {
			// 
		} finally {
			socketStoped();
		}

	}
	/**
	 * Метод возвращает список файлов, доступных для загрузки
	 * 
	 * @return список файлов, доступных для загрузки
	 */
	public String[] getFileList() {
		return files;
	}
	/**
	 * Метод позволяет загрузить определённый файл.
	 * 
	 * @param number номер файла из списка файлов, доступных для загрузки
	 * @param file полный путь к файлу
	 */
	public void downloadFile(int number, File file) {
		try {
			// если аргументы заданы неверно
			if (number > files.length || number < 0 || file == null)
				// выбрасываем исключение
				throw new IllegalArgumentException();

			downloadStarted();

			// отправляем запрос серверу на скачивание файла
			loger.message(Level.MESSAGE, "Передача файла начата");
			sout.write(Protocol.GET_FILE);
			sout.write(number);
			sout.flush();
			// получаем размер файла от сервера
			long size = oin.readLong();
			loger.message(Level.MESSAGE, "Сохранение файла размером " + (size / 1024) + "Кб");
			// передача файла по 1 кб
			byte buffer[] = new byte[1024];
			// открываем файл
			BufferedOutputStream fos = new BufferedOutputStream(
					new FileOutputStream(file));
			// сколько раз была произведена операция загрузки
			long loaded = 0;
			// шаг отображения процесса загрузки
			long step = (size / 1024) / 10;
			if(step == 0)
				step++;
			// сколько байт прочитано с сервера
			int readed;
			// загрузка файла и его запись
			while (size > 0) {
				readed = oin.read(buffer, 0,
						(int) (size > buffer.length ? buffer.length : size));
				fos.write(buffer, 0, readed);
				size -= readed;
				loaded++;
				if (loaded == step) {
					loger.message(Level.MESSAGE,
							"Сохранения файла, осталось загрузить ~" + (size
									/ 1024 ) + " Кб");
					loaded = 0;
				}
			}
			// сохранения и закрытие файла
			fos.flush();
			fos.close();
		// обработка ошибок
		} catch (Exception e) {
			loger.message(Level.ERROR, "Ошибка загрузки файла");
		// в любом случае сообщаем об окончании передачи файлов
		} finally {
			loger.message(Level.MESSAGE, "Передача файла закончена");
			downloadStoped();
		}
	}
	/**
	 * Метод устанавливает объект для передачи событий клиента
	 * 
	 * @param listener ссылка на объект для передачи событий клиента
	 */
	public void setConnectionActionListener(ConnectionActionListener listener) {
		this.listener = listener;
	}
	/**
	 * Метод возвращает true если объект для передачи событий был задан ранее
	 * и false в обратном случае
	 * @return true если объект для передачи событий был задан ранее
	 * и false в обратном случае
	 */
	private boolean isConnectionListenerSet() {
		return listener != null;
	}
	/**
	 * Метод сообщает о начале загрузки файла.
	 */
	private void downloadStarted() {
		if (isConnectionListenerSet())
			listener.downloadFileStart();
	}
	/**
	 * Метод сообщает об окончании загрузки файла
	 */
	private void downloadStoped() {
		if (isConnectionListenerSet())
			listener.downloadFileFinish();
	}
	/**
	 * Метод сообщает об установке соединения с сервером
	 */
	private void socketStarted() {
		if (isConnectionListenerSet())
			listener.connectionOpen();
	}
	/**
	 * Метод сообщает об окончании соединения с сервером
	 */
	private void socketStoped() {
		if (isConnectionListenerSet())
			listener.connectionClose();
	}
}