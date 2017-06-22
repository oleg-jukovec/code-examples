#ifndef CONSOLELOGER_H
#define CONSOLELOGER_H
/*
 * Файл consoleloger.h
 */
#include "loger.h"
/*
 * Класс ConsoleLoger расширяет класс Loger для вывода
 * сообщений на консоль
 */
class ConsoleLoger : public Loger
{
public:
    // конструктор класса ConsoleLoger
    ConsoleLoger(Loger::level lvl);
    // виртуальный деструктор
    virtual ~ConsoleLoger(){}
protected:
    // метод отправляет сообщение на консоль
    // не должен вызаваться непосредственно, только
    // через метод send_message
    virtual void send(QString message) const;
};
// общий объект, который может быть использован для
// вывода сообщений на консоль
extern ConsoleLoger loger;
#endif // CONSOLELOGER_H
