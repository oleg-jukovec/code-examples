#include <stdlib.h>

#include "dr_array.h"

int *array = 0;
int len = 0;
/**
 * Функция сравнивает два запроса, возвращает 1, если first > second.
 **/
int more(struct request_data first, struct request_data second) {
    if(first.time > second.time)
        return 1;
    if(first.time == second.time && first.id > second.id)
        return 1;
    return 0;
}
/**
 * Функция инициализирует массив с поддержкой заданного количества процессов.
 */
void drarray_init(int processes_count) {
    len = processes_count;
    array = malloc(sizeof(int) * len);
}
/**
 * Функция очищает память от массива.
 */
void drarray_free() {
    free(array);
    len = 0;
}
/**
 * Функция устанавливает процесс на ожидание.
 */
void drarray_set(int process) {
    if(process >= 0 && process < len)
        array[process] = 1;
}
/**
 * Функция снимает флаг ожидание от процесса.
 */
void drarray_unset(int process) {
    if(process >= 0 && process < len)
        array[process] = 0;
}
/**
 * Функция проверяет, установлен ли флаг ожидание для процесса.
 */
int drarray_isset(int process) {
    if(process >= 0 && process < len)
        return array[process] != 0;
    return 0;
}
