/*
 * Файл clientgui.cpp
 */
#include <QMessageBox>

#include "clientgui.h"
#include "ui_clientgui.h"
#include "protocol.h"
/*
 * Конструирует объект ClientGui
 */
ClientGui::ClientGui(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::ClientGui),
    next_block_size(0),
    active_my(-1),
    active_enemy(-1)
{
    ui->setupUi(this);
    // запрещаем изменения размеров окна
    this->setFixedHeight(this->geometry().height());
    this->setFixedWidth(this->geometry().width());
    // инициализируем объект логирования
    loger = new TextBrowserLoger(findChild<QTextBrowser*>("log"), Loger::DEBUG);
    loger->send_message("GUI started", Loger::DEBUG);
    // связываем кнопку соединения с сервером со слотом
    connect_button = findChild<QPushButton*>("connectButton");
    connect(connect_button, SIGNAL(pressed()), this, SLOT(connect_button_pressed()));
    // получаем указатели на поля ввода хоста и порта сервера
    host_edit = findChild<QLineEdit*>("hostEdit");
    port_edit = findChild<QLineEdit*>("portEdit");
    // заполняем векторы кнопок карт и меток для карт клиента
    my_cards_buttons.append(findChild<QPushButton*>("myCard1"));
    my_cards_labels.append(findChild<QLabel*>("myHealth1"));
    my_cards_buttons.append(findChild<QPushButton*>("myCard2"));
    my_cards_labels.append(findChild<QLabel*>("myHealth2"));
    my_cards_buttons.append(findChild<QPushButton*>("myCard3"));
    my_cards_labels.append(findChild<QLabel*>("myHealth3"));
    my_cards_buttons.append(findChild<QPushButton*>("myCard4"));
    my_cards_labels.append(findChild<QLabel*>("myHealth4"));
    my_cards_buttons.append(findChild<QPushButton*>("myCard5"));
    my_cards_labels.append(findChild<QLabel*>("myHealth5"));
    // заполняем векторы кнопок карт и меток для карт противника
    enemy_cards_buttons.append(findChild<QPushButton*>("enemyCard1"));
    enemy_cards_labels.append(findChild<QLabel*>("enemyHealth1"));
    enemy_cards_buttons.append(findChild<QPushButton*>("enemyCard2"));
    enemy_cards_labels.append(findChild<QLabel*>("enemyHealth2"));
    enemy_cards_buttons.append(findChild<QPushButton*>("enemyCard3"));
    enemy_cards_labels.append(findChild<QLabel*>("enemyHealth3"));
    enemy_cards_buttons.append(findChild<QPushButton*>("enemyCard4"));
    enemy_cards_labels.append(findChild<QLabel*>("enemyHealth4"));
    enemy_cards_buttons.append(findChild<QPushButton*>("enemyCard5"));
    enemy_cards_labels.append(findChild<QLabel*>("enemyHealth5"));
    // связываем нажатие кнопок карт со слотами
    for(int i = 0; i < CARDS_NUMBER; i++)
    {
        // для кнопок карт клиента
        my_cards_buttons[i]->setCheckable(true);
        connect(my_cards_buttons[i], SIGNAL(pressed()), this, SLOT(my_pressed()));
        // для кнопок карт противника
        enemy_cards_buttons[i]->setCheckable(true);
        connect(enemy_cards_buttons[i], SIGNAL(pressed()), this, SLOT(enemy_pressed()));
    }
    // заполняем вектора метод характеристика выбранной карты клиента
    my_damage_labels.append(findChild<QLabel*>("myDamageMelee"));
    my_damage_labels.append(findChild<QLabel*>("myDamageRange"));
    my_damage_labels.append(findChild<QLabel*>("myDamageMagic"));
    // заполняем вектора метод характеристика выбранной карты противника
    enemy_damage_labels.append(findChild<QLabel*>("enemyDamageMelee"));
    enemy_damage_labels.append(findChild<QLabel*>("enemyDamageRange"));
    enemy_damage_labels.append(findChild<QLabel*>("enemyDamageMagic"));
    // получаем указатель на метку игры
    game_label = findChild<QLabel*>("gameLabel");
    // получаем указатель на метку шага
    step_label = findChild<QLabel*>("stepLabel");
    // получаем указатель на кнопку шага
    step_button = findChild<QPushButton*>("stepButton");
    // связываем нажатие кнопки шага с соответсвующим методом
    connect(step_button, SIGNAL(pressed()), this, SLOT(step_button_pressed()));
    // отключаем GUI игры
    disable_game_gui();
}
// очищаем память
ClientGui::~ClientGui()
{
    // очищаем память за элементами GUI
    delete ui;
    // удаляем из памяти объект для логирования
    delete loger;
}
// слот обрабатывает нажатие кнопок карт клиента
void ClientGui::my_pressed()
{
    // получаем индекс карты, соответсвующей кнопке
    int i = get_index(my_cards_buttons, (QPushButton*)sender());
    loger->send_message("My button clicked: " + QString::number(i), Loger::DEBUG);
    // если i принимает недопустимое значение, то завершаем работу метода
    if(i == -1 || i >= CARDS_NUMBER)
        return;
    // если какая-то карта была ранее активирована
    if(active_my != -1)
    {
        // деактивируем кнопку карты
        my_cards_buttons[active_my]->setDown(false);
        my_cards_buttons[active_my]->setChecked(false);
    }
    // активируем новую кнопку карты
    active_my = i;
    my_cards_buttons[active_my]->setDown(true);
    my_cards_buttons[active_my]->setChecked(false);
    // перерисовываем показатели карты
    rewrite_damage_labels();
}
// слот обрабатывает нажатие кнопок карт противника
void ClientGui::enemy_pressed()
{
    // получаем индекс карты, соответсвующей кнопке
    int i = get_index(enemy_cards_buttons, (QPushButton*)sender());
    // если i принимает недопустимое значение, то завершаем работу метода
    loger->send_message("Enemy button clicked: " + QString::number(i), Loger::DEBUG);
    if(i == -1 || i >= CARDS_NUMBER)
        return;
    // если какая-то карта была ранее активирована
    if(active_enemy != -1)
    {
        // деактивируем кнопку карты
        enemy_cards_buttons[active_enemy]->setDown(false);
        enemy_cards_buttons[active_enemy]->setChecked(false);
    }
    // активируем новую кнопку карты
    active_enemy = i;
    enemy_cards_buttons[active_enemy]->setDown(true);
    enemy_cards_buttons[active_enemy]->setChecked(false);
    // перерисовываем показатели карты
    rewrite_damage_labels();
}
// метод возращает индекс кнопки вектора, иначе -1
int ClientGui::get_index(QVector<QPushButton*> &vector, QPushButton *button)
{
    for(int i = 0; i < vector.size(); i++)
        if(vector[i] == button)
            return i;
    return -1;
}
// метод перерисовывает кнопки и метки для всех карт
void ClientGui::rewrite_cards()
{
    // если векторы карт неверного размера
    if(my_cards.size() != CARDS_NUMBER || enemy_cards.size() != CARDS_NUMBER)
    {
        // сообщаем об ошибке
        loger->send_message("Can not rewrite cards", Loger::WARNING);
        return;
    }
    // обходим все карты
    for(int i = 0; i < CARDS_NUMBER; i++)
    {
        // устанавливаем имя карт клиента
        my_cards_buttons[i]->setText(my_cards[i].get_name());
        // устанавливаем здоровье карт клиента
        if(my_cards[i].is_leave())
            my_cards_labels[i]->setText(QString::number(my_cards[i].get_health()));
        // если карта мертва
        else {
            // выбрана мёртвая карта
            if(i == active_my)
                // обнуляем выбор
                active_my = -1;
            // устанавливаем соответствующее сообщение
            my_cards_labels[i]->setText(QString("мёртв"));
            // отключаем кнопку карты
            my_cards_buttons[i]->setEnabled(false);
        }
        // деактивируем кнопки карт клиента
        my_cards_buttons[i]->setDown(false);
        my_cards_buttons[i]->setChecked(false);
        // устанавливаем имя карт противника
        enemy_cards_buttons[i]->setText(enemy_cards[i].get_name());
        // устанавливаем здоровье карт противника, если карта мертва
        if(enemy_cards[i].is_leave())
            enemy_cards_labels[i]->setText(QString::number(enemy_cards[i].get_health()));
        // если карта мертва
        else {
            // выбрана мёртвая карта
            if(i == active_enemy)
                // обнуляем выбор
                active_enemy = -1;
            // устанавливаем соответствующее сообщение
            enemy_cards_labels[i]->setText(QString("мёртв"));
            // отключаем кнопку карты
            enemy_cards_buttons[i]->setEnabled(false);
        }
        // деактивируем кнопки карт противника
        enemy_cards_buttons[i]->setDown(false);
        enemy_cards_buttons[i]->setChecked(false);
    }
    // устаналиваем нажатой кнопку выбранной карта клиента
    if(active_my != -1)
    {
        my_cards_buttons[active_my]->setDown(true);
        my_cards_buttons[active_my]->setChecked(false);
    }
    // устаналиваем нажатой кнопку выбранной карта противника
    if(active_enemy != -1)
    {
        enemy_cards_buttons[active_enemy]->setDown(true);
        enemy_cards_buttons[active_enemy]->setChecked(false);
    }
    // перерисовываем характеристики карт
    rewrite_damage_labels();
}
// перерисовываем характеристики карт
void ClientGui::rewrite_damage_labels()
{
    loger->send_message("Rewrite my label: " + QString::number(active_my), Loger::DEBUG);
    // если карта клиента не выбрана
    if(active_my == -1)
        rewrite_damage_label(0, my_damage_labels);
    // если карта клиента выбрана
    else{
        Card tmp = my_cards[active_my];
        rewrite_damage_label(&tmp, my_damage_labels);
    }
    // если карта клиента не выбрана
    if(active_enemy == -1)
        rewrite_damage_label(0, enemy_damage_labels);
    // если карта клиента выбрана
    else{
        Card tmp = enemy_cards[active_enemy];
        rewrite_damage_label(&tmp, enemy_damage_labels);
    }
}
// перерисовываем характеристики карты
void ClientGui::rewrite_damage_label(Card *card, QVector<QLabel*> labels)
{
    QString melee;
    QString range;
    QString magic;
    // значение по умолчанию
    const QString no_card("нет");
    // если карты нет, то задаём значение по умолчанию
    if(card == 0)
    {
        melee = no_card;
        range = no_card;
        magic = no_card;
    // иначе получаем значения характеристик карты
    } else {
        melee = QString::number(card->get_melee());
        range = QString::number(card->get_range());
        magic = QString::number(card->get_magic());
    }
    // перерисовываем содержимое меток характеристик
    labels[0]->setText(melee);
    labels[1]->setText(range);
    labels[2]->setText(magic);
}
// отключаем GUI игры
void ClientGui::disable_game_gui()
{
    QGroupBox *game = findChild<QGroupBox*>("game");
    game->setEnabled(false);
    QGroupBox *my= findChild<QGroupBox*>("my_cards");
    my->setEnabled(false);
    QGroupBox *enemy = findChild<QGroupBox*>("enemy_cards");
    enemy->setEnabled(false);
    QGroupBox *stats = findChild<QGroupBox*>("stats");
    stats->setEnabled(false);
}
// влючаем GUI игры
void ClientGui::enable_game_gui()
{
    QGroupBox *game = findChild<QGroupBox*>("game");
    game->setEnabled(true);
    QGroupBox *my= findChild<QGroupBox*>("my_cards");
    my->setEnabled(true);
    QGroupBox *enemy = findChild<QGroupBox*>("enemy_cards");
    enemy->setEnabled(true);
    QGroupBox *stats = findChild<QGroupBox*>("stats");
    stats->setEnabled(true);
}
// отключаем GUI подключения
void ClientGui::disable_connect_gui()
{
    QGroupBox *server = findChild<QGroupBox*>("server");
    server->setEnabled(false);
}
// включаем GUI подключения
void ClientGui::enable_connect_gui()
{
    QGroupBox *server = findChild<QGroupBox*>("server");
    server->setEnabled(true);
}
// отключаем GUI шага
void ClientGui::disable_step_gui()
{
    QGroupBox *game = findChild<QGroupBox*>("game");
    game->setEnabled(false);
}
// включаем GUI шага
void ClientGui::enable_step_gui()
{
    QGroupBox *game = findChild<QGroupBox*>("game");
    game->setEnabled(true);
}
// слот обрабатывает нажатие кнопки подключения
void ClientGui::connect_button_pressed()
{
    // получаем адрес хоста и порт сервера
    QString host = host_edit->text();
    int port = port_edit->text().toInt();
    // инициализируем новое подключение
    socket = new QTcpSocket(this);
    socket->connectToHost(host, port);
    // связываем сигналы сокета с соответствующими методами
    connect(socket, SIGNAL(connected()), this, SLOT(connected()));
    connect(socket, SIGNAL(disconnected()), this, SLOT(socket_disconnected()));
    connect(socket, SIGNAL(error(QAbstractSocket::SocketError)), this, SLOT(slot_error(QAbstractSocket::SocketError)));
    connect(socket, SIGNAL(readyRead()), this, SLOT(read_data()));
}
// слот обрабатывает нажатие кнопки шага
void ClientGui::step_button_pressed()
{
    // если не выбрана карта клиента или противника
    if(active_my == -1 || active_enemy == -1){
        // сообщаем об ошибке
        loger->send_message("Не все карты выбраны", Loger::MESSAGE);
        return;
    }
    loger->send_message("Step button has been pressed by user", Loger::DEBUG);
    // иначе делаем шаг игры (отправляем данные на сервер)
    do_step();
}
// слот обрабатывает входящие данные сокета
void ClientGui::read_data()
{
    loger->send_message("Message recived", Loger::DEBUG);
    // инициализируем поток чтения данных из слота
    QDataStream in(socket);
    //in.setVersion(QDataStream::Qt_4_5);
    for(;;)
    {
        // если размер следующего блока данных не определён
        if(!next_block_size)
        {
            if(socket->bytesAvailable() < sizeof(quint16))
                break;
            // считываем размер блока данных
            in >> next_block_size;
        }
        loger->send_message("Bytes need recivied: " + QString::number(next_block_size), Loger::DEBUG);
        loger->send_message("Bytes available: " + QString::number(socket->bytesAvailable()), Loger::DEBUG);
        // если данных в сокете доступное меньше размера блока данных
        if(socket->bytesAvailable() < next_block_size)
        {
            loger->send_message("Block is not ready", Loger::DEBUG);
            // прерываем выполнение
            break;
        }
        // инициализируем переменные
        PROTOCOL::MESSAGE type, damage_type, turn;
        // инициализируем окно диалога
        QMessageBox message_box(this);
        // переменная для чтения текущей карты
        Card card;
        // считываем тип сообщения от сервера
        in >> type;
        // в зависимости от типа сообщения
        switch(type){
        // считываем данные следующего хода
        case PROTOCOL::NEXT_TURN:
            // считывание типа урона и очередь хода
            in >> damage_type >> turn;
            // установка типа урона
            if(damage_type == PROTOCOL::NEXT_MELEE)
                step_label->setText("Тип хода: Ближний бой");
            else if (damage_type == PROTOCOL::NEXT_RANGE)
                step_label->setText("Тип хода: Дальний бой");
            else if (damage_type == PROTOCOL::NEXT_MAGIC)
                step_label->setText("Тип хода: Магический урон");
            // установка очереди хода
            if(turn == PROTOCOL::YOU)
                game_label->setText("Ваш ход");
            else if(turn == PROTOCOL::ENEMY)
                game_label->setText("Ход противника");
            else if(turn == PROTOCOL::NOBODY)
                game_label->setText("Некому ходить");
            // очистка векторов карт
            my_cards.clear();
            enemy_cards.clear();
            // считывание карт
            for(int i = 0; i < CARDS_NUMBER * 2; i++)
            {
                in >> card;
                loger->send_message(QString("Readed card:" + QString::number(i)), Loger::DEBUG);
                loger->send_message(QString("Name: ") + card.get_name(), Loger::DEBUG);
                loger->send_message(QString("Health: ") + QString::number(card.get_health()), Loger::DEBUG);
                loger->send_message(QString("Melee damage: ") + QString::number(card.get_melee()), Loger::DEBUG);
                loger->send_message(QString("Range damage: ") + QString::number(card.get_range()), Loger::DEBUG);
                loger->send_message(QString("Magic damage: ") + QString::number(card.get_magic()), Loger::DEBUG);
                // первые 5 карт - карты клиента, остальные - карты противника
                if(i < CARDS_NUMBER)
                    my_cards.append(card);
                else
                    enemy_cards.append(card);
            }
            // перерисовка GUI
            rewrite_cards();
            enable_game_gui();
            // если ход клиента
            if(turn == PROTOCOL::YOU)
                // активируем кнопку хода
                step_button->setEnabled(true);
            else
                // иначе деактивируем кнопку хода
                step_button->setEnabled(false);
            break;
        // победа
        case PROTOCOL::WIN:
            loger->send_message("Вы победили!", Loger::MESSAGE);
            // выводим диалог о победе
            message_box.setText("Вы победили!");
            message_box.setWindowTitle("Победа!");
            message_box.exec();
            break;
        // проигрыш
        case PROTOCOL::LOSE:
            loger->send_message("Вы проиграли!", Loger::MESSAGE);
            // выводим диалог о проигрыше
            message_box.setText("Все ваши карты мертвы");
            message_box.setWindowTitle("Вы проиграли!");
            message_box.exec();
            break;
        // ничья
        case PROTOCOL::NOBODY:
            loger->send_message("Ничья", Loger::MESSAGE);
            // выводим диалог о ничье
            message_box.setText("Все ваши карты и карты противника мертвы");
            message_box.setWindowTitle("Ничья!");
            message_box.exec();
            break;
        // сервер полон
        case PROTOCOL::FULL:
            loger->send_message("Невозможно подключиться, сервер полон", Loger::MESSAGE);
            // выводим диалог о невозможности подключения
            message_box.setText("Невозможно подключиться");
            message_box.setWindowTitle("Сервер полон");
            message_box.exec();
            // отключаемся от сервера
            socket->disconnectFromHost();
            break;
        // противник отключился
        case PROTOCOL::ENEMY_DISCONNECT:
            loger->send_message("Противник завершил соединение с сервером", Loger::MESSAGE);
            // выводим диалог об отключении противника
            message_box.setText("Противник отключился от сервера");
            message_box.setWindowTitle("Противник отключился");
            message_box.exec();
            // отключаемся от сервера
            socket->disconnectFromHost();
            break;
        // ошибка последовательности ходов
        case PROTOCOL::ERROR_SEQUANCE:
            loger->send_message("Ошибка последовательности игры", Loger::WARNING);
            break;
        // неподдерживаемый тип сообщений
        default:
            loger->send_message("Неподдерживаемый тип сообщения от сервера", Loger::ERROR);
            break;
        }
    }
    next_block_size = 0;
}
// обработка ошибки сокета
void ClientGui::slot_error(QAbstractSocket::SocketError err)
{
    QString strError =
    "Ошибка: " + (err == QAbstractSocket::HostNotFoundError ?
    "Адрес или порт сервера заданы неверно" :
    err == QAbstractSocket::RemoteHostClosedError ?
    "Сервер завершил соединение" :
    err == QAbstractSocket::ConnectionRefusedError ?
    "Соединение было сброшено" :
    QString(socket->errorString())
    );
    loger->send_message(strError, Loger::ERROR);
}
// метод отправляет серверу данные о сделанном ходе
void ClientGui::do_step()
{
    // создаётся пакет данных
    QByteArray packet;
    // инициализируем поток вывода данных в пакет
    QDataStream out(&packet, QIODevice::WriteOnly);
    // формировани пакета
    out << quint16(0) << PROTOCOL::DONE << qint16(active_my) << qint16(active_enemy);
    out.device()->seek(0);
    out << quint16(packet.size() - sizeof(quint16));
    loger->send_message("Step message sended", Loger::DEBUG);
    // отправка пакета
    socket->write(packet);
}
// слот обрабатывает подключение к сервера
void ClientGui::connected()
{
    // сбрасываем номера выбранных карт
    active_my = -1;
    active_enemy = -1;
    // отключаем GUI соединения
    disable_connect_gui();
    // включаем GUI шага
    enable_step_gui();
    loger->send_message("Соединение с сервером установлено", Loger::MESSAGE);
    loger->send_message("Ожидание подключение второго игрока", Loger::MESSAGE);
    step_label->setText("Соединение установлено");
    game_label->setText("Ожидание подключение второго игрока");
    // отключаем кнопку шага
    step_button->setEnabled(false);
}
// слот обрабатывает отключение от сервера
void ClientGui::socket_disconnected()
{
    loger->send_message("Отключение от сервера", Loger::MESSAGE);
    // закрываем сокет
    socket->close();
    // инициализируем удаление сокета из памяти
    socket->deleteLater();
    // активируем все кнопки
    for(int i = 0; i < CARDS_NUMBER; i++)
    {
        my_cards_buttons[i]->setEnabled(true);
        enemy_cards_buttons[i]->setEnabled(true);
    }
    // отключаем GUI игры
    disable_game_gui();
    // включаем GUI подключения
    enable_connect_gui();
}
