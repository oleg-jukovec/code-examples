#include <unistd.h>

#include "ipc.h"

#include "pipes.h"

/**
 * Функция записывает сообщение в канал для записи.
 **/ 
int read_message(int pipe_read, Message *msg) {
    if(read(pipe_read, &(msg->s_header), sizeof(MessageHeader)) == -1)
        return -1;
    if(read(pipe_read, msg->s_payload, msg->s_header.s_payload_len) == -1)
        return -1;
    return 0;
}
/**
 * Функция считывает сообщение из канала для записи.
 **/
int write_message(int pipe_write, const Message *msg) {
    if(write(pipe_write, &(msg->s_header), sizeof(MessageHeader)) == -1)
        return -1;
    if(write(pipe_write, msg->s_payload, msg->s_header.s_payload_len) == -1)
        return -1;
    return 0;
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
            if(send(self, i, msg) == -1)
                return -1;
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
    
    if(read_message(get_read_channel(process->pipes, from), msg) == -1)
        return -1;
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
            if(receive(self, i, msg) == -1)
                return -1;
        }
    }
    return 0;
}
