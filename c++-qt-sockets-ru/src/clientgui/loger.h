#ifndef LOGER_H
#define LOGER_H
/*
 * Файл loger.h
 */
#include <QString>

/*
 * Абстрактный класс Loger предоставляет единый интерфейс для
 * логирования сообщений клиента и сервера.
 */
class Loger
{
public:
    /*
     * Перечисление определяет типы доступных сообщений
     */
    enum level{
        // критическая ошибка, наивысший приоритет
        ERROR   = 0,
        // не критическая ошибка
        WARNING = 1,
        // общее сообщение
        MESSAGE = 2,
        // используется при дебаге, наименьший приоритет
        DEBUG   = 3
    };
    // констроктор класса Loger
    Loger(level lvl);
    // виртуальный деструктор класса Loger
    virtual ~Loger(){}
    // метод проверяет приоритет сообщения и отправляет его с
    // помощью метода send
    void send_message(QString message, level lvl) const;
protected:
    // метод непосредственноо отвечает за отправку сообщения
    // должен быть переопределён в классе-наследнике
    virtual void send(QString) const = 0;
private:
    // поле задаёт текущий приоритет объекта
    level lvl;
};

#endif // LOGER_H
