#include <stdio.h>
#include <unistd.h>

#include "log.h"

#include "common.h"
#include "pa2345.h"

// файловый дескриптор для вывода сообщений о создании
// каналов передачи данных
FILE *pipes  = 0;
// файловый дескриптор для вывода сообщений о событиях
FILE *events = 0;
// метод открывает файлы для записи
// должен быть вызван процессом родителем
int open_logs() {
    if((pipes = fopen(pipes_log, "w")) == 0) {
        return -1;
    }
    if((events = fopen(events_log, "w")) == 0) {
        fclose(pipes);
        pipes = 0;
        return -1;
    }
    return 0;
}
// метод закрывает файлы для записи
// должен быть вызван всеми процессами
// (при fork дескрипторы копируются)
void close_logs() {
    fclose(pipes);
    fclose(events);
}
// метод записывает в лог событие создание однонаправленного канала
// связи
// from - процесс, который читает из канала
// to - процесс, который пишет в канал
void log_pipe_open(local_id from, local_id to, int read_id, int write_id) {
    fprintf(pipes, "Pipe open from process %d to process %d\n", from, to);
    fprintf(pipes, "Read id = %d, write id = %d\n", read_id, write_id);
    fflush(pipes);
}
// метод записывает в лог событие о запуске процесса
void log_events_started(int timestamp, local_id id, int balance) {
    printf(log_started_fmt, timestamp, id, getpid(), getppid(), balance);
    fprintf(events, log_started_fmt, timestamp, id, getpid(), getppid()
            ,balance );
    fflush(stdout);
    fflush(events);
}
// метод записывает в лог событие о запуске всех процессов
void log_events_received_all_started(int timestamp, local_id id) {
    printf(log_received_all_started_fmt, timestamp, id);
    fprintf(events, log_received_all_started_fmt, timestamp, id);
    fflush(stdout);
    fflush(events);
}
// метод записывает в лог событие об окончании полезной работы процесса
void log_events_done(int timestamp, local_id id, int balance) {
    printf(log_done_fmt, timestamp, id, balance);
    fprintf(events, log_done_fmt, timestamp, id, balance);
    fflush(stdout);
    fflush(events);
}
// метод записывает в лог событие об окончании полезной работы
// всех процессов
void log_events_received_all_done(int timestamp, local_id id) {
    printf(log_received_all_done_fmt, timestamp, id);
    fprintf(events, log_received_all_done_fmt, timestamp, id);
    fflush(stdout);
    fflush(events);
}

// метод записывает в лог событие вывода в цикле
void log_events_loop(local_id id, int it, int max_it) {
    fprintf(events, log_loop_operation_fmt, id, it, max_it);
    fflush(events);
}
