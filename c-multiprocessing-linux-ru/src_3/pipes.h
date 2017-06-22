#ifndef PIPES_H
#define PIPES_H

/**
 * Структура описывает каналы связи процесса.
 **/
struct process_pipes {
    // указатель на массив каналов связи
    int** pipes;
    // количество процессов
    int   process_count;
};

/**
 * Метод создаёт новые канали связи. Но не инициализирует их.
 * (не закрывает ненужные).
 **/
int*** create_pipes(unsigned int processes);
/**
 * Очистка памяти от индефикаторов созданных каналов связи.
 **/
void free_pipes(int*** pipes, unsigned int processes);
/**
 * Номер процесса можно однозначно определить исходя из его
 * структуры индефикаторов каналов связи.
 **/
int get_process_number(int **pipes, unsigned int max_number);
/**
 * Функция возвращает индефикатор канала на чтение данных от
 * процесса.
 **/
int get_read_channel(int **pipes, unsigned int process);
/**
 * Функция возвращает индефикатор канала на запись данных от
 * процесса.
 **/
int get_write_channel(int **pipes, unsigned int process);
/**
 * Функция закрывает индефикаторы каналов.
 **/
void close_pipes(int **pipes, unsigned int processes);
/**
 * Функция закрывает индефикаторы каналов за исключением
 * индефикаторов процесса.
 **/
void close_pipes_except(int ***pipes, unsigned int processes
                                    , unsigned int except);
#endif
