/*
 *  Файл server.cpp
 */
#include <QByteArray>
#include <QDataStream>
#include <QTcpSocket>
#include <QDataStream>
#include <QIODevice>

#include "server.h"
#include "consoleloger.h"

// конструктор создаёт новый объект
Server::Server(quint16 new_port, QVector<Card> new_cards, QObject *parent)
    : QObject(parent), game(new_cards), port(new_port), next_block_size(0)
{
    // обнуляются данные о клиентах
    clients_count = 0;
    for(int i = 0; i < CLIENTS_MAX; i++)
        clients[i] = 0;
    // создаётся новый QTcpServer
    server = new QTcpServer(this);
    // сервер начинает свою работу
    if(!server->listen(QHostAddress::Any, port))
    {
        // если не удалось запустить сервер, то сообщаем об ошибке
        loger.send_message("Can not start server", Loger::DEBUG);
        server->close();
        return;
    }
    // подключаем сигнал о новом подключении QTcpServer с соответсвующим слотом
    connect(server, SIGNAL(newConnection()), this, SLOT(new_connection()));
}
// деструктор очищает память
Server::~Server()
{
    server->deleteLater();
}
// метод обрабатывает новое подключение клиента
void Server::new_connection()
{
    // получаем указатель на клиент
    QTcpSocket *client = server->nextPendingConnection();
    // если уже достигнут придел кол-ва подключённых клиентов
    if(clients_count >= CLIENTS_MAX)
    {
        // сообщаем об этом клиенту
        send_simply(client, PROTOCOL::FULL);
        // закрываем соединение с клиентом
        client->disconnectFromHost();
        client->deleteLater();
        return;
    }
    // заполняем массив клиентов
    clients[clients_count] = client;
    clients_count++;
    // соединяем сигналы сокета и слоты объекта класса
    connect(client, SIGNAL(disconnected()), this, SLOT(client_disconnected()));
    connect(client, SIGNAL(disconnected()), client, SLOT(deleteLater()));
    connect(client, SIGNAL(readyRead()), this, SLOT(read_data()));
    // если кол-во клиентов максимальное - запускаем игру
    if(clients_count == CLIENTS_MAX)
        start_game();
}
// метод обрабатывает отключение клиентов
void Server::client_disconnected()
{
    loger.send_message("Disconnect query", Loger::DEBUG);
    // получаем индекс отключённого клиента
    int i = get_index((QTcpSocket*)sender());
    // если индекс не верен
    if(i >= CLIENTS_MAX || i < 0)
    {
        // сообщаем о том, что клиент не зарегестрирован ранее
        loger.send_message("Socket is not client", Loger::WARNING);
        return;
    }
    // сообщаем об отключении клиента
    loger.send_message("Disconnected client : " + QString::number(i), Loger::MESSAGE);
    // если индекс клиента = 0
    if(i == 0)
    {
        // удаляем клиент
        clients[0] = clients[1];
        clients[1] = 0;
        clients_count > 0 ? clients_count-- : clients_count;
    // если индекс клиента = 1
    } else {
        // удаляем клиент
        clients[1] = 0;
        clients_count > 1 ? clients_count-- : clients_count;
    }
    // если остался активный клиент
    if(clients_count >= 1)
    {
        // сообщаем ему об отключении его противника
        loger.send_message("Send disconnect to another client", Loger::DEBUG);
        send_simply(clients[0], PROTOCOL::ENEMY_DISCONNECT);
    }
}
// метод возвращает индекс клиента из массива зарегестрированных
int Server::get_index(QTcpSocket *socket) const
{
    for(int i = 0; i < CLIENTS_MAX; i++)
        if(clients[i] == socket)
            return i;
    return CLIENTS_MAX;
}
// метод обрабатывает сообщения от клиента
void Server::read_data()
{
    // получаем индекс клиента
    int i = get_index((QTcpSocket*)sender());
    loger.send_message("Message recived from client: " + QString::number(i), Loger::DEBUG);
    // если индекс не допустим
    if(i >= CLIENTS_MAX || i < 0)
    {
        // сообщаем об ошибке
        loger.send_message("Message from unknown client", Loger::WARNING);
        return;
    }
    // инициализируем поток чтения данных от клиента
    QDataStream in(clients[i]);
    //in.setVersion(QDataStream::Qt_4_5);
    for(;;)
    {
        // если размер следующего блока данных не определён
        if(!next_block_size)
        {
            // считываем размер блока данных
            if(clients[i]->bytesAvailable() < sizeof(quint16))
                break;
            in >> next_block_size;
        }
        loger.send_message("Bytes need recivied: " + QString::number(next_block_size), Loger::DEBUG);
        loger.send_message("Bytes available: " + QString::number(clients[i]->bytesAvailable()), Loger::DEBUG);
        // если байтов для чтения доступно меньше, чем нужно
        if(clients[i]->bytesAvailable() < next_block_size)
        {
            // выходим из цикла
            loger.send_message("Block is not ready", Loger::DEBUG);
            break;
        }
        // иначе начинаем чтение данных от клиента
        // тип сообщения
        PROTOCOL::MESSAGE type;
        // номера карт
        qint16 from, to;
        // считываем тип сообщений
        in >> type;
        // в зависимости от типа
        switch(type)
        {
        // клиент сделал ход
        case PROTOCOL::DONE:
            // считываем номера карт
            in >> from >> to;
            loger.send_message("Atack from: " + QString::number(from), Loger::DEBUG);
            loger.send_message("Atack to: " + QString::number(to), Loger::DEBUG);
            // если не ход игрока
            if(Game::players(i) != game.get_cur_player() || game.is_end())
            {
                // отправляем клиенту сообщение об ошибке последовательности
                send_simply(clients[i], PROTOCOL::ERROR_SEQUANCE);
                send_step();
                break;
            }
            loger.send_message("Do step", Loger::DEBUG);
            // делаем шаг игры
            game.do_step(from, to);
            // если игра окончилась
            if(game.is_end())
            {
                // и нет победителей
                if(game.get_winner() == Game::NOBODY){
                    // отсылаем клиенту сообщения об этом
                    send_simply(clients[0], PROTOCOL::NOBODY);
                    send_simply(clients[0], PROTOCOL::NOBODY);
                // иначе
                } else {
                    // отсылаем клиентам сообщение о том, кто победил
                    QTcpSocket *winner = 0;
                    QTcpSocket *looser = 0;
                    if(game.get_winner() == Game::FIRST)
                    {
                        winner = clients[0];
                        looser = clients[1];
                    } else if(game.get_winner() == Game::SECOND)
                    {
                        winner = clients[1];
                        looser = clients[0];
                    }
                    send_simply(winner, PROTOCOL::WIN);
                    send_simply(looser, PROTOCOL::LOSE);
                }
            }
            // отсылаем клиентам сообщения о результатах хода
            send_step();
            break;
        // иначе - незарегестрированный тип сообщения, ошибка
        default:
            loger.send_message("Unsupported message type", Loger::ERROR);
            break;
        }
    }
    // обнуляем блок для считывания данных
    next_block_size = 0;
}
// метод отсылает клиентам результаты хода
void Server::send_step() const
{
    // игрок, чей сейчас ход
    Game::players cur    = game.get_cur_player();
    // карты первого игрока
    QVector<Card> first  = game.get_cards(Game::FIRST);
    // карты второго игрока
    QVector<Card> second = game.get_cards(Game::SECOND);
    // формируем пакет данных для первого игрока
    QByteArray first_packet;
    QDataStream out_first(&first_packet, QIODevice::WriteOnly);
    // согласно протоколу передачи данных тип сообщения - следующий ход
    out_first << quint16(0);
    out_first << PROTOCOL::NEXT_TURN;
    // согласно протокола - тип следующего хода
    if(game.get_damage_type() == Game::MELEE)
        out_first << PROTOCOL::NEXT_MELEE;
    else if(game.get_damage_type() == Game::RANGE)
        out_first << PROTOCOL::NEXT_RANGE;
    else if(game.get_damage_type() == Game::MAGIC)
        out_first << PROTOCOL::NEXT_MELEE;
    // согласно протокола - чей следующий ход
    if(cur == Game::FIRST)
        out_first << PROTOCOL::YOU;
    else if(cur == Game::NOBODY)
        out_first << PROTOCOL::NOBODY;
    else
        out_first << PROTOCOL::ENEMY;
    // записываем карты в поток
    write_cards(out_first, first);
    write_cards(out_first, second);
    // заканчиваем формирование пакета данных
    out_first.device()->seek(0);
    out_first << quint16(first_packet.size() - sizeof(quint16));
    // формируем пакет данных для второго игрока
    QByteArray second_packet;
    QDataStream out_second(&second_packet, QIODevice::WriteOnly);
    // согласно протоколу передачи данных тип сообщения - следующий ход
    out_second << quint16(0);
    out_second << PROTOCOL::NEXT_TURN;
    // согласно протокола - тип следующего хода
    if(game.get_damage_type() == Game::MELEE)
        out_second << PROTOCOL::NEXT_MELEE;
    else if(game.get_damage_type() == Game::RANGE)
        out_second << PROTOCOL::NEXT_RANGE;
    else if(game.get_damage_type() == Game::MAGIC)
        out_second << PROTOCOL::NEXT_MELEE;
    // согласно протокола - чей следующий ход
    if(cur == Game::SECOND)
        out_second << PROTOCOL::YOU;
    else if(cur == Game::NOBODY)
        out_second << PROTOCOL::NOBODY;
    else
        out_second << PROTOCOL::ENEMY;
    // записываем карты в поток
    write_cards(out_second, second);
    write_cards(out_second, first);
    // заканчиваем формирование пакета данных
    out_second.device()->seek(0);
    out_second << quint16(second_packet.size() - sizeof(quint16));
    // если хотя бы один из клиентов отключён
    if(!clients[0] || !clients[1])
    {
        // сообщаем об ошибке
        loger.send_message("One or more clients has been disconnected", Loger::ERROR);
        loger.send_message("Can not send cards list", Loger::ERROR);
        return;
    }
    // передаём сформированные пакеты
    clients[0]->write(first_packet);
    clients[1]->write(second_packet);
}
// метод отсылает простое сообщение согласно протоколу клиенту
void Server::send_simply(QTcpSocket *socket, PROTOCOL::MESSAGE message) const
{
    // формируется пакет
    QByteArray packet;
    QDataStream out(&packet, QIODevice::WriteOnly);
    out << quint16(sizeof(quint8)) << message;
    // отсылка сообщения
    socket->write(packet);
}
// метод запускает игру
void Server::start_game()
{
    // инициализируется новая игра
    game.new_game();
    // отсылается результаты клиентам
    send_step();
}
// вспомогательный метод записывает карты из вектора в поток
void Server::write_cards(QDataStream &out, QVector<Card> cards) const
{
    for(int i = 0; i < cards.size(); i++)
        out << cards[i];
}
