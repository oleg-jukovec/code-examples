#ifndef CARDSXMLREADER_H
#define CARDSXMLREADER_H
/*
 * Файл cardsxmlreader.h
 */
#include <QtXml>
#include <QVector>

#include "card.h"
/*
 * Класс CardsXMLReader предназначен для считывания объектов
 * класса Card из xml файла
 */
class CardsXMLReader
{
public:
    // константа определяет успешный результат чтения
    const static bool READ_SUCCESS = true;
    // константа определяет ошибку при чтении данных из файла
    const static bool READ_ERROR   = false;
    // конструирует объект класса
    CardsXMLReader(const QString file) : file_name(file)
    {
    }
    // производит чтение данных из файла с именем file_name
    // возвращает READ_SUCCESS при успешном чтении и
    // READ_ERROR при ошибках
    bool read_cards();
    // устанавливает новое имя файла для чтения
    void set_file_name(const QString file_name)
    {
        this->file_name = file_name;
        cards.clear();
    }
    // возвращает вектор прочитанных карт
    QVector<Card> get_cards() const
    {
        return cards;
    }
private:
    /*
     * Константы определены в cardsxmlreader.cpp
     */
    // константа определяет главный тег XML файла
    static const QString ROOT;
    // константа определяет тег отдельной карты
    static const QString CARD;
    // константа определяет параметр имени карты
    static const QString NAME;
    // константа определяет тег здоровья карты
    static const QString HEALTH;
    // константа определяет тег урона в ближнем бою
    static const QString MELEE;
    // константа определяет тег урона в дальнем бою
    static const QString RANGE;
    // константа определяет тег магического урона
    static const QString MAGIC;
    // имя текущего файла
    QString file_name;
    // вектор прочитанных карт
    QVector<Card> cards;
    // метод считывает данные
    bool read_data(const QDomElement &root);
    // метод считывает отдельную карту
    bool read_card(const QDomElement &element, Card &card) const;
};

#endif // CARDSXMLREADER_H
