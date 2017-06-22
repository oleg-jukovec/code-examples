#include "lamport.h"

timestamp_t cur_time = 0;

/**
 * Функция возвращает локальное время.
 **/
timestamp_t get_lamport_time() {
    return cur_time;
}
/**
 * Функция увеличивает значение логических часов.
 **/
void inc_lamport_time() {
    cur_time += 1;
}
/**
 * Функция устанавливает значение логических часов.
 **/
void set_lamport_time(timestamp_t time) {
    cur_time = time;
}
/**
 * Если текущее время < time. То функция устанавливает
 * значение логических часов как time + 1.
 **/
void set_inc_lamport_time(timestamp_t time) {
    if(cur_time < time)
        cur_time = time;
    cur_time++;
}
/**
 * Функция инициализирует значение логических часов.
 **/
void init_lamport_time() {
    cur_time = 0;
}
