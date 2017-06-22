#include <stdlib.h>
#include <unistd.h>

#include "pipes.h"

#include "log.h"

// индефикатор канала чтения
#define READ_CH  0
// индефикатор канала записи
#define WRITE_CH 1
/**
 * Метод создаёт новые канали связи. Но не инициализирует их.
 * (не закрывает ненужные).
 **/
int*** create_pipes(unsigned int processes) {
    int ***pipes;
    // выделение памяти
    pipes = malloc(sizeof(int**) * processes);
    for(unsigned int i = 0; i < processes; i++) {
        pipes[i] = malloc(sizeof(int*) * processes);
        for(unsigned int j = 0; j < processes; j++) {
            pipes[i][j] = malloc(sizeof(int) * 2);
        }
    }
    // обнуление значений выделенной памяти
    for(unsigned int i = 0; i < processes; i++) {
        for(unsigned int j = 0; j < processes; j++){
            for(unsigned int k = 0; k < 2; k++) {
                pipes[i][j][k] = 0;
            }
        }
    }
    // создание каналов связи между процессами
    for(unsigned int i = 0; i < processes; i++) {
        for(unsigned int j = i + 1; j < processes; j++) {
            // открываем два канала, один для чтение текущего потока
            int tmp_read[2];
            pipe(tmp_read);
            log_pipe_open(j, i, tmp_read[READ_CH], tmp_read[WRITE_CH]);
            // второй для записи в текущий поток
            int tmp_write[2];
            pipe(tmp_write);
            log_pipe_open(i, j, tmp_write[READ_CH], tmp_write[WRITE_CH]);
            // назначение индефикаторов каналов между потоками
            pipes[i][j][READ_CH]   = tmp_read[READ_CH];
            pipes[i][j][WRITE_CH]  = tmp_write[WRITE_CH];
            pipes[j][i][READ_CH]   = tmp_write[READ_CH];
            pipes[j][i][WRITE_CH]  = tmp_read[WRITE_CH];
        }
    }
    // возращение созданной структуры данных
    return pipes;
}
/**
 * Очистка памяти от индефикаторов созданных каналов связи.
 **/
void free_pipes(int*** pipes, unsigned int processes) {
    for(int i = 0; i < processes; i++) {
        for(int j = 0; j < processes; j++) {
            free(pipes[i][j]);
        }
        free(pipes[i]);
    }
    free(pipes);
}
/**
 * Номер процесса можно однозначно определить исходя из его
 * структуры индефикаторов каналов связи.
 **/
int get_process_number(int **pipes, unsigned int max_number) {
    for(unsigned int i = 0; i < max_number; i++) {
        // индефикатор канала записи и чтения для самого
        // процесса = 0
        if(pipes[i][READ_CH] == 0 && pipes[i][WRITE_CH] == 0)
            return i;
    }
    return -1;
}
/**
 * Функция возвращает индефикатор канала на чтение данных от
 * процесса.
 **/
int get_read_channel(int **pipes, unsigned int process) {
    return pipes[process][READ_CH];
}
/**
 * Функция возвращает индефикатор канала на запись данных от
 * процесса.
 **/
int get_write_channel(int **pipes, unsigned int process) {
    return pipes[process][WRITE_CH];
}
/**
 * Функция закрывает индефикаторы каналов.
 **/
void close_pipes(int **pipes, unsigned int processes) {
    for(unsigned int i = 0; i < processes; i++) {
        if(pipes[i][READ_CH] != 0)
            close(pipes[i][READ_CH]);
        if(pipes[i][WRITE_CH] != 0)
            close(pipes[i][WRITE_CH]);
    }
}
/**
 * Функция закрывает индефикаторы каналов за исключением
 * индефикаторов процесса.
 **/
void close_pipes_except(int ***pipes, unsigned int processes
                             , unsigned int except) {
    for(unsigned int i = 0; i < processes; i++)
        if(i != except) {
            close_pipes(pipes[i], processes);
        }
}
