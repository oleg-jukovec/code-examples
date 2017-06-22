/*
 * Файл Loger.java
 */
package log;
/**
 * Класс предоставляет интерфейс для дальнейшей реализации 
 * простейшей системы логирования
 */
abstract public class Loger {
	/**
	 * Возможные типы сообщений
	 */
	public static enum Level{
		ERROR("Ошибка:"), MESSAGE("Работа сервера:");
		private String str;
		Level(String str){
			this.str = str;
		}
		public String getString(){
			return str;
		}
	}
	/**
	 * Логирования сообщения
	 * 
	 * @param level тип сообщения
	 * @param message сообщение
	 */
	abstract public void message(Level level, String message);
}
