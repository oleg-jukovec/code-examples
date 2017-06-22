/*
 * Файл card.cpp
 */
#include "card.h"
// метод переопеределяет вывод карты в поток QDataStream
QDataStream & operator <<(QDataStream &out, const Card &card)
{
    out << card.name << card.health << card.melee
        << card.range << card.magic;
    return out;
}
// метод опеределяет считывание карты из потока QDataStream
QDataStream & operator >>(QDataStream &in, Card &card)
{
    in >> card.name >> card.health >> card.melee
        >> card.range >> card.magic;
    return in;
}
