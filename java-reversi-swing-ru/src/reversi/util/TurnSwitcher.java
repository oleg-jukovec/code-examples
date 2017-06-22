/*
 * Файл TurnSwitcher.java
 */
package reversi.util;

import java.util.concurrent.Semaphore;

/**
 * Класс используется для переключение ходов и блокирует выполнение потока.
 * 
 */
public class TurnSwitcher {
	/**
	 * Начальное значение семафора.
	 */
	private static final int MUTEX_INITIAL_LOCKS = 0;
	/**
	 * Семафор.
	 */
	private final Semaphore mutex;

	/**
	 * Конструктор создаёт обьект и не блокирует поток выполнения.
	 */
	public TurnSwitcher() {
		mutex = new Semaphore(MUTEX_INITIAL_LOCKS);
	}

	/**
	 * Метод блокирует поток выполнения.
	 */
	public void startTurn() {
		try {
			mutex.acquire();
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Метод разблокирует поток выполнения.
	 */
	public void endTurn() {
		mutex.release();
	}

}
