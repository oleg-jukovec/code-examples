#include <unistd.h>

#include "ipc.h"

#include "pipes.h"

int last_message_from = -1;

/**
 * Функция записывает сообщение в канал для записи.
 **/ 
int read_message(int pipe_read, Message *msg) {
    int16_t header[4];
    int need_read = sizeof(int16_t) * 4;
    uint8_t *p = (uint8_t*) header;
    int readed     = 0;
    int now_readed = 0;
    if((now_readed = read(pipe_read, p, need_read)) < 1)
        return -1;
    readed = now_readed;
    p += readed;
    while(readed < need_read) {
        now_readed = read(pipe_read, p, need_read - readed);
        if(now_readed > 0) {
            readed += now_readed;
            p += now_readed;
        }
    }
    msg->s_header.s_magic = header[0];
    msg->s_header.s_payload_len = header[1];
    msg->s_header.s_type = header[2];
    msg->s_header.s_local_time = header[3];
    p = (uint8_t*)msg->s_payload;
    readed = 0;
    need_read = header[1];
    while(readed < need_read) {
        now_readed = read(pipe_read, p, need_read - readed);
        if(now_readed > 0) {
            readed += now_readed;
            p += now_readed;
        }
    }
    return 0;
}
/**
 * Функция считывает сообщение из канала для записи.
 **/
int write_message(int pipe_write, const Message *msg) {
    int16_t header[4];
    header[0] = msg->s_header.s_magic;
    header[1] = msg->s_header.s_payload_len;
    header[2] = msg->s_header.s_type;
    header[3] = msg->s_header.s_local_time;
    int need_write = sizeof(int16_t) * 4;
    uint8_t *p = (uint8_t*) header;
    int writed     = 0;
    int now_writed = 0;
    while(writed < need_write) {
        now_writed = write(pipe_write, p, need_write - writed);
        if(now_writed > 0) {
            writed += now_writed;
            p += now_writed;
        }
    }
    p = (uint8_t*) msg->s_payload;
    need_write = header[1];
    writed     = 0;
    now_writed = 0;
    while(writed < need_write) {
        now_writed = write(pipe_write, p, need_write - writed);
        if(now_writed > 0) {
            writed += now_writed;
            p += now_writed;
        }
    }
    return 0;
}
/**
 * Функция возращает от кого было прочитано последнее сообщение.
 **/
int get_last_sender() {
    return last_message_from;
}

/** Send a message to the process specified by id.
 *
 * @param self    Any data structure implemented by students to perform I/O
 * @param dst     ID of recepient
 * @param msg     Message to send
 *
 * @return 0 on success, any non-zero value on error
 */
int send(void * self, local_id dst, const Message * msg) {

    struct process_pipes *process = (struct process_pipes*)(self);

    int process_number = get_process_number(process->pipes,
                                            process->process_count);

    if(dst == process_number || dst > process->process_count)
        return -1;
    
    if(write_message(get_write_channel(process->pipes, dst), msg) == -1)
        return -1;
    
    return 0;
}

/** Send multicast message.
 *
 * Send msg to all other processes including parrent.
 * Should stop on the first error.
 * 
 * @param self    Any data structure implemented by students to perform I/O
 * @param msg     Message to multicast.
 *
 * @return 0 on success, any non-zero value on error
 */
int send_multicast(void * self, const Message * msg) {
    struct process_pipes *process = (struct process_pipes*)(self);
    int process_number = get_process_number(process->pipes,
                                            process->process_count);
    // обход всех процессов
    for(int i = 0; i < process->process_count; i++) {
        // не шлём сообщение самом себе
        if(i != process_number) {
            if(send(self, i, msg) == -1) {
                return -1;
            }
        }
    }
    return 0;
}

/** Receive a message from the process specified by id.
 *
 * Might block depending on IPC settings.
 *
 * @param self    Any data structure implemented by students to perform I/O
 * @param from    ID of the process to receive message from
 * @param msg     Message structure allocated by the caller
 *
 * @return 0 on success, any non-zero value on error
 */
int receive(void * self, local_id from, Message * msg) {
        struct process_pipes *process = (struct process_pipes*)(self);

    int process_number = get_process_number(process->pipes,
                                            process->process_count);

    if(from == process_number || from > process->process_count)
        return -1;
    
    if(read_message(get_read_channel(process->pipes, from), msg) == -1) {
        last_message_from = -1;
        return -1;
    } else {
        last_message_from = from;
    } 
    return 0;
}

/** Receive a message from any process.
 *
 * Receive a message from any process, in case of blocking I/O should be used
 * with extra care to avoid deadlocks.
 *
 * @param self    Any data structure implemented by students to perform I/O
 * @param msg     Message structure allocated by the caller
 *
 * @return 0 on success, any non-zero value on error
 */
int receive_any(void * self, Message * msg) {
    struct process_pipes *process = (struct process_pipes*)(self);
    int process_number = get_process_number(process->pipes,
                                            process->process_count);
    // обход всех процессов
    for(int i = 0; i < process->process_count; i++) {
        // не получаем сообщения от самого себя
        if(i != process_number) {
            if(receive(self, i, msg) != -1) {
                last_message_from = i;
                return 0;
            }
        }
    }
    last_message_from = -1;
    return -1;
}
