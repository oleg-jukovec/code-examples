/*
 * Файл loger.cpp
 */
#include "loger.h"

// конструирует объект класса Loger
Loger::Loger(level new_lvl) : lvl(new_lvl)
{

}
// метод проверяет приоритет сообщения и если он ниже или
// равен приоритету объекта - отправляет сообщение
void Loger::send_message(QString message, level lvl) const
{
    // проверка значения приоритета сообщения
    if(lvl <= this->lvl)
        // отправка сообщения
        send(message);
}
