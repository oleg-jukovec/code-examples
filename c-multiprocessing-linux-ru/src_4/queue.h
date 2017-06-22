#ifndef QUEUE_H
#define QUEUE_H

#include "ipc.h"
// структура определяет данные, хранимые в очереди
struct queue_data {
    // локальное время
    timestamp_t time;
    // индефикатор процесса
    local_id    id;
};
/**
 * Функция вставляет данные в очередь согласно приоритета локального
 * времени.
 **/
void queue_push(struct queue_data data);
/**
 * Функция возвращает первый элемент очереди.
 **/
struct queue_data queue_begin();
/**
 * Функция удаляет элемент с id заданного процесса.
 **/
void queue_remove(local_id id);
/**
 * Функция возвращает пуста ли очередь.
 **/
int queue_empty();
/**
 * Функция очищает очередь.
 **/
void queue_clear();

#endif
