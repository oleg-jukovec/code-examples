/*
 * Файл Runner.java
 */
import gui.ServerGui;
/**
 * Запуск программы на выполнение
 */
public class Runner {
	public static void main(String[] args) {
		// создание объекта графического интерфейса сервера
		final ServerGui gui = new ServerGui();
		// инициализация интерфейса
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				gui.createAndShowGUI();
			}
		});
	}
}