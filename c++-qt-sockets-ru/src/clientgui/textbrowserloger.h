#ifndef TEXTBROWSERLOGER_H
#define TEXTBROWSERLOGER_H
/*
 * Файл textbrowserloger.h
 */
#include <QTextBrowser>

#include "loger.h"
/*
 * Класс TextBrowserLoger расширяет класс Loger для вывода
 * сообщений в объект QTextBrowser
 */
class TextBrowserLoger : public Loger
{
    // ссылка на объект
    QTextBrowser *browser;
public:
    // конструирует объект класса TextBrowserLoger
    TextBrowserLoger(QTextBrowser *browser, Loger::level lvl);
    // виртуальный деструктор
    virtual ~TextBrowserLoger(){}
protected:
    // переопределённая функция
    virtual void send(QString message) const;
};

#endif // TEXTBROWSERLOGER_H
