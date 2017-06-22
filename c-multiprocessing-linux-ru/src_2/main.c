#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>
#include <time.h>
#include <stdio.h>
#include <string.h>

#include <sys/wait.h>

#include "ipc.h"
#include "pa2345.h"

#include "log.h"
#include "pipes.h"
#include "banking.h"
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
    sprintf(str, log_started_fmt, get_physical_time(), id,
            getpid(), getppid(), 0);
    return str;
}
/**
 * Функция формирует строку для отправки - завершение работы процесса.
 **/
char* make_str_done(local_id id) {
    char *str = malloc(strlen(log_done_fmt) + 50);
    sprintf(str, log_done_fmt, get_physical_time(), id, 0);
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
    msg->s_header.s_local_time  = get_physical_time();
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
    msg->s_header.s_local_time  = get_physical_time();
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
void wait_message_from_all(struct process_pipes *selt,
                           int message_type) {
    //eep(1);
    struct process_pipes *process = selt;
    int process_number = get_process_number(process->pipes,
                                            process->process_count);
    Message msg;
    for(int i = 1; i < process->process_count; i++) {
        if(i != process_number) {
            if(receive(process, i, &msg) == -1) {
                i--;
            } else if(msg.s_header.s_type != message_type) {
                i--;
            } // иначе сообщение принято и его тип определён
        }
    }
}

void add_state(BalanceHistory *history, int balance) {
    int cur_time = get_physical_time();
    for(int i = 1; i < cur_time; i++) {
        if(history->s_history[i].s_time == 0) {
            history->s_history[i] = history->s_history[i - 1];
            history->s_history[i].s_time += 1;
            history->s_history_len += 1;
        }
    }
    int cur_len = history->s_history_len;
    if(cur_len > 1)
        while(history->s_history[cur_len - 1].s_time + 1 < cur_time) {
            history->s_history[cur_len - 1] = history->s_history[cur_len - 2];
            history->s_history[cur_len - 1].s_time += 1;
            cur_len++;
            history->s_history_len++;
        }
    if(history->s_history[cur_len - 1].s_time == cur_time) {
        history->s_history[cur_len - 1].s_balance = balance;
    } else {
        history->s_history[cur_len].s_balance = balance;
        history->s_history[cur_len].s_time    = cur_time;
        history->s_history[cur_len].s_balance_pending_in = 0;
        history->s_history_len += 1;
    }
}

void wait_all_done(struct process_pipes *self, int *balance, int not_done) {
    struct process_pipes *process = self;
    int process_number = get_process_number(process->pipes,
                                            process->process_count);
    Message msg;
    TransferOrder* order;
    Message *ack;
    while(not_done > 0) {
        if(receive_any(process, &msg) != -1) {
            switch(msg.s_header.s_type){
            case TRANSFER:
                order =(TransferOrder*)msg.s_payload;
                *balance += order->s_amount;
                ack = make_empty_message(ACK);
                log_event_transfer_in(get_physical_time(),
                                      order->s_src, process_number,
                                      order->s_amount);
                send(&process, 0, ack);
                free_message(ack);
                break;
            case DONE:
                not_done--;
                break;
            default:
                break;
            }
        }
    }
}

void transfer(void * parent_data, local_id src, local_id dst,
              balance_t amount) {
    struct process_pipes *pipes = (struct process_pipes*)(parent_data);
    Message msg;
    msg.s_header.s_magic       = MESSAGE_MAGIC;
    msg.s_header.s_type        = TRANSFER;
    msg.s_header.s_local_time  = get_physical_time();
    msg.s_header.s_payload_len = sizeof(TransferOrder);

    TransferOrder *order = (TransferOrder*)(msg.s_payload);
    order->s_amount = amount;
    order->s_src = src;
    order->s_dst = dst;
    send(pipes, src, &msg);

    while(receive(pipes, dst, &msg) == -1
            && msg.s_header.s_type != ACK);
}
// точка входа в программу
int main(int argc, char *argv[]) {
    int good_args = 0;
    int child_processes = 0;
    // проверка и считывание аргументов командной строки
    if(argc > 3 && !strcmp("-p", argv[1])) {
        child_processes = atoi(argv[2]);
        if(child_processes >= 1 && child_processes <= 10) {
            if((argc - 2) >= child_processes)
                good_args = 1;
        }
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
            int balance = atoi(argv[2 + id]);
            BalanceHistory history;
            for(int j = 0; j < MAX_T + 1; j++) {
                history.s_history[j].s_balance = 0;
                history.s_history[j].s_balance_pending_in = 0;
                history.s_history[j].s_time = 0;
            }
            history.s_id = id;
            history.s_history_len = 1;
            history.s_history[0].s_time = 0;
            history.s_history[0].s_balance = balance;
            //add_state(&history, balance);
            // закрытие всех лишних каналов передачи данных
            close_pipes_except(all_pipes, PROCESSES_COUNT, id);
            // инициализация структуры каналов связи процесса
            struct process_pipes process;
            process.pipes = all_pipes[id];
            process.process_count = PROCESSES_COUNT;
            // отсылка сообщения начала выполнения процесса
            Message *msg = make_message_started(id);
            log_events_started(get_physical_time(), id, balance);
            send_multicast(&process, msg);
            free_message(msg);
            // ожидание сообщений о запуске всех процессов
            // от всех клиентов
            wait_message_from_all(&process, STARTED);
            log_events_received_all_started(get_physical_time(), id);
            Message in_msg;
            int done = 0;
            TransferOrder *order;
            Message *ack;
            int not_done = CHILD_PROCESSES_COUNT;
            while(!done) {
                //sleep(1);
                if(receive_any(&process, &in_msg) != -1) {
                    switch(in_msg.s_header.s_type) {
                    case TRANSFER:
                        if(get_last_sender() == 0) {
                            // получен запрос на перевод средств от
                            // процесса родителя
                            order = (TransferOrder*)in_msg.s_payload;
                            balance -= order->s_amount;
                            add_state(&history, balance);
                            send(&process, order->s_dst, &in_msg);
                            log_event_transfer_out(get_physical_time(),
                                                   id, order->s_dst,
                                                   order->s_amount);
                        } else if(get_last_sender() > 0){
                            // получен запрос на перевод средств от
                            // процесса С
                            order =(TransferOrder*)in_msg.s_payload;
                            balance += order->s_amount;
                            add_state(&history, balance);
                            ack = make_empty_message(ACK);
                            log_event_transfer_in(get_physical_time(),
                                                  order->s_src, id,
                                                  order->s_amount);
                            send(&process, 0, ack);
                            free_message(ack);
                        }
                        break;
                    case STOP:
                        if(!get_last_sender()) {
                            // получен запрос остановки от главного
                            // процесса
                            add_state(&history, balance);
                            done = 1;
                            not_done--;
                        }
                        break;
                    case DONE:
                        not_done--;
                        break;
                    default:
                        break;
                    }
                }
            }
            // отсылка сообщения об окончании выполнения
            // полезной работы
            msg = make_message_done(id);
            log_events_done(get_physical_time(), id, balance);
            send_multicast(&process, msg);
            free_message(msg);
            // ожидание сообщений об окончании выполнения
            // полезной работы от всех клиентов
            wait_all_done(&process, &balance, not_done);
            log_events_received_all_done(get_physical_time(), id);
            msg = make_history_message(&history);
            send(&process, 0, msg);
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
    log_events_received_all_started(get_physical_time(), id);

    bank_robbery(&process, CHILD_PROCESSES_COUNT);

    Message *stop = make_empty_message(STOP);
    send_multicast(&process, stop);
    free_message(stop);
    // ожидание сообщений об окончании выполнения
    // полезной работы от всех клиентов
    wait_message_from_all(&process, DONE);
    log_events_received_all_done(get_physical_time(), id);

    Message history[CHILD_PROCESSES_COUNT];
    AllHistory all;
    all.s_history_len = 0;
    for(int i = 1; i < process.process_count; i++) {
        if(receive(&process, i, &history[all.s_history_len]) == -1) {
            i--;
        } else if(history[all.s_history_len].s_header.s_type != BALANCE_HISTORY) {
            i--;
        } else {
            all.s_history[all.s_history_len]
                = *((BalanceHistory*)(history[all.s_history_len].s_payload));
            all.s_history_len += 1;
        }
    }

    print_history(&all);

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
