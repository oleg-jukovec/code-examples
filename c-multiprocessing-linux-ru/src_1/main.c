#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>
#include <time.h>
#include <stdio.h>
#include <string.h>

#include <sys/wait.h>

#include "ipc.h"
#include "pa1.h"

#include "log.h"
#include "pipes.h"
/**
 * Функция формирует строку для отправки - начало работы процесса.
 **/
char* make_str_started(local_id id) {
    char *str = malloc(strlen(log_started_fmt) + 50);
    sprintf(str, log_started_fmt, id, getpid(), getppid());
    return str;
}
/**
 * Функция формирует строку для отправки - завершение работы процесса.
 **/
char* make_str_done(local_id id) {
    char *str = malloc(strlen(log_done_fmt) + 50);
    sprintf(str, log_done_fmt, id);
    return str;
}
/**
 * Функция очищает память от сформированной строки.
 **/
void free_str(char *str) {
    free(str);
}
/**
 * Функция создаёт новое сообщение для отправки.
 **/
Message* make_message(int16_t type, char *message) {
    // выделяем память под сообщение
    Message *msg = malloc(sizeof(Message));
    // копируем тело сообщение
    int msg_size;
    for(msg_size = 0; message[msg_size] != '\n'; msg_size++ ) {
        msg->s_payload[msg_size] = message[msg_size];
    }
    msg->s_payload[msg_size++] = '\n';
    // заполняем заголовок сообщения
    msg->s_header.s_magic       = MESSAGE_MAGIC;
    msg->s_header.s_type        = type;
    msg->s_header.s_local_time  = time(0);
    msg->s_header.s_payload_len = sizeof(char) * msg_size;
    return msg;
}
/**
 * Функция создаёт сообщение для отправки - начало работы процесса.
 **/
Message* make_message_started(local_id id) {
    char *str = make_str_started(id);
    Message *msg = make_message(STARTED, str);
    free_str(str);
    return msg;
}
/**
 * Функция создаёт сообщение для отправки - завершение работы процесса.
 **/
Message* make_message_done(local_id id) {
    char *str = make_str_done(id);
    Message *msg = make_message(DONE, str);
    free_str(str);
    return msg;
}
/**
 * Функция очищает выделенную под сообщение память.
 **/
void free_message(Message *message) {
    free(message);
}

/**
 * Метод ожидает сообщение заданного типа от всех процессов.
 **/
void wait_message_from_all(struct process_pipes *selt,
                           int message_type) {
    struct process_pipes *process = selt;
    int process_number = get_process_number(process->pipes,
                                            process->process_count);
    Message msg;
    for(int i = 1; i < process->process_count; i++) {
        if(i != process_number) {
            receive(process, i, &msg);
            if(msg.s_header.s_type != message_type) {
                i--;
            }
        }
    }
}
// точка входа в программу
int main(int argc, char *argv[]) {
    int good_args = 0;
    int child_processes = 0;
    // проверка и считывание аргументов командной строки
    if(argc == 3 && !strcmp("-p", argv[1])) {
        child_processes = atoi(argv[2]);
        if(child_processes < 1 || child_processes > 10) {
            good_args = 0;
        } else {
            good_args = 1;
        }
    } else {
        good_args = 0;
    }
    // если аргументы заданы неверно - сообщаем об ошибке
    if(!good_args) {
        printf("Usage:\n");
        printf("%s -p N\n", argv[0]);
        printf("N must be between 1 and 10\n");
        exit(EXIT_FAILURE);
    }
    // инициализация констант
    // количество дочерних процессов
    const int CHILD_PROCESSES_COUNT = child_processes;
    // всего процессов
    const int PROCESSES_COUNT = CHILD_PROCESSES_COUNT + 1;
    // открытие дескрипторов файлов логирования
    open_logs();
    // инициализация каналов связи
    int ***all_pipes = create_pipes(PROCESSES_COUNT);
    local_id id = 0;
    // запуск дочерних процессов
    for(int i = 0; i < CHILD_PROCESSES_COUNT; i++) {
        if(fork() == 0) {
            /**
             * Код дочерних процессов.
             **/
            // индефикатор текущего процесса
            id++;
            // закрытие всех лишних каналов передачи данных
            close_pipes_except(all_pipes, PROCESSES_COUNT, id);
            // инициализация структуры каналов связи процесса
            struct process_pipes process;
            process.pipes = all_pipes[id];
            process.process_count = PROCESSES_COUNT;
            // отсылка сообщения начала выполнения процесса
            Message *msg = make_message_started(id);
            log_events_started(id);
            send_multicast(&process, msg);
            free_message(msg);
            // ожидание сообщений о запуске всех процессов
            // от всех клиентов
            wait_message_from_all(&process, STARTED);
            log_events_received_all_started(id);
            // отсылка сообщения об окончании выполнения
            // полезной работы
            msg = make_message_done(id);
            log_events_done(id);
            send_multicast(&process, msg);
            free_message(msg);
            // ожидание сообщений об окончании выполнения
            // полезной работы от всех клиентов
            wait_message_from_all(&process, DONE);
            log_events_received_all_done(id);
            // закрытие каналов связи процесса
            close_pipes(process.pipes, process.process_count);
            // закрытие файловых дескрипторов
            close_logs();
            // завершения процесса
            exit(EXIT_SUCCESS);
        } else {
            id++;
        }
    }
    /**
     * Код родительского процесса.
     **/
    id = 0;
    // закрытие всех неиспользуемых каналов связи
    close_pipes_except(all_pipes, PROCESSES_COUNT, id);
    struct process_pipes process;
    // инициализация структуры каналов связи процесса
    process.pipes = all_pipes[id];
    process.process_count = PROCESSES_COUNT;
    // ожидание сообщений о запуске всех процессов
    // от всех клиентов
    wait_message_from_all(&process, STARTED);
    log_events_received_all_started(id);
    // ожидание сообщений об окончании выполнения
    // полезной работы от всех клиентов   
    wait_message_from_all(&process, DONE);
    log_events_received_all_done(id);
    // закрытие каналов связи процесса
    close_pipes(process.pipes, PROCESSES_COUNT);
    // очистка памяти
    free_pipes(all_pipes,PROCESSES_COUNT);
    // ожидание завершения дочерних процессов
    int status;
    for(int i = 0; i < CHILD_PROCESSES_COUNT; i++) {
        wait(&status);
        // если процесс завершился неудачно
        if(status != 0)
            exit(EXIT_FAILURE);
    }
    // закрытие файловых дескрипторов
    close_logs();
    // завершение процесса
    exit(EXIT_SUCCESS);
}
