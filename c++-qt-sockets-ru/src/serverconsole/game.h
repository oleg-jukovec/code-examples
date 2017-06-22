#ifndef GAME_H
#define GAME_H
/*
 * Файл game.h
 */
#include <QVector>

#include "card.h"
/*
 * Класс Game организует игру на стороне сервера
 * для двух игроков
 */
class Game
{
public:
    // перечисление задаёт возможных игроков
    enum players
    {
        // первый игрок
        FIRST = 0,
        // второй игрок
        SECOND = 1,
        // никто из игроков
        NOBODY = 2
    };
    // перечисление задаёт возможные типы урона
    enum damage
    {
        // ближний бой
        MELEE = 0,
        // дальний бой
        RANGE = 1,
        // магический
        MAGIC = 2
    };
    // конструирует объект, вектор cards должен содержать > 10 элементов
    Game(QVector<Card> cards);
    // метод инициализирует новую игру
    void          new_game();
    // метод возвращает список карт заданного игрока
    QVector<Card> get_cards(Game::players player) const;
    // метод возвращает 1, если игра закончена и 0, если ещё нет
    bool          is_end() const;
    // метод делает шаг игры, from_card - номер карты текущего игрока
    // to_card - номер карты противника
    void          do_step(quint16 from_card, quint16 to_card);
    // метод возвращает победителя игры, в противном случае NOBODY
    Game::players get_winner() const;
    // метод возвращает текущего игрока, который делает ход
    Game::players get_cur_player() const;
    // метод возвращает тип урона текущего хода
    Game::damage  get_damage_type() const;
private:
    // метод наносит урон от игрока player его противнику
    void set_damaged(Game::players player, Game::damage type,
                            quint16 player_card, quint16 enemy_card);
    // метод случайным образом генерирует урон хода
    void generate_damage();
    // метод переводит ход к следующему игроку
    void next_player();
    // метод задаёт максимальное количество карт игрока
    static const quint16 CARDS_NUMBER = 5;
    // вектор содержит все возможные карты
    QVector<Card> all_cards;
    // поле хранит значение текущего игрока, чей ход
    Game::players cur_step;
    // поле хранит тип урона хода
    Game::damage  cur_damage;
    // вектор хранит карты первого игрока
    QVector<Card> first;
    // вектор хранит карты второго игрока
    QVector<Card> second;
};

#endif // GAME_H
