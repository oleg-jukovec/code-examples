#ifndef DR_ARRAY_H
#define DR_ARRAY_H

#include "ipc.h"
// структура определяет данные, хранимые в в запросе
struct request_data {
    // локальное время
    timestamp_t time;
    // индефикатор процесса
    local_id    id;
};
/**
 * Функция сравнивает два запроса, возвращает 1, если first > second.
 **/
int more(struct request_data first, struct request_data second);
/**
 * Функция инициализирует массив с поддержкой заданного количества процессов.
 */
void drarray_init(int processes_count);
/**
 * Функция очищает память от массива.
 */
void drarray_free();
/**
 * Функция устанавливает процесс на ожидание.
 */
void drarray_set(int process);
/**
 * Функция снимает флаг ожидание от процесса.
 */
void drarray_unset(int process);
/**
 * Функция проверяет, установлен ли флаг ожидание для процесса.
 */
int drarray_isset(int process);
#endif
