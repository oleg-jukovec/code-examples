#ifndef LOG_H
#define LOG_H

#include "ipc.h"
// метод открывает файлы для записи
// должен быть вызван процессом родителем
int open_logs();
// метод закрывает файлы для записи
// должен быть вызван всеми процессами
// (при fork дескрипторы копируются)
void close_logs();
// метод записывает в лог событие создание однонаправленного канала
// связи
// from - процесс, который читает из канала
// to - процесс, который пишет в канал
void log_pipe_open(local_id from, local_id to, int read_id, int write_id);
// метод записывает в лог событие о запуске процесса
void log_events_started(int timestamp, local_id id, int balance);
// метод записывает в лог событие о запуске всех процессов
void log_events_received_all_started(int timestamp, local_id id);
// метод записывает в лог событие об окончании полезной работы процесса
void log_events_done(int timestamp, local_id id, int balance);
// метод записывает в лог событие об окончании полезной работы
// всех процессов
void log_events_received_all_done(int timestamp, local_id id);
// метод записывает в лог событие о входящем переводе денег
void log_event_transfer_in(int timestamp, local_id from, local_id to, int cost);
// метод записывает в лог событие о исходящем переводе денег
void log_event_transfer_out(int timestamp, local_id from, local_id to, int cost);

#endif
