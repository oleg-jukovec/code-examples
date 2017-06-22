#ifndef SERVER_H
#define SERVER_H
/*
 * Файл server.h
 */
#include <QObject>
#include <QTcpServer>
#include <QTcpSocket>
#include <QDataStream>

#include "protocol.h"
#include "card.h"
#include "game.h"
/*
 * Класс Server представляет собой простой QTcpServer, который обрабатывает
 * подключения клиентов, их сообщения и ведёт игру.
 */
class Server : public QObject
{
    Q_OBJECT
public:
    // конструктор класса Server
    explicit Server(quint16 port, QVector<Card> cards, QObject *parent = 0);
    // деструктор класса Server
    ~Server();

signals:
    // сигнал посылается при окончании работы сервера
    void finished();

public slots:
    // слот используется для новых подключений
    void new_connection();
    // слот используется для чтения данных от клиентов
    void read_data();
    // слот используется для регистрации отключения клиентов
    void client_disconnected();

private:
    // константа задаёт максимальное количество клиентов
    static const int CLIENTS_MAX = 2;
    // клиенты для игры
    QTcpSocket    *clients[CLIENTS_MAX];
    // количество подключённых клиентов
    int           clients_count;
    // объект игры
    Game          game;
    // указатель на QTcpServer
    QTcpServer   *server;
    // порт сервера
    quint16       port;
    // количество байт, которые содержит следующий блок данных
    quint16       next_block_size;
    // метод начинает игру
    void          start_game();
    // метод получает индекс клиента из массива
    int           get_index(QTcpSocket *socket) const;
    // метод отправляет клиентам текущее состояние хода
    void          send_step() const;
    // метод отправляет клиенту простое сообщение
    void          send_simply(QTcpSocket *socket, PROTOCOL::MESSAGE message) const;
    // вспомогательный метод, который заносит вектор карт в поток QDataStream
    void          write_cards(QDataStream &out, QVector<Card> cards) const;
};

#endif // SERVER_H
