#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>
#include <time.h>
#include <stdio.h>
#include <string.h>

#include <sys/wait.h>

#include "ipc.h"
#include "pa2345.h"
#include "banking.h"

#include "log.h"
#include "pipes.h"
#include "lamport.h"
#include "queue.h"
/**
 * Функция возращает от кого было прочитано последнее сообщение.
 * Реализация в ipc.c
 **/
int get_last_sender();
/**
 * Функция формирует строку для отправки - начало работы процесса.
 **/
char* make_str_started(local_id id) {
    char *str = malloc(strlen(log_started_fmt) + 50);
    sprintf(str, log_started_fmt, get_lamport_time(), id,
            getpid(), getppid(), 0);
    return str;
}
/**
 * Функция формирует строку для отправки - завершение работы процесса.
 **/
char* make_str_done(local_id id) {
    char *str = malloc(strlen(log_done_fmt) + 50);
    sprintf(str, log_done_fmt, get_lamport_time(), id, 0);
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
    msg->s_header.s_local_time  = get_lamport_time();
    msg->s_header.s_payload_len = sizeof(char) * msg_size;
    return msg;
}
/**
 * Функция создаёт пустое сообщение заданного типа.
 **/
Message* make_empty_message(int16_t type) {
    Message *msg = malloc(sizeof(Message));
    msg->s_header.s_magic       = MESSAGE_MAGIC;
    msg->s_header.s_type        = type;
    msg->s_header.s_local_time  = get_lamport_time();
    msg->s_header.s_payload_len = 0;
    return msg;
}
/**
 * Функция создаёт новое сообщение с историей.
 **/
Message* make_history_message(BalanceHistory *history) {
    Message *msg = make_empty_message(BALANCE_HISTORY);
    
    msg->s_header.s_payload_len = sizeof(BalanceHistory);
    BalanceHistory *tmp = (BalanceHistory*)msg->s_payload;
    tmp->s_id = history->s_id;
    tmp->s_history_len = history->s_history_len;
    for(int i = 0; i < history->s_history_len; i++) {
        tmp->s_history[i] = history->s_history[i];
    }
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
void wait_message_from_all(struct process_pipes *self,
                           int message_type) {
    //eep(1);
    struct process_pipes *process = self;
    int process_number = get_process_number(process->pipes,
                                            process->process_count);
    Message msg;
    for(int i = 1; i < process->process_count; i++) {
        if(i != process_number) {
            if(receive(process, i, &msg) == -1) {
                i--;
            } else if(msg.s_header.s_type != message_type) {
                i--;
            } else {
                // иначе сообщение принято и его тип определён
                set_inc_lamport_time(msg.s_header.s_local_time);
            }
        }
    }
}

int request_cs(const void * self) {
    queue_clear();
    struct process_pipes *process = (struct process_pipes *)self;
    inc_lamport_time(); //
    Message *msg = make_empty_message(CS_REQUEST);
    send_multicast(process, msg);
    free_message(msg);
    struct queue_data data;
    data.id = get_process_number(process->pipes,
                                 process->process_count);
    data.time = get_lamport_time();
    queue_push(data);
    int replies = 0;
    Message tmp;
    Message *reply;
    while(replies < process->process_count - 2) {
        if(receive_any(process, &tmp) != -1) {
            set_inc_lamport_time(tmp.s_header.s_local_time);
            switch(tmp.s_header.s_type) {
            case CS_REQUEST:
                data.id   = get_last_sender();
                data.time = tmp.s_header.s_local_time;
                queue_push(data);
                
                inc_lamport_time();

                reply = make_empty_message(CS_REPLY);
                send(process, get_last_sender(), reply);
                free_message(reply);
                break;
            case CS_RELEASE:
                queue_remove(get_last_sender());
                break;
            case CS_REPLY:
                replies++;
                break;
            case DONE:
                process->active_count -= 1;
                break;
            default:
                break;
            }
        }
    }

    while(queue_begin().id != get_process_number(process->pipes,
                                                 process->process_count)) {
        int quit = 0;
        while(!quit) {
            if(receive(process, queue_begin().id, &tmp) != -1) {
                inc_lamport_time();
                if(tmp.s_header.s_type == CS_RELEASE) {
                    queue_remove(get_last_sender());
                    quit = 1;
                } else if (tmp.s_header.s_type == DONE){
                    queue_remove(get_last_sender());
                    process->active_count -= 1;
                    quit = 1;
                }
            }
        }
    }
    return 0;
}

int release_cs(const void * self) {
    void *tmp = (void*)self;
    Message *msg = make_empty_message(CS_RELEASE);
    send_multicast(tmp, msg);
    free_message(msg);
    return 0;
}

// не используется, но нужна для компиляции
void transfer(void * parent_data, local_id src, local_id dst,
              balance_t amount)
{
    // не используется, но нужна для компиляции
}
// точка входа в программу
int main(int argc, char *argv[]) {
    int good_args = 0;
    int child_processes = 0;
    int mutex = 0;
    // проверка и считывание аргументов командной строки
    if(argc > 2) {
        for(int i = 1; i < argc; i++) {
            if(!strcmp(argv[i], "--mutexl")) {
                mutex = 1;
            } else if(!strcmp(argv[i], "-p")) {
                if(i + 1 < argc) {
                    child_processes = atoi(argv[i + 1]);
                    if(child_processes >= 1 && child_processes <= 10)
                        good_args = 1;
                }
            }
        }
    }
    // если аргументы заданы неверно - сообщаем об ошибке
    if(!good_args) {
        printf("Usage:\n");
        printf("%s -p N [--mutexl]\n", argv[0]);
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
            init_lamport_time(); ///
            // закрытие всех лишних каналов передачи данных
            close_pipes_except(all_pipes, PROCESSES_COUNT, id);
            // инициализация структуры каналов связи процесса
            struct process_pipes process;
            process.pipes = all_pipes[id];
            process.process_count = PROCESSES_COUNT;
            process.active_count  = CHILD_PROCESSES_COUNT - 1;
            // отсылка сообщения начала выполнения процесса
            inc_lamport_time(); ///
            Message *msg = make_message_started(id);
            log_events_started(get_lamport_time(), id, 0);
            send_multicast(&process, msg);
            free_message(msg);
            // ожидание сообщений о запуске всех процессов
            // от всех клиентов
            wait_message_from_all(&process, STARTED);
            log_events_received_all_started(get_lamport_time(), id);
            // полезная работа

            // буфер
            char str[256];
            for(int j = 1; j <= id * 5; j++) {
                if(mutex)
                    request_cs(&process);
                str[0] = '\0';
                log_events_loop(id, i, id*5);
                sprintf(str, log_loop_operation_fmt, id, j, id*5);
                print(str);
                if(mutex)
                    release_cs(&process);
            }

            // отсылка сообщения об окончании выполнения
            // полезной работы
            inc_lamport_time(); ///
            msg = make_message_done(id);
            log_events_done(get_lamport_time(), id, 0);
            send_multicast(&process, msg);
            free_message(msg);
            // ожидание сообщений завершения от всех дочерних процессов
            // и обработка запросов на вхождение в критическую область
            Message tmp;
            struct queue_data data;
            while(process.active_count > 0) {
                if(receive_any(&process, &tmp) != -1) {
                    set_inc_lamport_time(tmp.s_header.s_local_time);
                    switch(tmp.s_header.s_type) {
                    case CS_REQUEST:
                        data.id   = get_last_sender();
                        data.time = tmp.s_header.s_local_time;
                        queue_push(data);

                        inc_lamport_time();

                        msg = make_empty_message(CS_REPLY);
                        send(&process, get_last_sender(), msg);
                        free_message(msg);
                        break;
                    case CS_RELEASE:
                        queue_remove(get_last_sender());
                        break;
                    case DONE:
                        process.active_count -= 1;
                        break;
                    default:
                        break;
                    }
                }
            }

            // ожидание сообщений об окончании выполнения
            // полезной работы от всех клиентов
            // wait_message_from_all(&process, DONE);
            log_events_received_all_done(get_lamport_time(), id);
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

    init_lamport_time();
    
    // закрытие всех неиспользуемых каналов связи
    close_pipes_except(all_pipes, PROCESSES_COUNT, id);
    struct process_pipes process;
    // инициализация структуры каналов связи процесса
    process.pipes = all_pipes[id];
    process.process_count = PROCESSES_COUNT;
    // ожидание сообщений о запуске всех процессов
    // от всех клиентов
    wait_message_from_all(&process, STARTED);
    log_events_received_all_started(get_lamport_time(), id);
    // ожидание сообщений об окончании выполнения
    // полезной работы от всех клиентов   
    wait_message_from_all(&process, DONE);
    log_events_received_all_done(get_lamport_time(), id);
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
