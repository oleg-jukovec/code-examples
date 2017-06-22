/*
 * Файл game.cpp
 */
#include "game.h"

// объект конструируется из списка карт
Game::Game(QVector<Card> cards)
    : all_cards(cards), cur_step(NOBODY)
{

}
// метод запускает новую игру
void Game::new_game()
{
    // временный вектор для хранения не занятых карт
    QVector<Card> tmp(all_cards);
    // очищаются векторы карт 1 и 2 игрока
    first.clear();
    second.clear();
    // заполняется вектор карт для первого игрока
    for(int i = 0; i < CARDS_NUMBER; i++)
    {
        // генерируется индекс карты из оставшихся в
        // векторе tmp
        int card_index = qrand() % tmp.size();
        // добавляем к вектору карт первого игрока карту
        first.append(tmp[card_index]);
        // удаляем карту из вектора tmp
        tmp.remove(card_index);
    }
    // заполняется вектор карт для второго игрока
    for(int i = 0; i < CARDS_NUMBER; i++)
    {
        // генерируется индекс карты из оставшихся в
        // векторе tmp
        int card_index = qrand() % tmp.size();
        // добавляем к вектору карт первого игрока карту
        second.append(tmp[card_index]);
        // удаляем карту из вектора tmp
        tmp.remove(card_index);
    }
    // устанавливается текущий игрок
    cur_step   = Game::players(qrand() % NOBODY);
    // генерируется тип урона текущего хода
    generate_damage();
}
// метод возвращает вектор карт текущего игрока
QVector<Card> Game::get_cards(Game::players player) const
{
    // возвращает вектор карт первого игрока
    if(player == FIRST)
        return QVector<Card>(first);
    // возвращает вектор карт второго игрока
    else if(player == SECOND)
        return QVector<Card>(second);
    // возвращает пустой вектор
    return QVector<Card>();
}
// метод делает ход игры
void Game::do_step(quint16 from_card, quint16 to_card)
{
    // если игра не окончена
    if(!is_end())
    {
        // наносим урон от текущего игрока к противнику
        set_damaged(get_cur_player(), get_damage_type()
                    , from_card, to_card);
        // если игра окончена, устанавливаем текущего игрока
        // NOBODY
        if(is_end())
            cur_step = NOBODY;
        // иначе
        else
        {
            // ход переходит к следующему игрока
            next_player();
            // генерируем тип урона хода
            generate_damage();
        }
    }

}
// метод проверяет, достигнут ли конец игры
bool Game::is_end() const
{
    // флаги по умолчанию - игроки мертвы
    bool first_dead = true, second_dead = true;
    // перебор всех доступных карт
    for(int i = 0; i < CARDS_NUMBER; i++)
    {
        // если карта первого игрока жива
        if(first[i].is_leave())
            // то первый игрок жив
            first_dead = false;
        // если карта второго игрока жива
        if(second[i].is_leave())
            // то второй игрок жив
            second_dead = false;

    }
    // возвращает true если первый или второй игрок мёртв
    return first_dead || second_dead;
}

Game::players Game::get_winner() const
{
    // флаги по умолчанию - игроки мертвы
    bool first_dead = true, second_dead = true;
    // перебор всех доступных карт
    for(int i = 0; i < CARDS_NUMBER; i++)
    {
        // если карта первого игрока жива
        if(first[i].is_leave())
            // то первый игрок жив
            first_dead = false;
        // если карта второго игрока жива
        if(second[i].is_leave())
            // то второй игрок жив
            second_dead = false;
    }
    // если два игрока живы или мертвы - никто не победил
    if(!first_dead && !second_dead)
        return NOBODY;
    if(first_dead && second_dead)
        return NOBODY;
    // если мёртв только второй игрок
    if(second_dead)
        // победил первый игрок
        return FIRST;
    // иначе
    else
        // победил второй игрок
        return SECOND;
}
// метод возвращает текущего игрока
Game::players Game::get_cur_player() const
{
    return cur_step;
}
// метод возвращает текущий тип урона
Game::damage  Game::get_damage_type() const
{
    return cur_damage;
}
// метод случайным образом генерирует тип урона
void Game::generate_damage()
{
    cur_damage = Game::damage(qrand() % 3);
}
// метод переводит ход к следующему игроку
void Game::next_player()
{
    if(cur_step == FIRST)
        cur_step = SECOND;
    else if(cur_step == SECOND)
        cur_step = FIRST;
}
// метод наносит урон от карты player_card игрока player
// карте enemy_card противника
void Game::set_damaged(Game::players player, Game::damage type,
                        quint16 player_card, quint16 enemy_card)
{
    // получаем указатели на карты для первого игрока
    Card *from = &first[player_card], *to = &second[enemy_card];
    // если текущий ход у второго игрока
    if(player == SECOND)
    {
        // получаем указатели для второго игрока
        from = &second[player_card];
        to = &first[enemy_card];
    // если текущий ход ни у одного из игроков
    } else if(player == NOBODY)
    {
        // завершаем работу цикла
        return;
    }
    // переменная хранит нанечённый урон
    qint16 damage_value = 0;
    // в зависимости от типа урона присваиваем
    // значение нанесённого урона
    if(type == Game::MELEE)
    {
        // для ближнего боя
        damage_value = from->get_melee();
    } else if(type == Game::RANGE)
    {
        // для дальнего боя
        damage_value = from->get_range();
    } else if(type == Game::MAGIC)
    {
        // для магического урона
        damage_value = from->get_magic();
    }
    // наносим урон карте противника
    to->damage(damage_value);
}
