/*
 * Файд Server.java
 */
package net;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import log.Loger;
import log.Loger.Level;
/**
 * Класс реализует простейший tcp/ip сервер
 */
public class Server extends Thread {
	// сокет для подключения клиентов
	ServerSocket socket;
	// список файлов, доступных для загрузки
	File files[];
	// объект логирования
	Loger loger;
	/**
	 * Конструирование объекта 
	 * 
	 * @param port порт сервера
	 * @param files список файлов, доступных для загрузки
	 * @param loger объект логирования
	 * @throws IOException в случае неудачного открытия сокета
	 * @throws IllegalArgumentException в случае неверных аргументов
	 */
	public Server(int port, File files[], Loger loger) throws IOException,
			IllegalArgumentException {
		// если аргументы заданы неправильно
		if (files == null || loger == null)
			// выбрасываем исключение
			throw new IllegalArgumentException();
		// открываем сокет
		socket = new ServerSocket(port);
		// устанавливаем ссылки
		this.files = files;
		this.loger = loger;
		// запускаем поток
		this.start();
	}
	/**
	 * В потоке сервер ожидает подключения клиентов
	 */
	@Override
	public void run() {
		Socket client = null;
		try {
			// ожидаем подключения клиентов
			while (true){
				while (client == null) {
					client = socket.accept();
				}
				loger.message(Level.MESSAGE, "Подключение клиента");
				// запускаем работу с клиентов в отдельном потоке
				new ClientThread(client, files, loger);
				client = null;
			}
		// если возникла какая-либо исключительная ситуация
		// сервер прекращает свою работу
		} catch (Exception e) {
			loger.message(Level.MESSAGE, "Сервер был остановлен");
		}
	}
	/**
	 * Прекращение работы сервера
	 */
	public void closeSocket() {
		try{
			socket.close();
		}catch(IOException e){
			
		}
	}

}
