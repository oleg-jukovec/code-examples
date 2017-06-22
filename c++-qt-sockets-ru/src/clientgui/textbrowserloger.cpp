/*
 * Файл textbrowserloger.cpp
 */
#include "textbrowserloger.h"
// конструирует объект класса
TextBrowserLoger::TextBrowserLoger(QTextBrowser *new_browser, Loger::level lvl)
    : Loger(lvl), browser(new_browser)
{

}
// выводит сообщение в QTextBrowser
void TextBrowserLoger::send(QString message) const
{
    browser->append(message);
}

