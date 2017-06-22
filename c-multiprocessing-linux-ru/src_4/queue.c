#include <stdlib.h>

#include "queue.h"

// указатель на начало очереди
struct queue_data  *queue = 0;
int                   len = 0;

int more(struct queue_data first, struct queue_data second) {
    if(first.time > second.time)
        return 1;
    if(first.time == second.time && first.id > second.id)
        return 1;
    return 0;
}

void copy(struct queue_data *to, struct queue_data *from, int len) {
    for(int i = 0; i < len; i++)
        to[i] = from[i];
}

void sort(struct queue_data *array, int len) {
    for(int i = 0; i < len - 1; i++)
        for(int j = 0; j < len - 1 - i; j++)
            if(more(array[j], array[j + 1])) {
                struct queue_data tmp = array[j];
                array[j] = array[j + 1];
                array[j + 1] = tmp;
            }
}
/**
 * Функция вставляет данные в очередь согласно приоритета локального
 * времени.
 **/
void queue_push(struct queue_data data) {
    struct queue_data *new_q = malloc(sizeof(struct queue_data)
                                                   * (len + 1));
    copy(new_q, queue, len);
    new_q[len] = data; 
    if(queue != 0)
        free(queue);
    queue = new_q;
    len++;
    sort(queue, len);
}
/**
 * Функция возвращает первый элемент очереди.
 **/
struct queue_data queue_begin() {
    return queue[0];
}
/**
 * Функция удаляет элемент с id заданного процесса.
 **/
void queue_remove(local_id id) {
    int count = 0;
    for(int i = 0; i < len; i++)
        if(queue[i].id == id)
            count++;
    if(!count)
        return;
    int new_len = len - count;
    struct queue_data *new_q = malloc(sizeof(struct queue_data) * new_len);
    int new_size = 0;
    for(int i = 0; i < len; i++) {
        if(queue[i].id != id)
            new_q[new_size++] = queue[i];
    }
    free(queue);
    queue = new_q;
    len   = new_size;
    
}
/**
 * Функция возвращает пуста ли очередь.
 **/
int queue_empty() {
    return len == 0;
}

void queue_clear() {
    if(len > 0) {
        free(queue);
        queue = 0;
    }
    len = 0;
}
