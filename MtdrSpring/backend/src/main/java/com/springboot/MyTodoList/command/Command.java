package com.springboot.MyTodoList.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface Command {
    SendMessage execute(long chatId, String messageText, int userId);
}