/*
 * Файл ConnectionActionListener.java
 */
package gui;
/**
 * Интерфейс позволяет передавать события tcp/ip соединения
 */
public interface ConnectionActionListener {
	/**
	 * Соединение установлено
	 */
	public void connectionOpen();
	/**
	 * Соединение разорвано
	 */
	public void connectionClose();
	/**
	 * Начало загрузки файла
	 */
	public void downloadFileStart();
	/**
	 * Окончание загрузки файла
	 */
	public void downloadFileFinish();
}
