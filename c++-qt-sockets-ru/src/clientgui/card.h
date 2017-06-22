#ifndef CARD_H
#define CARD_H
/*
 * Файл card.h
 */
#include <QDataStream>
#include <QString>
/*
 * Класс Card определяет игровую карту и основные её параметры
 */
class Card
{
public:
    // конструирует объект по умолчанию
    Card() : name(""), health(0), melee(0), range(0), magic(0)
    {
    }
    // конструирует заданный объект
    Card(QString new_name, qint16 new_health, qint16 new_melee, qint16 new_range, qint16 new_magic) :
        name(new_name), health(new_health), melee(new_melee), range(new_range), magic(new_magic)
    {
    }
    // метод вычитает урон из уровня здоровья карты
    void damage(const quint16 damage)
    {
        health -= damage;
    }
    // метод проверяет, жива ли карта
    bool is_leave() const
    {
        return health > 0;
    }
    // метод устанавливает новое имя карты
    void set_name(QString name)
    {
        this->name = name;
    }
    // метод возвращает имя карты
    QString get_name() const
    {
        return name;
    }
    // метод устанавливает уровень здоровья карты
    void set_health(const qint16 health)
    {
        this->health = health;
    }
    // метод возвращает уровень здоровья карты
    qint16 get_health() const
    {
        return health;
    }
    // метод устанавливает урон в ближнем бою карты
    void set_melee(const qint16 melee)
    {
        this->melee = melee;
    }
    // метод возвращает урон в ближнем бою карты
    qint16 get_melee() const
    {
        return melee;
    }
    // метод устанавливает урон в дальнем бою карты
    void set_range(const qint16 range)
    {
        this->range = range;
    }
    // метод возвращает урон в дальнем бою карты
    qint16 get_range() const
    {
        return range;
    }
    // метод устанавливает магический урон карты
    void set_magic(const qint16 magic)
    {
        this->magic = magic;
    }
    // метод возвращает магический урон карты
    qint16 get_magic() const
    {
        return magic;
    }
    // переопределение оператора << для QDataStream
    friend QDataStream & operator <<(QDataStream &out, const Card &card);
    // переопределение оператора >> для QDataStream
    friend QDataStream & operator >>(QDataStream &in, Card &card);
private:
    // имя карты
    QString name;
    // уровень здоровья карты
    qint16  health;
    // урон в ближнем бою карты
    qint16  melee;
    // урон в дальнем бою карты
    qint16  range;
    // магический урон карты
    qint16  magic;
};
// переопределение оператора << для QDataStream
QDataStream & operator <<(QDataStream &out, const Card &card);
// переопределение оператора >> для QDataStream
QDataStream & operator >>(QDataStream &in, Card &card);

#endif // CARD_H
