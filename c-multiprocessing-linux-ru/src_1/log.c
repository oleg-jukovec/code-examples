#include <stdio.h>
#include <unistd.h>

#include "log.h"

#include "common.h"
#include "pa1.h"

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
void log_events_started(local_id id) {
    printf(log_started_fmt, id, getpid(), getppid());
    fprintf(events, log_started_fmt, id, getpid(), getppid());
    fflush(stdout);
    fflush(events);
}
// метод записывает в лог событие о запуске всех процессов
void log_events_received_all_started(local_id id) {
    printf(log_received_all_started_fmt, id);
    fprintf(events, log_received_all_started_fmt, id);
    fflush(stdout);
    fflush(events);
}
// метод записывает в лог событие об окончании полезной работы процесса
void log_events_done(local_id id) {
    printf(log_done_fmt, id);
    fprintf(events, log_done_fmt, id);
    fflush(stdout);
    fflush(events);
}
// метод записывает в лог событие об окончании полезной работы
// всех процессов
void log_events_received_all_done(local_id id) {
    printf(log_received_all_done_fmt, id);
    fprintf(events, log_received_all_done_fmt, id);
    fflush(stdout);
    fflush(events);
}
