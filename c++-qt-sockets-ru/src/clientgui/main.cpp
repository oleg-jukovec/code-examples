/*
 * Файл main.cpp
 */
#include <QApplication>
#include "clientgui.h"
// точка входа в программу
int main(int argc, char *argv[])
{
    // инициализируем GUI
    QApplication a(argc, argv);
    ClientGui w;
    w.show();
    // ждём завершения работы GUI
    return a.exec();
}
