/*
 * Файл Runner.java
 */
import gui.ClientGui;
/**
 * Запуск программы на выполнение
 */
public class Runner {
	public static void main(String[] args) {
		// создание объекта графического интерфейса клиента
		final ClientGui gui = new ClientGui();
		// инициализация интерфейса
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				gui.createAndShowGUI();
			}
		});
	}
}