/*
 * Файл ClientThread.java
 */
package net;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import log.Loger;
import log.Loger.Level;

/**
 * Класс позволяет работать с отдельным подключением в отдельном потоке
 */
public class ClientThread extends Thread {
	// сокет
	private Socket socket;
	// объект логирования
	private Loger loger;
	// список файлов, доступных для загрузки
	private File  files[];
	// входной поток данных
	private BufferedInputStream sin;
	// выходной поток данных
	private ObjectOutputStream oout;
	/**
	 * Создаёт новый объект
	 * 
	 * @param socket сокет подключения
	 * @param files список файлов, доступных для загрузки
	 * @param loger объект логирования
	 * @throws IOException в случае ошибки инициализации входных/выходных потоков
	 */
	public ClientThread(Socket socket, File files[], Loger loger) throws IOException {
		// если аргументы заданы неверно
		if (socket == null || loger == null || files == null)
			// выбрасываем исключение
			throw new IllegalArgumentException();
		this.socket = socket;
		this.loger = loger;
		this.files = files;
		// задаём ссылку на входной поток данных
		sin = new BufferedInputStream(socket.getInputStream());
		// задаёмссылку на выходной поток данных
		oout = new ObjectOutputStream(socket.getOutputStream());
		// запускаем обработку сообщений клиента в отдельном потоке
		this.start();
	}
	/**
	 * Обработка сообщений клиента в отдельном потоке
	 */
	@Override
	public void run() {
		// код операции
		int operation = 0;
		try {
			operation = sin.read();
			// если клиент отправил запрос на подключение
			if (operation == Protocol.CONNECT) {
				loger.message(Level.MESSAGE, "Клиент удачно подключён");
				// отправляем сообщение об удачномсоединении
				oout.write(Protocol.CONNECT_SUCCESS);
				oout.flush();
				// чтение и обработка сообщений в цикле
				do {
					// чтение сообщения
					operation = sin.read();
					// определяем тип сообщения
					switch (operation) {
					case Protocol.GET_FILES:
												// передача списка файлов
						loger.message(Level.MESSAGE, "Передача списка файлов");
						String strings[] = new String[files.length];
						for(int i = 0; i < files.length; i++)
							strings[i] = files[i].getName();
						oout.writeObject(strings);
						oout.flush();
						break;
					case Protocol.GET_FILE:
						// передача определённого файла
						int number = sin.read();
						loger.message(Level.MESSAGE, "Указан номер файла: " + number);
						if(number >= files.length || number < 0)
							throw new IllegalArgumentException();
						byte buffer[] = new byte[1024];
						// передача клиенту размера файла
						long size = files[number].length();
						loger.message(Level.MESSAGE, "Передача файла размером " + size + "Кб начата");
						oout.writeLong(size);
						oout.flush();
						// передача файла клиенту
						int readed = 0;
						BufferedInputStream fin = new BufferedInputStream(new FileInputStream(files[number]));
						while( size > 0 ){
							readed = fin.read(buffer);
							size -= readed;
							oout.write(buffer, 0, readed);
						}
						oout.flush();
						fin.close();
						loger.message(Level.MESSAGE, "Передача файла закончена");
						break;
					case Protocol.HALT:
						// отключение от сервера
						break;
					default:
						// неверная операция
						throw new IllegalArgumentException();
					}
				} while (operation != Protocol.HALT);
			// иначе выбрасываем исключение
			} else {
				throw new IllegalArgumentException();
			}
		// обработка исключений
		} catch (IOException e) {
			loger.message(Level.MESSAGE, "Клиент отключился.");
		} catch (IllegalArgumentException e1) {
			loger.message(Level.ERROR,
					"Недопустимая операция с киента. Прерывание связи с клиентом");
		}
	}
	/**
	 * Закрывает соединени
	 */
	public void closeConnection() throws IOException {
		try{
			oout.write(Protocol.HALT);
			oout.flush();
			socket.close();
		} catch(IOException e){
			
		}
	}
}
