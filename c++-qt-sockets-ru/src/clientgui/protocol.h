#ifndef PROTOCOL_H
#define PROTOCOL_H
/*
 * Файл protocol.h
 */
/*
 * Класс определяет простейший протокол обмена данных
 */
class PROTOCOL
{
public:
    // 1 байт
    typedef quint8 MESSAGE;
    /*
     * соединение
     */
    // ошибка последовательно ходов
    static const MESSAGE ERROR_SEQUANCE    = 0x00;
    // сервер полон
    static const MESSAGE FULL              = 0x01;
    // противник отключился от сервера
    static const MESSAGE ENEMY_DISCONNECT  = 0x02;
    /*
     * игра
     */
    // победа
    static const MESSAGE WIN               = 0x11;
    // проигрыш
    static const MESSAGE LOSE              = 0x12;
    /*
     * ход игры
     */
    // ход сделан
    static const MESSAGE DONE              = 0x20;
    // следующих ход
    static const MESSAGE NEXT_TURN         = 0x21;
    /*
     * обозначения клиентов
     */
    // сам клиента
    static const MESSAGE YOU               = 0x40;
    // противник клиента
    static const MESSAGE ENEMY             = 0x41;
    // никто из клиентов
    static const MESSAGE NOBODY            = 0x42;
    /*
     * тип хода
     */
    // следующий ход - ближний бой
    static const MESSAGE NEXT_MELEE        = 0x51;
    // следующий ход - дальний бой
    static const MESSAGE NEXT_RANGE        = 0x52;
    // следующий ход - магический урон
    static const MESSAGE NEXT_MAGIC        = 0x53;
};

#endif // PROTOCOL_H
