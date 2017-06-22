#ifndef LAMPORT_H
#define LAMPORT_H

#include "ipc.h"

/**
 * Функция увеличивает значение логических часов.
 **/
void inc_lamport_time();
/**
 * Функция устанавливает значение логических часов.
 **/
void set_lamport_time(timestamp_t time);
/**
 * Если текущее время <= time. То функция устанавливает
 * значение логических часов как time + 1.
 **/
void set_inc_lamport_time(timestamp_t time);
/**
 * Функция инициализирует значение логических часов.
 **/
void init_lamport_time();

#endif
