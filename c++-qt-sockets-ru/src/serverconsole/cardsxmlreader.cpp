/*
 * Файл cardsxmlreader.cpp
 */
#include <QFile>

#include "cardsxmlreader.h"
#include "consoleloger.h"
/*
 * Определение констант
 */
const QString CardsXMLReader::ROOT   = QString("cards");
const QString CardsXMLReader::CARD   = QString("card");
const QString CardsXMLReader::NAME   = QString("name");
const QString CardsXMLReader::HEALTH = QString("health");
const QString CardsXMLReader::MELEE  = QString("melee");
const QString CardsXMLReader::RANGE  = QString("range");
const QString CardsXMLReader::MAGIC  = QString("magic");

// метод открывает файл и считывает корень xml-файла
bool CardsXMLReader::read_cards()
{
    // очищаем список карт
    cards.clear();
    // инициализируем объект файла
    QFile file(file_name);
    // флаг хранит состояние операции
    bool result;
    // попытка открыть файл для чтения
    if(file.open(QIODevice::ReadOnly | QIODevice::Text))
    {
        // попытка считать XML данные из файла
        QDomDocument dom_doc;
        if(dom_doc.setContent(&file))
        {
            // получаем корневой элемент xml
            QDomElement top_element = dom_doc.documentElement();
            // если он не равен ROOT
            if(top_element.tagName() != ROOT)
            {
                // ошибка
                loger.send_message("Top element is not cards", Loger::DEBUG);
                file.close();
                return READ_ERROR;
            }
            // производим чтение списка карт
            result = read_data(top_element);
            // если ошибка
            if(!result)
                // сообщаем об ошибке
                loger.send_message("Can not parse root element", Loger::DEBUG);
        // если не удалось считать XML данные из файла
        } else {
            loger.send_message("Can not set content of file", Loger::DEBUG);
            result = READ_ERROR;
        }
        file.close();
    // если не удалось открыть файл
    } else {
        loger.send_message("Can not open file", Loger::DEBUG);
        return READ_ERROR;
    }
    // возвращаем результат чтения файла
    return result;
}
// метод обрабатывает список карт
bool CardsXMLReader::read_data(const QDomElement &root)
{
    // получаем первый дочерний элемент
    QDomNode child = root.firstChild();
    // пока существуют дочерние элементы
    while(!child.isNull())
    {
        // если дочерний тег дочернего элемента == CARD
        if(child.toElement().tagName() == CARD)
        {
            // считываем отдельную карту
            Card card;
            card.set_name(child.toElement().attribute(NAME));
            // если не удалось считать отдельную карту
            if(read_card(child.toElement(), card) == READ_ERROR)
            {
                // сообщаем об ошибке
                loger.send_message("Can not read card", Loger::DEBUG);
                return READ_ERROR;
            }
            cards.append(card);
        // если тег дочернего элемента != CARD
        } else {
            loger.send_message("Element is not card", Loger::DEBUG);
            return READ_ERROR;
        }
        // переходим к следующему дочернему элементу
        child = child.nextSibling();
    }
    // если список карт остался пуст
    if(!cards.size())
    {
        // сообщаем об ошибке
        loger.send_message("Cards is empty", Loger::DEBUG);
        return READ_ERROR;
    }
    // иначе данные прочитаны успешно
    return READ_SUCCESS;
}
// метод считывает отдельную карту
bool CardsXMLReader::read_card(const QDomElement &element, Card &card) const
{
    // получаем дочерний элемент
    QDomNode child = element.firstChild();
    // сообщаем о начале считывания карт
    loger.send_message("Started card reading", Loger::DEBUG);
    // пока дочерний элемент существует
    while(!child.isNull())
    {
        // считываем карту
        QString tag  = child.toElement().tagName();
        qint16 value = child.toElement().text().toInt();
        if(tag == HEALTH)
        {
            card.set_health(value);
        } else if(tag == MELEE)
        {
            card.set_melee(value);
        } else if(tag == RANGE)
        {
            card.set_range(value);
        } else if(tag == MAGIC)
        {
            card.set_magic(value);
        } else {
            // если тег задан неверно сообщаем об ошибке
            loger.send_message("Can not read card's values", Loger::DEBUG);
            return READ_ERROR;
        }
        // переходим к следующему элементу
        child = child.nextSibling();
    }
    // возвращаем успешный результат
    return READ_SUCCESS;
}
