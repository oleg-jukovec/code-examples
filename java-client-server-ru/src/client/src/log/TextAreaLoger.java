/*
 * Файл TextAreaLoger.java
 */
package log;

import javax.swing.JTextArea;
/**
 * Класс предоставляет простейшую возможность логирования сообщений
 * в поле JTextArea
 */
public class TextAreaLoger extends Loger {
	// объект на поле
	private JTextArea area;
	// конструктор
	public TextAreaLoger(JTextArea area) {
		this.area = area;
	}
	@Override
	public void message(Level level, String message) {
		// получаем полный текст сообщения
		String fullMessage = level.getString() + ' ' + message;
		// добавляем сообщение в конец текстового поля
		area.append(fullMessage + '\n');
		// устанавливаем каретку на конец текстового поля
		// (проматывает JTextArea в JScrollPane)
		area.setCaretPosition(area.getDocument().getLength());
	}
}