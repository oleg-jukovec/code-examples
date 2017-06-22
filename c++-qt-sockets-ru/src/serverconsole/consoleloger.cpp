/*
 * Файл consoleloger.cpp
 */
#include "consoleloger.h"
#include <iostream>
// конструктор класса ConsoleLoger
ConsoleLoger::ConsoleLoger(Loger::level lvl) : Loger(lvl)
{

}
// метод отправляет сообщение на консоль
void ConsoleLoger::send(QString message) const{
    std::cout << message.toStdString() << std::endl;
}
// инициализируем общий объект для вывода на консоль
// сообщений от сервера
ConsoleLoger loger(Loger::DEBUG);
