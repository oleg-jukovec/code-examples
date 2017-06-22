/*
 * Файл main.cpp
 */
#include <QCoreApplication>

#include "card.h"
#include "server.h"
#include "cardsxmlreader.h"
#include "consoleloger.h"

// путь к файлу настроек карт
// можно задать, к примеру, /etc/cards.xml
#define PATH_XML "cards.xml"
// минимальное количество карт, при которых возможен запуск
// сервера
const int          MIN_CARDS   = 10;
// константа задаёт порт сервера
const unsigned int SERVER_PORT = 2222;
// точка входа в программу
int main(int argc, char *argv[])
{
    // считываем карты из файла
    CardsXMLReader reader(PATH_XML);
    if(reader.read_cards() == CardsXMLReader::READ_ERROR){
        // при неудаче сообщаем об ошибке
        loger.send_message("Can not read XML file", Loger::ERROR);
        return 1;
    }
    // получаем список прочитанных карт
    QVector<Card> cards = reader.get_cards();
    // выводим список прочитанных карт
    loger.send_message("Readed cards:", Loger::DEBUG);
    for(int i = 0; i < cards.size(); i++){
        loger.send_message("Readed card:", Loger::DEBUG);
        loger.send_message(QString("Name: ") + cards[i].get_name(),
                           Loger::DEBUG);
        loger.send_message(QString("Health: ") + QString::number(cards[i].get_health()),
                           Loger::DEBUG);
        loger.send_message(QString("Melee damage: ") + QString::number(cards[i].get_melee()),
                           Loger::DEBUG);
        loger.send_message(QString("Range damage: ") + QString::number(cards[i].get_range()),
                           Loger::DEBUG);
        loger.send_message(QString("Magic damage: ") + QString::number(cards[i].get_magic()),
                           Loger::DEBUG);
    }
    // если карт прочитано меньше, чем необходимо
    if(cards.size() < MIN_CARDS)
    {
        // сообщаем об ошибке
        loger.send_message("Too few cards", Loger::ERROR);
        return 1;
    }
    // запускаем сервер
    loger.send_message("Start server", Loger::MESSAGE);
    QCoreApplication a(argc, argv);
    Server server(SERVER_PORT, cards, &a);
    QObject::connect(&server, SIGNAL(finished()), &a, SLOT(quit()));
    int result = a.exec();
    loger.send_message("Server stoped", Loger::MESSAGE);
    // завершаем работу программы
    return result;
}
