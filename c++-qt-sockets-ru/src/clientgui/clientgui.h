#ifndef CLIENTGUI_H
#define CLIENTGUI_H
/*
 * Файл clientgui.h
 */
#include <QMainWindow>
#include <QtGui>
#include <QPushButton>
#include <QLineEdit>
#include <QLabel>
#include <QTcpSocket>

#include "textbrowserloger.h"
#include "card.h"

namespace Ui {
    class ClientGui;
}
/*
 * Класс ClientGui реализует Tcp клиент и графический интерфейс пользователя
 * для игры.
 */
class ClientGui : public QMainWindow
{
    Q_OBJECT

public:
    // конструирует объект
    explicit ClientGui(QWidget *parent = 0);
    // очищает память
    ~ClientGui();

public slots:
    // слот используется для чтения данных из сокета
    void read_data();
    // слот используется для вывода ошибки сокета
    void slot_error(QAbstractSocket::SocketError);
    // сокет используется для регистрации подключения к серверу
    void connected();
    // сокет используется для регистрации отключения от сервера
    void socket_disconnected();
    // сокет используется для регистрации нажатия кнопки подключения
    void connect_button_pressed();
    // сокет используется для регистрации нажатия кнопки хода
    void step_button_pressed();
    // сокет используется для регистрации нажатия кнопок карт пользователя
    void my_pressed();
    // сокет используется для регистрации нажатия кнопок карт противника
    void enemy_pressed();

private:
    // константа задаёт кол-во карт в наборе
    static const int CARDS_NUMBER = 5;
    // метод делает неактивным GUI игры
    void disable_game_gui();
    // метод делает активным GUI игры
    void enable_game_gui();
    // метод делает неактивным GUI подключения
    void disable_connect_gui();
    // метод делает активным GUI подключения
    void enable_connect_gui();
    // метод делает неактивным GUI хода игры
    void disable_step_gui();
    // метод делает активным GUI хода игры
    void enable_step_gui();
    // делает шаг игры
    void do_step();
    // перерисовывает GUI карт
    void rewrite_cards();
    // перерисовывает GUI характеристик карт
    void rewrite_damage_labels();
    // перерисовывает GUI характеристик для конкретной карты и меток
    void rewrite_damage_label(Card *card, QVector<QLabel*> labels);
    // возвращает индекс кнопки button в массиве vector
    int get_index(QVector<QPushButton*> &vector, QPushButton *button);
    // указатель на UI
    Ui::ClientGui *ui;
    // указатель на объект, используемый для логирования
    TextBrowserLoger *loger;
    // указатель на кнопку подключения
    QPushButton *connect_button;
    // указатель на кнопку шага
    QPushButton *step_button;
    // указатель на метку игры
    QLabel      *game_label;
    // указатель на метку шага
    QLabel      *step_label;
    // указатель на поле редактирования хоста сервера
    QLineEdit *host_edit;
    // указатель на поле редактирования порта сервера
    QLineEdit *port_edit;
    // массив кнопок для карт клиента
    QVector<QPushButton*> my_cards_buttons;
    // массив кнопок для карт противника
    QVector<QPushButton*> enemy_cards_buttons;
    // массив меток здоровья карт клиента
    QVector<QLabel*> my_cards_labels;
    // массив меток здоровья карт противника
    QVector<QLabel*> enemy_cards_labels;
    // массив меток характеристик карты клиента
    QVector<QLabel*> my_damage_labels;
    // массив меток характеристик карты противника
    QVector<QLabel*> enemy_damage_labels;
    // вектор карт клиентов
    QVector<Card> my_cards;
    // вектор карт противника
    QVector<Card> enemy_cards;
    // сокет для подключения
    QTcpSocket    *socket;
    // размер следующего блока данных
    quint16       next_block_size;
    // номер выбранной кнопки клиента
    int active_my;
    // номер выбранной кнопки противника
    int active_enemy;
};

#endif // CLIENTGUI_H
