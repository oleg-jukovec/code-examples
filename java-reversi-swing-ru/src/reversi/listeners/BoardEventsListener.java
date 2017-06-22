/*
 * Файл BoardEventsListener.java
 */
package reversi.listeners;

/**
 * Интерфейс предназначен для передачи сообщения о том, что поле на доске было
 * выбрано.
 */
public interface BoardEventsListener {
	/**
	 * Метод должен принимать сообщения о выбранной фишке с заданным индексом.
	 * 
	 * @param cellIndex
	 *            индекс выбранной фишки.
	 */
	void onCellSelected(final int cellIndex);
}
