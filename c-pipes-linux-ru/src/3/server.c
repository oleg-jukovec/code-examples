#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <string.h>
#include <signal.h>
// общий заголовочный файл для клиента и сервера
#include "public.h"
// максимальный размер буфера чтения/записи
#define BUFFER_SIZE 1024
/*
 * Функция корректно обрабатывает сигналы SIGINT и SIGTERM
 */
void sig_int(int sig) {
    // удаления файла общего канала
    unlink(PUBLIC);
    // завершение провесса
    exit(EXIT_SUCCESS);
}
/*
 * Функция добавляет строку к существующему массиву строк. Возвращает
 * указатель на новый массив строк.
 */
char** add_string(char **strings, int strings_size, char *string) {
    // выделение памяти под новый массив указателей строк
    char** new_strings = malloc(sizeof(char*) * (strings_size + 1));
    int i;
    // копирование указателей существующих строк
    for(i = 0; i < strings_size; i++) {
        new_strings[i] = strings[i];
    }
    // удаления массива указателей на строки
    if(strings) {
        free(strings);
    }
    // выделение памяти для новой строки
    new_strings[strings_size] = malloc(sizeof(char) * (strlen(string) + 1));
    // копирование строки
    for(i = 0; i < strlen(string) + 1; i++) {
        new_strings[strings_size][i] = string[i];
    }
    // возврат нового массива
    return new_strings;
}
/**
 * Программа-сервер:
 * 1. Читает строки из входного потока.
 * 2. Добавляет номера страник каждые 24 прочитанные строки.
 * 3. Ожидает, пока клиент передаст имя файла приватного канала.
 * 4. Записывает массив прочитанный строк в приватный канал.
 * 5. Пункты 3-4 в бесконечном цикле. Может последовательно передавать
 * массив прочитанный строк разным клиентам.
 *
 * Программу можно завершить комбинацией клавиш Ctrl+C,при этом файл именованного
 * канала удалится.
 *
 * ПОДРОБНЕЕ - КОМПИЛЯЦИЯ И ЗАПУСК - в файле public.h !!!!!!!!!!!!!!!!!!!!!!!!!!!
 */
int main() {
    // установка обработчика сигналов SIGINT и SIGTERM
    // (необходимо для удаления файла именнованного канала при завершении)
    signal(SIGINT, sig_int);
    signal(SIGTERM, sig_int);
    // массив прочитанных строк
    char **strings   = 0;
    // размер массива прочитанных строк
    int strings_size = 0;
    // буфер для чтения/записи
    char buffer[BUFFER_SIZE];
    char page_str[BUFFER_SIZE];
    // читаем строку из входного потока
    int num = 0;
    while(fgets(buffer, BUFFER_SIZE, stdin)) {
        // если номер строки делится на 24 без остатка
        if(num % 24 == 0) {
            // вставляем номер страницы
            sprintf(page_str, "PAGE %d\n", num / 24 + 1);
            strings = add_string(strings, strings_size, page_str);
            strings_size++;
        }
        // добавляем её к массиву прочитанных строк
        strings = add_string(strings, strings_size, buffer);
        strings_size++;
        num++;
    }
    // создание общего канала связи
    if(mkfifo(PUBLIC, 0666) == -1) {
        // если не удалось
        printf("Невозможно создать общий канал.\n");
        printf("Возможно следует удалить"
                 " вручную файл %s\n", PUBLIC);
        exit(EXIT_FAILURE);
    }
    // дескриптор файла для чтения из общего канала
    int in;
    // дескриптор файла для записи в приватный канал
    int out;
    // итератор
    int i;
    // читаемое сообщение
    message message;
    for(;;) {
        // пытаемся открыть общий канал для чтения
        // выполнение процесса блокируется, пока другой процесс
        // не откроет этот канал для записи
        if((in = open(PUBLIC, O_RDONLY)) == -1) {
            // в случае неудачи
            printf("Ошибка открытия общего канала.\n");
            exit(EXIT_FAILURE);
        }
        // читаем сообщение
        read(in, &message, sizeof(message));
        // закрываем канал
        close(in);
        // пытаемся открыть приватный канал для записи в него
        if((out = open(message.file_name, O_WRONLY)) == -1) {
            // в случае неудачи
            printf("Ошибка открытия приватного канала для записи.\n");
        }
        // пишем массив прочитанных слов в прнватный канал
        for(i = 0; i < strings_size; i++) {
            write(out, strings[i], strlen(strings[i]) * sizeof(char));
        }
        // закрываем приватный канал для записи
        close(out);
    }
}
